package com.inmobi.surprise.lib.android;

import com.inmobi.surprise.lib.ads.AdService;
import com.inmobi.surprise.lib.ads.AssetService;
import com.inmobi.surprise.lib.ads.SlotManager;
import com.inmobi.surprise.lib.location.GmsLocationManager;
import com.inmobi.surprise.lib.location.LocationManager;
import com.inmobi.surprise.lib.notification.NotificationScheduler;
import com.inmobi.surprise.lib.util.AndroidUtils;
import com.inmobi.surprise.lib.util.Constants;
import com.inmobi.surprise.lib.util.OkHttp3Downloader;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import javax.inject.Singleton;

/**
 * Created by davendar.ojha on 7/5/16.
 */
@Module
public class AppModule {

    private AdService adService;
    private SharedPreferences sharedPreferences;
    private SlotManager slotManager;
    private ActivityLifeCycle activityLifeCycle;
    private NotificationScheduler notificationScheduler;
    private Context context;
    private AssetService assetService;
    private LocationManager locationManager;


    public AppModule(Context givenContext) {
        this.context = givenContext.getApplicationContext();
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        slotManager = new SlotManager();
        locationManager = new GmsLocationManager(context);
        locationManager.start();

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().header("Cache-Control", "max-age=" + (60 * 60 * 24 * 30)).build();
            }
        });

        File cacheFile = new File(context.getFilesDir(), "imageCache");
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        okHttpBuilder.cache(new Cache(cacheFile, AndroidUtils.calculateDiskCacheSize(cacheFile)));
        OkHttp3Downloader okHttpDownloader = new OkHttp3Downloader(okHttpBuilder.build());
        Picasso.setSingletonInstance(new Picasso.Builder(context).downloader(okHttpDownloader).build());
        assetService = new AssetService(context);
        adService = new AdService(context, slotManager, assetService, locationManager);
        activityLifeCycle = new ActivityLifeCycle();
        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(activityLifeCycle);
        notificationScheduler = new NotificationScheduler(context, sharedPreferences);
    }


    @Provides
    @Singleton
    public AdService providesAdService() {
        return adService;
    }

    @Provides
    @Singleton
    public SharedPreferences providesSharedPreferences() {
        return sharedPreferences;
    }

    @Provides
    @Singleton
    public SlotManager providesSlotManager() {
        return slotManager;
    }

    @Provides
    @Singleton
    public ActivityLifeCycle provideActivityLifeCycle() {
        return activityLifeCycle;
    }

    @Provides
    @Singleton
    public NotificationScheduler provideNotificationScheduler() {
        return notificationScheduler;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    public AssetService provideAssetService() {
        return assetService;
    }
}
