package com.example.myapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationType = intent.getStringExtra("NOTIFICATION_TYPE");

        if (notificationType != null) {
            if (notificationType.equals("daily")) {
                showNotification(context, "Daily Notification", "This is your daily notification.");
            } else if (notificationType.equals("monthly")) {
                showNotification(context, "Monthly Notification", "This is your monthly notification.");
            }
        }
    }

    private void showNotification(Context context, String title, String message) {
        Intent notificationIntent = new Intent(context, HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "daily_channel")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.notificationsicon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }

}
