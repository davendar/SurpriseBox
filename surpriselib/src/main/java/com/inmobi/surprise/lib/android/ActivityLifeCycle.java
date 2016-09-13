package com.inmobi.surprise.lib.android;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by davendar.ojha on 7/20/16.
 */
public class ActivityLifeCycle implements Application.ActivityLifecycleCallbacks {


    private AtomicInteger visible = new AtomicInteger(0);

    private AtomicInteger foreground = new AtomicInteger(0);


    public boolean isAppVisible() {
        return visible.get() > 0;
    }

    public boolean isAppInForeground() {
        return foreground.get() > 0;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        foreground.incrementAndGet();
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        visible.incrementAndGet();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (visible.get() > 0) {
            visible.decrementAndGet();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        foreground.decrementAndGet();
    }
}
