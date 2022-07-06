package com.ocproject7.go4lunch.utils;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ocproject7.go4lunch.ui.ReminderNotification;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;

import java.util.Calendar;

public abstract class Utils {
    private static final String TAG = "TAG_Utils";


    public static void loadImage(Context context, String url, ImageView view) {
        Glide.with(context).load(url).apply(RequestOptions.centerCropTransform()).into(view);
    }

    public static void notifyGo4Lunch(String message, Context context, boolean isActive) {
        Intent intent = new Intent(context, ReminderNotification.class);
        intent.putExtra("MESSAGE", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (!isActive) {
            Log.d(TAG, "notifyGo4Lunch: notif désactivé");
            alarmManager.cancel(pendingIntent);
        } else {
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }
            Log.d(TAG, "notifyGo4Lunch: notif activé");
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
