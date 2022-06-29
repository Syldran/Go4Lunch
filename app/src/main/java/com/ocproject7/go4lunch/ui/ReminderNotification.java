package com.ocproject7.go4lunch.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ocproject7.go4lunch.R;

public class ReminderNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyGo4Lunch")
                .setSmallIcon(R.drawable.ic_add_alert_24)
                .setContentTitle("Restaurant Reminder")
                .setContentText("You'll be eating at RESTAURANT CHOSEN")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());
    }
}
