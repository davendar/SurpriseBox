package com.inmobi.surprise.lib.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by davendar.ojha on 7/20/16.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == intent || null == intent.getAction()) {
            return;
        }
        Intent notificationService = new Intent(context, NotificationService.class);
        notificationService.setAction(intent.getAction());
        context.startService(notificationService);
    }
}
