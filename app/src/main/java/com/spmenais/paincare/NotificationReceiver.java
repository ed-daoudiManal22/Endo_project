package com.spmenais.paincare;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");

        Log.d(TAG, "Received notification request");
        Log.d(TAG, "Name: " + name);
        Log.d(TAG, "Description: " + description);

        Intent repeating_intent = new Intent(context, NotifiactionSettings_Activity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, repeating_intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Notification")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notificationsicon)
                .setColor(ContextCompat.getColor(context, R.color.pink))
                .setContentTitle(name) // Use the name as the title
                .setContentText(description) // Use the description as the text
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());

        Log.d(TAG, "Notification shown");

    }
}
