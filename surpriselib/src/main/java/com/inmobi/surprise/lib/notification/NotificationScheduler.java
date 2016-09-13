package com.inmobi.surprise.lib.notification;

import com.inmobi.surprise.lib.util.Constants;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.concurrent.TimeUnit;

/**
 * Created by davendar.ojha on 7/20/16.
 */
public class NotificationScheduler {

    private Context context;
    private SharedPreferences sharedPreferences;

    public NotificationScheduler(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    public void scheduleNotificationService() {
        long currentTime = System.currentTimeMillis();
        long interval = TimeUnit.HOURS.toMillis(1);
        boolean isLimited = sharedPreferences.getBoolean(Constants.LIMIT_NOTIF, false);
        if (isLimited) {
            interval = TimeUnit.HOURS.toMillis(6);
        }
        long alarmTime = currentTime + interval;
        Intent notificationBroadcastIntent = new Intent(context, NotificationReceiver.class);
        notificationBroadcastIntent.setAction(Constants.CREATE_NOTIFICATION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, notificationBroadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager notificationAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            notificationAlarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pi);
        } else {
            notificationAlarm.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
        }
    }
}

