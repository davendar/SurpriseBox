package com.inmobi.surprise.lib.android;

import com.inmobi.surprise.lib.R;
import com.inmobi.surprise.lib.ads.AdService;
import com.inmobi.surprise.lib.ads.AdServiceInitializedCallback;
import com.inmobi.surprise.lib.notification.NotificationScheduler;
import com.inmobi.surprise.lib.util.Constants;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

/**
 * Created by davendar.ojha on 7/5/16.
 */
public class AdPreloadActivity extends Activity implements AdServiceInitializedCallback {

    @Inject
    AdService adService;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    NotificationScheduler notificationScheduler;


    private RelativeLayout noInternetParent;
    private ImageView progressBar;
    private RelativeLayout rlCuratingParent;
    private RelativeLayout rlRetryParent;
    private NetworkReceiver receiver = new NetworkReceiver();
    private long resumeTime;
    private RotateAnimation rotate;
    private Handler handler;
    private Handler retryHandler;
    private boolean isVisible = false;
    private Button btnRetry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Injectors.initialize(getApplicationContext());
        Injectors.APP_COMPONENT.inject(AdPreloadActivity.this);
        noInternetParent = (RelativeLayout) findViewById(R.id.NoInternetParent);
        progressBar = (ImageView) findViewById(R.id.ivProgress);
        rlCuratingParent = (RelativeLayout) findViewById(R.id.rlCuratingParent);
        rlRetryParent = (RelativeLayout) findViewById(R.id.rlRetryParent);
        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectivityWork(getConnectivityStatus(AdPreloadActivity.this));
            }
        });
        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(1200);
        rotate.setInterpolator(new LinearInterpolator());
        handler = new Handler(Looper.getMainLooper());
        retryHandler = new Handler(Looper.getMainLooper());
    }


    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        resumeTime = System.currentTimeMillis();
        receiver = new NetworkReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        updateAppStatus();
        connectivityWork(getConnectivityStatus(AdPreloadActivity.this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
        removeReceiver();
    }

    private void removeReceiver() {
        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception e) {
                //log
            }
        }
    }

    private boolean getConnectivityStatus(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    private void connectivityWork(final boolean connected) {
        Log.e("Splash", "Working in state of " + connected);
        retryHandler.removeCallbacksAndMessages(null);
        if (!connected) {
            rlCuratingParent.setVisibility(View.GONE);
            rlRetryParent.setVisibility(View.GONE);
            noInternetParent.setVisibility(View.VISIBLE);
        } else {
            noInternetParent.setVisibility(View.GONE);
            rlRetryParent.setVisibility(View.GONE);
            rlCuratingParent.setVisibility(View.VISIBLE);
            rotate.reset();
            progressBar.startAnimation(rotate);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adService.startService(AdPreloadActivity.this);
                    retryHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rlRetryParent.setVisibility(View.VISIBLE);
                            rlCuratingParent.setVisibility(View.GONE);
                            noInternetParent.setVisibility(View.GONE);
                        }
                    }, TimeUnit.SECONDS.toMillis(30));
                }
            }, 100);
        }
    }

    @Override
    public void serviceInitialized() {
        AdPreloadActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && isVisible) {
                    Intent appIntent = new Intent(AdPreloadActivity.this, MainActivity.class);
                    appIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(appIntent);
                    finish();
                    overridePendingTransition(0, 0);
                    try {
                        rotate.cancel();
                        removeReceiver();
                    } catch (Exception e) {
                        //DO NOTHING
                    }
                }
            }
        });

    }

    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (System.currentTimeMillis() - resumeTime > 2000 && getConnectivityStatus(context)) {
                Log.e("SPLASH", "NetworkReceiver Success");
                connectivityWork(true);
            }

        }
    }

    private void updateAppStatus() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIF_ID);
        sharedPreferences.edit().putLong(Constants.LAST_APP_OPEN_TIME, System.currentTimeMillis()).apply();
        notificationScheduler.scheduleNotificationService();
    }
}
