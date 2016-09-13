package com.inmobi.surprise.lib.android.login;

import com.inmobi.surprise.lib.R;
import com.inmobi.surprise.lib.android.Injectors;
import com.inmobi.surprise.lib.notification.NotificationScheduler;
import com.inmobi.surprise.lib.util.CirclePageIndicator;
import com.inmobi.surprise.lib.util.Constants;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

/**
 * Created by davendar.ojha on 7/6/16.
 */
public class LoginActivity extends FragmentActivity implements SexPrefChangeCallback {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    NotificationScheduler notificationScheduler;

    private ViewPager detailPager;
    private CirclePageIndicator mIndicator;
    private LoginAdapter loginAdapter;
    private ImageView loginBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Injectors.initialize(getApplicationContext());
        Injectors.APP_COMPONENT.inject(this);
        loginBackground = (ImageView) findViewById(R.id.loginBackground);
        detailPager = (ViewPager) findViewById(R.id.detailPager);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        loginAdapter = new LoginAdapter(getSupportFragmentManager());
        detailPager.setAdapter(loginAdapter);
        detailPager.setOffscreenPageLimit(2);
        mIndicator.setViewPager(detailPager);
        detailPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        Picasso.with(LoginActivity.this).load(R.drawable.second_frag_background).into(loginBackground);
                        mIndicator.setVisibility(ViewPager.VISIBLE);
                        break;
                    default:
                        boolean isFemale = sharedPreferences.getBoolean(Constants.SEX_PREF, Constants.DEFAULT_SEX_PREF);
                        onSexPrefChanged(isFemale);
                        mIndicator.setVisibility(ViewPager.GONE);
                        break;
                }
                mIndicator.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAppStatus();
    }


    @Override
    public void onSexPrefChanged(boolean isFemale) {
        if (isFemale) {
            Picasso.with(LoginActivity.this).load(R.drawable.third_frag_background_female)
                    .into(loginBackground, new Callback() {
                        @Override
                        public void onSuccess() {
                            Animation fadeOut = new AlphaAnimation(0.5f, 1);
                            fadeOut.setInterpolator(new AccelerateInterpolator());
                            fadeOut.setDuration(200);
                            loginBackground.startAnimation(fadeOut);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        } else {
            Picasso.with(LoginActivity.this).load(R.drawable.third_frag_background_male)
                    .into(loginBackground, new Callback() {
                        @Override
                        public void onSuccess() {
                            Animation fadeOut = new AlphaAnimation(0.5f, 1);
                            fadeOut.setInterpolator(new AccelerateInterpolator());
                            fadeOut.setDuration(200);
                            loginBackground.startAnimation(fadeOut);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }

    private void updateAppStatus() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIF_ID);
        sharedPreferences.edit().putLong(Constants.LAST_APP_OPEN_TIME, System.currentTimeMillis()).apply();
        notificationScheduler.scheduleNotificationService();
    }
}
