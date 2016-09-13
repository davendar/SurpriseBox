package com.inmobi.surprise.lib.ads;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.sdk.InMobiSdk;
import com.inmobi.surprise.lib.location.LocationException;
import com.inmobi.surprise.lib.location.LocationManager;
import com.inmobi.surprise.lib.util.AndroidUtils;
import com.inmobi.surprise.lib.util.Constants;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.facebook.ads.AdSettings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by davendar.ojha on 7/4/16.
 */
public class AdService implements AdListener, AssetFetchListener {
    WeakReference<AdConsumer> adConsumer;
    WeakReference<AdServiceInitializedCallback> callback;


    AppAdFetcher appAdFetcher;
    BrandAdFetcher brandAdFetcher;
    CommerceAdFetcher commerceAdFetcher;
    SponsoredAdFetcher sponsoredAdFetcher;
    FanAdFetcher fanAdFetcher;


    List<SurpriseAd> drainedList;

    Handler uiHandler;
    SlottingTask slottingTask;
    MinimalAdFetcher minimalAdFetcher;
    Timer slottingTimer;
    Timer minimalAdTimer;


    SlotManager slotManager;
    AtomicInteger enqueueCount = new AtomicInteger(0);
    int forceAdFetchCount = 0;

    private ExecutorService requestPool;
    private ExecutorService responsePool;

    private Runnable requestRunnable;
    private Runnable appAdFetcherRunnable;
    private Runnable brandAdFetcherRunnable;
    //private Runnable commerceAdFetcherRunnable;
    //  private Runnable sponseredAdFetcherRunnable;

    private AdStoreMem adStoreMem;
    private AssetService assetService;
    private LocationManager locationManager;
    private WeakReference<Context> context;

    private AtomicBoolean serviceStarted = new AtomicBoolean(false);


    public AdService(Context context, SlotManager slotManager, AssetService assetService, LocationManager locationManager) {
        this.context = new WeakReference<>(context);
        this.slotManager = slotManager;
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.assetService = assetService;
        fanAdFetcher = new FanAdFetcher(context, this);
        appAdFetcher = new AppAdFetcher(this);
        brandAdFetcher = new BrandAdFetcher(this);
        commerceAdFetcher = new CommerceAdFetcher(this);
        sponsoredAdFetcher = new SponsoredAdFetcher(this);
        drainedList = new ArrayList<>();
        adStoreMem = new AdStoreMem();
        this.locationManager = locationManager;
        setupAdRunnable();
    }


    public void fetchAd(AdConsumer adConsumer) {
        try {
            if (!serviceStarted.get()) {
                startService(null);
            }
            this.adConsumer = new WeakReference<>(adConsumer);
            requestPool.execute(requestRunnable);
        } catch (Exception e) {
            //Todo Log
        }
    }

    public void startService(AdServiceInitializedCallback callback) {
        serviceStarted.set(true);
        InMobiSdk.init(this.context.get(), Constants.APPLICATION_ID);
        AdSettings.isTestMode(this.context.get());
        //Log.e(AdService.class.getName(), "AdService Started " + System.currentTimeMillis());
        this.callback = new WeakReference<>(callback);
        this.adConsumer = null;
        forceAdFetchCount = 0;
        adStoreMem.clearMemory();
        fanAdFetcher.startFanAdFetcher();
        slotManager.resetSlotManager();
        requestPool = Executors.newSingleThreadExecutor();
        responsePool = Executors.newCachedThreadPool();
        createAndScheduleMinimalAdTask();
        coldStart(4);
    }

    public void stopService() {
        try {
            serviceStarted.set(false);
            AndroidUtils.shutdownAndAwaitTermination(requestPool, 0);
            AndroidUtils.shutdownAndAwaitTermination(responsePool, 0);
            fanAdFetcher.stopFanAdFetcher();
            adStoreMem.clearMemory();
            slotManager.resetSlotManager();
            purgeSlottingTask();
            purgeMinimalAdTask();
            forceAdFetchCount = 0;
            callback = null;
            adConsumer = null;
            requestPool = null;
            responsePool = null;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void createAndScheduleSlottingTask() {
        slottingTimer = new Timer();
        slottingTask = new SlottingTask();
        slottingTimer.schedule(slottingTask, 0, 1500);
    }

    public void purgeSlottingTask() {
        try {
            slottingTimer.cancel();
            slottingTimer.purge();
            slottingTimer = null;
            slottingTask = null;
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void createAndScheduleMinimalAdTask() {
        minimalAdTimer = new Timer();
        minimalAdFetcher = new MinimalAdFetcher();
        minimalAdTimer.schedule(minimalAdFetcher, 0, 1500);
    }

    public void purgeMinimalAdTask() {
        try {
            minimalAdTimer.cancel();
            minimalAdTimer.purge();
            minimalAdTimer = null;
            minimalAdFetcher = null;
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    void internalFetch() {
        try {
            android.location.Location location = locationManager.getLastKnownLocation();
            if (null != location) {
                InMobiSdk.setLocation(location);
            }
        } catch (LocationException e) {
            //TODO log
        }
        uiHandler.post(appAdFetcherRunnable);
        uiHandler.postDelayed(brandAdFetcherRunnable, 10);
        askFan(context.get(), 30);
        // uiHandler.postDelayed(commerceAdFetcherRunnable, 20);
        //uiHandler.postDelayed(sponseredAdFetcherRunnable, 40);
    }

    private void askFan(Context context, int waitPeriod) {
        if (null != context && AndroidUtils.isFaceBookInstalled(context)) {
            fanAdFetcher.fetchAds(waitPeriod);
        }
    }

    private void coldStart(int loopCount) {
        for (int i = 0; i < loopCount; i++) {
            internalFetch();
        }
    }

    @Override
    public void adLoaded(final SurpriseAd surpriseAd) {
        try {
            responsePool.execute(new Runnable() {
                @Override
                public void run() {
                    assetService.cacheAsset(surpriseAd, AdService.this);
                }
            });
        } catch (Exception e) {
            //TODO LOG
        }
    }

    @Override
    public void adFailed(int type, InMobiAdRequestStatus inMobiAdRequestStatus) {
        //Log.e("AdService", "Failed to load ad. " + type + " msg " + inMobiAdRequestStatus.getMessage());
        if (type != AdType.FAN_AD) {
            askFan(context.get(), 0);
        }
    }

    @Override
    public void onAssetFetched(final SurpriseAd surpriseAd) {
        try {
            responsePool.execute(new Runnable() {
                @Override
                public void run() {
                    adStoreMem.storeAd(surpriseAd);
                    if (null != callback && null != callback.get()) {
                        synchronized (AdService.class) {
                            if (null != callback && null != callback.get()) {
                                try {
                                    callback.get().serviceInitialized();
                                    callback = null;
                                } catch (Exception e) {
                                    //Log.e("AdService", "err" + e);
                                }
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }


    class SlottingTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (null != adConsumer && null != adConsumer.get()) {
                    drainedList.clear();
                    adStoreMem.getBufferAds(drainedList);
                    //Log.e("QWERT", "drained list size" + drainedList.size());
                    if (drainedList.size() > 0) {
                        drainedList = slotManager.arrangeAccordingToSlot(drainedList);
                        adConsumer.get().adAvailable(drainedList);
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }

        }
    }


    class MinimalAdFetcher extends TimerTask {

        @Override
        public void run() {
            if (!enqueueCount.compareAndSet(0, 0)) {
                enqueueCount.decrementAndGet();
                internalFetch();
            } else if (!adStoreMem.isEnoughAdsInStore()) {
                forceAdFetchCount++;
                if (forceAdFetchCount % 2 == 0 && forceAdFetchCount < 20) {
                    internalFetch();
                }
            }
        }
    }

    private void setupAdRunnable() {
        appAdFetcherRunnable = new Runnable() {
            @Override
            public void run() {
                appAdFetcher.fetchAds();
            }
        };
        brandAdFetcherRunnable = new Runnable() {
            @Override
            public void run() {
                brandAdFetcher.fetchAds();
            }
        };
//        commerceAdFetcherRunnable = new Runnable() {
//            @Override
//            public void run() {
//                commerceAdFetcher.fetchAds();
//            }
//        };
//        sponseredAdFetcherRunnable = new Runnable() {
//            @Override
//            public void run() {
//                sponsoredAdFetcher.fetchAds();
//            }
//        };
        requestRunnable = new Runnable() {
            @Override
            public void run() {
                if (null == slottingTask || null == slottingTimer) {
                    createAndScheduleSlottingTask();
                }
                if (enqueueCount.get() < 1) {
                    enqueueCount.incrementAndGet();
                }
            }
        };
    }


}
