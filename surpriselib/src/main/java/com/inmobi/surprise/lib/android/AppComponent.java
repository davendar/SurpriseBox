package com.inmobi.surprise.lib.android;

import com.inmobi.surprise.lib.android.login.LoginActivity;
import com.inmobi.surprise.lib.android.login.ThirdFragment;
import com.inmobi.surprise.lib.notification.NotificationService;

import dagger.Component;

import javax.inject.Singleton;

/**
 * Created by davendar.ojha on 7/5/16.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(AdPreloadActivity adPreloadActivity);

    void inject(MainActivity activity);

    void inject(LoginActivity loginActivity);

    void inject(ThirdFragment thirdFragment);

    void inject(SplashActivity splashActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(NotificationService notificationService);
}
