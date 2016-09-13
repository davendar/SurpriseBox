package com.inmobi.surprise;

import com.inmobi.surprise.lib.android.Injectors;

import android.app.Application;
import android.util.Log;
//import com.squareup.leakcanary.LeakCanary;

/**
 * Created by davendar.ojha on 8/4/16.
 */
public class MainApplication extends Application {

    static {
        final Thread.UncaughtExceptionHandler uncaughtExceptionHandler =
                Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExHandler(uncaughtExceptionHandler));
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }


    private static class UncaughtExHandler implements Thread.UncaughtExceptionHandler {
        private final Thread.UncaughtExceptionHandler replaced;
        private static final String TAG = "UncaughtExHandler";

        public UncaughtExHandler(Thread.UncaughtExceptionHandler replaced) {
            this.replaced = replaced;
        }

        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e("UncaughtExHandler", thread.getId() + "||" + thread.getName() + "||" + thread.getPriority(), ex);
        }
    }
}
