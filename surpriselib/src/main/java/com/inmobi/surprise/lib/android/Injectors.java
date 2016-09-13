package com.inmobi.surprise.lib.android;

import com.inmobi.surprise.lib.android.DaggerAppComponent;

import android.content.Context;

public class Injectors {

    public static volatile AppComponent APP_COMPONENT;

    public static void initialize(final Context context) {
        if (APP_COMPONENT == null) {
            synchronized (Injectors.class) {
                if (APP_COMPONENT == null) {
                    APP_COMPONENT = DaggerAppComponent.builder()
                            .appModule(new AppModule(context))
                            .build();
                }
            }
        }
    }

}