package com.inmobi.surprise.lib.notification;

import com.inmobi.surprise.lib.R;
import com.inmobi.surprise.lib.android.ActivityLifeCycle;
import com.inmobi.surprise.lib.android.Injectors;
import com.inmobi.surprise.lib.android.SplashActivity;
import com.inmobi.surprise.lib.util.Constants;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

/**
 * Created by davendar.ojha on 7/20/16.
 */
public class NotificationService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */


    @Inject
    Context context;

    @Inject
    SharedPreferences sharedPreferences;


    @Inject
    ActivityLifeCycle activityLifeCycle;


    @Inject
    NotificationScheduler notificationScheduler;


    public NotificationService(String name) {
        super(name);
    }

    public NotificationService() {
        super("NotificationService");
    }

    private final String IS_BOOT_COMPLETE_PROCESSED = "boot_complete";

    private final String LAST_WORK_TIME = "last_work_time";

    private final long MINIMUM_WORK_DELAY = TimeUnit.MINUTES.toMillis(5);

    private final String imageUrl = "https://s3-ap-southeast-1.amazonaws.com/inmobi-surpriseme/notification/notif.jpg";
    private final String imageurl1 = "https://s3-ap-southeast-1.amazonaws.com/inmobi-surpriseme/notification/notif2.jpg";

    @Override
    protected void onHandleIntent(Intent intent) {
        Injectors.initialize(getApplicationContext());
        Injectors.APP_COMPONENT.inject(this);
        //update shared preferences on boot completed
        if (null == intent || null == intent.getAction()) {
            //ToDo log
        } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            connectivityWork();
        } else if (intent.getAction().equals(Constants.CREATE_NOTIFICATION)) {
            sharedPreferences.edit().putBoolean(IS_BOOT_COMPLETE_PROCESSED, false)
                    .putLong(Constants.BOOT_COMPLETE_TIME, System.currentTimeMillis())
                    .putLong(LAST_WORK_TIME, 0)
                    .apply();
            forceNotification();
        }
    }

    private void connectivityWork() {
        boolean isConnected = getConnectivityStatus(context);
        if (!isConnected) {
            return;
        }
        boolean isBootCompleteProcessed = sharedPreferences.getBoolean(IS_BOOT_COMPLETE_PROCESSED, true);
        boolean isAlreadyWorking = isAlreadyWorking();
        if (!isBootCompleteProcessed && !isAlreadyWorking && !isAlreadyOpened()) {
            updateWorkTime();
            sendRichNotification(null);
        }
    }

    private void forceNotification() {
        boolean isConnected = getConnectivityStatus(context);
        if (!isConnected) {
            return;
        }
        if (activityLifeCycle.isAppVisible()) {
            Log.e("NotifService", "yupppp App Visible");
            return;
        }
        Log.e("NotifService", "No App not visible");
        updateWorkTime();
        sendRichNotification(null);
    }

    private boolean getConnectivityStatus(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void updateWorkTime() {
        sharedPreferences.edit().putLong(LAST_WORK_TIME,
                System.currentTimeMillis()).apply();
    }

    private boolean isAlreadyWorking() {
        long currTime = System.currentTimeMillis();
        long lastWorkTime = sharedPreferences.getLong(LAST_WORK_TIME, 0);
        if ((currTime - lastWorkTime) < MINIMUM_WORK_DELAY) {
            Log.d("Notification", "Notifcreate event - Enforcing Minimum work delay enforced");
            return true;
        }
        return false;
    }

    private boolean isAlreadyOpened() {
        long boot_complete_time = sharedPreferences.getLong(Constants.BOOT_COMPLETE_TIME, 0);
        long last_app_opened_time = sharedPreferences.getLong(Constants.LAST_APP_OPEN_TIME, 0);
        if (last_app_opened_time > boot_complete_time) {
            return true;
        }
        return false;
    }

    private void sendRichNotification(String appTitle) {

        Intent mainIntent = new Intent(context, SplashActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.app_logo);
        if (null == appTitle) {
            mBuilder.setContentTitle(context.getResources().getString(R.string.app_name));
        } else {
            mBuilder.setContentTitle(appTitle);
        }
        mBuilder.setContentText(context.getResources().getString(R.string.notif_content));
        mBuilder.setColor(context.getResources().getColor(R.color.notification_background));
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setContentIntent(pIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setTicker(context.getResources().getString(R.string.app_name));
        setImage(mBuilder);
        NotificationManager mNotificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Constants.NOTIF_ID, mBuilder.build());
        sharedPreferences.edit().putBoolean(IS_BOOT_COMPLETE_PROCESSED, true).apply();
        notificationScheduler.scheduleNotificationService();
    }

    private void setImage(NotificationCompat.Builder mBuilder) {
        int random = (int) (System.currentTimeMillis() % 2);
        String finalUrl;
        if (random == 0) {
            finalUrl = imageUrl;
        } else {
            finalUrl = imageurl1;
        }
        try {
            Bitmap bitmap = Picasso.with(context).load(finalUrl).get();
            if (bitmap != null) {
                mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
            }
        } catch (Exception e) {
            Log.e("NotifService", "Unable to get image for notification error " + e);
        }
    }

}
