package com.example.myapplication;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Calendar;

public class NotifiactionSettings_Activity extends AppCompatActivity {
    PendingIntent pending_intent;
    AlarmManager alarm_manager;
    private ImageView leftIcon;
    private boolean isCommunityNotificationOn = false;
    private ListenerRegistration blogsListenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings);
        leftIcon = findViewById(R.id.leftIcon);

        notification_channel();
        pending_intent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(this, NotificationReceiver.class), PendingIntent.FLAG_IMMUTABLE);
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        SwitchMaterial CommunityNotif = findViewById(R.id.communityNotifiSwitch);
        SwitchMaterial TestNotif = findViewById(R.id.DiagTestSwitch);
        SwitchMaterial test = findViewById(R.id.oneMinut);

        // Assuming you have a reference to your Firestore collection "Blogs"
        CollectionReference blogsCollection = FirebaseFirestore.getInstance().collection("Blogs");

        // Set up a snapshot listener for "Blogs" collection
        blogsListenerRegistration = blogsCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore Listener", "Error listening for new blogs", error);
                return;
            }

            if (isCommunityNotificationOn) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        // A new blog has been added, trigger the notification
                        set_notification_alarm(0, "New Blog Added", "A member has added a new blog");
                        // Show a toast message
                        Toast.makeText(NotifiactionSettings_Activity.this, "Community notifications ON", Toast.LENGTH_SHORT).show();
                        // Exit the loop after the first added document is found
                        break;
                    }
                }
            }
        });

        // CommunityNotif switch click listener
        CommunityNotif.setOnClickListener(e -> {
            isCommunityNotificationOn = CommunityNotif.isChecked();
            if (!isCommunityNotificationOn) {
                cancel_notification_alarm();
                // Show a toast message
                Toast.makeText(NotifiactionSettings_Activity.this, "Community notifications OFF", Toast.LENGTH_SHORT).show();
            }
        });
        TestNotif.setOnClickListener(e->{
            if (TestNotif.isChecked()) {
                // Set the alarm to trigger once a month (30 days) with custom name and description
                set_test_notification_alarm(30 * 24 * 60 * 60 * 1000, "Test Reminder", "Take diagnostic test");
                // Show a toast message
                Toast.makeText(NotifiactionSettings_Activity.this, "Test notifications ON", Toast.LENGTH_SHORT).show();
            } else {
                // Test notifications are turned off
                cancel_notification_alarm();
                // Show a toast message
                Toast.makeText(NotifiactionSettings_Activity.this, "Test notifications OFF", Toast.LENGTH_SHORT).show();
            }
        });
        test.setOnClickListener(e->{
            if (test.isChecked()) {
                set_test_notification_alarm (60 * 1000, "Test Reminder", "Test desc");
                // Show a toast message
                Toast.makeText(NotifiactionSettings_Activity.this, "Test notifications ON", Toast.LENGTH_SHORT).show();
            } else {
                // Test notifications are turned off
                cancel_notification_alarm();
                // Show a toast message
                Toast.makeText(NotifiactionSettings_Activity.this, "Test notifications OFF", Toast.LENGTH_SHORT).show();
            }
        });
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event, navigate to HelloActivity
                Intent intent = new Intent(NotifiactionSettings_Activity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity after navigating
            }
        });
    }

    private void notification_channel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifications";
            String description = "Reminder notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Notification", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void set_notification_alarm(long delayMillis, String name, String description) {
        long triggerAtMillis = System.currentTimeMillis() + delayMillis;

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("name", name);
        notificationIntent.putExtra("description", description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarm_manager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarm_manager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }

        // Create a notification channel with the specified name and description
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Notification", "Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Reminder notifications");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    protected void onDestroy() {
        // Unregister the snapshot listener to avoid memory leaks
        if (blogsListenerRegistration != null) {
            blogsListenerRegistration.remove();
        }
        super.onDestroy();
    }

    public void cancel_notification_alarm() {
        alarm_manager.cancel(pending_intent);
    }
    public void set_test_notification_alarm(long intervalMillis, String name, String description) {
        long triggerAtMillis = System.currentTimeMillis() + intervalMillis;

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("name", name);
        notificationIntent.putExtra("description", description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);

        // Create a notification channel with the specified name and description
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Notification", "Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Reminder notifications");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
