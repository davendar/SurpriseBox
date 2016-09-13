package com.inmobi.surprise.lib.ads;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.surprise.lib.util.AndroidUtils;
import com.inmobi.surprise.lib.util.Constants;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by davendar.ojha on 7/5/16.
 */
public class FanAdFetcher implements NativeAdsManager.Listener, AdFetcher {
    private AdListener adListener;
    private NativeAdsManager manager;
    private Handler uiHandler;
    private AtomicInteger requestStatus = new AtomicInteger(-1);
    private ExecutorService requestPool;
    private Runnable lastEffortRunnable;
    private Runnable failedFetchRunnable;
    private Runnable adFetchRunnable;


    public FanAdFetcher(Context context, AdListener adListener) {
        this.adListener = adListener;
        manager = new NativeAdsManager(context, Constants.FAN_P_ID, 10);
        manager.setListener(this);
        this.uiHandler = new Handler(Looper.getMainLooper());
        setupFinalEffortRunnable();
        setupFailedFetch();
        setupAdFetchRunnable();
    }

    public void startFanAdFetcher() {
        requestPool = Executors.newFixedThreadPool(Constants.THREAD_POOL_SIZE);
        postForRefresh();
    }

    public void stopFanAdFetcher() {
        AndroidUtils.shutdownAndAwaitTermination(requestPool, 0);
    }

    private void postForRefresh() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                manager.loadAds();
            }
        });
    }


    @Override
    public void fetchAds() {
        fetchAds(30L);
    }

    public void fetchAds(long waitPeriod) {
        try {
            uiHandler.postDelayed(adFetchRunnable, waitPeriod);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void spinLock() {
        try {
            int count = 0;
            while (requestStatus.get() == -1 && count < 100) {
                Thread.sleep(100);
                count++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAdsLoaded() {
        requestStatus.set(0);
    }

    @Override
    public void onAdError(AdError adError) {
        requestStatus.set(1);
    }

    private void setupFinalEffortRunnable() {
        lastEffortRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    NativeAd freshAd = manager.nextNativeAd();
                    if (null != freshAd) {
                        adListener.adLoaded(new SurpriseAd(freshAd, AdType.FAN_AD));
                    } else {
                        adListener.adFailed(AdType.FAN_AD, new InMobiAdRequestStatus(InMobiAdRequestStatus.StatusCode.NO_FILL));
                        postForRefresh();
                    }
                } catch (Exception e) {
                    //Todo Log
                }
            }
        };
    }

    private void setupFailedFetch() {
        failedFetchRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (requestStatus.get() == -1) {
                        spinLock();
                        uiHandler.post(lastEffortRunnable);
                    } else {
                        adListener.adFailed(AdType.FAN_AD, new InMobiAdRequestStatus(InMobiAdRequestStatus.StatusCode.INTERNAL_ERROR));
                        postForRefresh();
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        };
    }

    private void setupAdFetchRunnable() {
        adFetchRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    NativeAd nativeAd = manager.nextNativeAd();
                    if (null == nativeAd) {
                        requestPool.execute(failedFetchRunnable);
                    } else {
                        adListener.adLoaded(new SurpriseAd(nativeAd, AdType.FAN_AD));
                    }
                } catch (Exception e) {
                    //Todo Log
                }
            }
        };
    }
}
