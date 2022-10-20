package com.ocproject7.go4lunch.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.ocproject7.go4lunch.R;


public class ReminderNotification extends BroadcastReceiver {

    private static String TAG = "TAG_ReminderNotification";

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("MESSAGE");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyGo4Lunch")
                .setSmallIcon(R.drawable.ic_add_alert_24)
                .setContentTitle("Restaurant Reminder")
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());
    }
}
