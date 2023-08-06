package com.example.myapplication;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;

public class NotifiactionSettings_Activity extends AppCompatActivity {
    PendingIntent pending_intent;
    AlarmManager alarm_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings);

        notification_channel();
        pending_intent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(this, NotificationReceiver.class), PendingIntent.FLAG_IMMUTABLE);
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        SwitchMaterial CommunityNotif = findViewById(R.id.communityNotifiSwitch);
        SwitchMaterial TestNotif = findViewById(R.id.DiagTestSwitch);
        SwitchMaterial test = findViewById(R.id.oneMinut);

        CommunityNotif.setOnClickListener(e->{
            if (CommunityNotif.isChecked()) {
                // Community notifications are turned on
                set_notification_alarm(24 * 60 * 60 * 1000, "Notifications", "Receive community notifications");
                // Show a toast message
                Toast.makeText(NotifiactionSettings_Activity.this, "Community notifications ON", Toast.LENGTH_SHORT).show();
            } else {
                // Community notifications are turned off
                cancel_notification_alarm();
                // Show a toast message
                Toast.makeText(NotifiactionSettings_Activity.this, "Community notifications OFF", Toast.LENGTH_SHORT).show();
            }
        });
        TestNotif.setOnClickListener(e->{
            if (TestNotif.isChecked()) {
                // Set the alarm to trigger once a month (30 days) with custom name and description
                set_notification_alarm(30 * 24 * 60 * 60 * 1000, "Test Reminder", "Take diagnostic test");
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
                set_notification_alarm(60 * 1000,"Test Reminder", "Test desc");
                // Show a toast message
                Toast.makeText(NotifiactionSettings_Activity.this, "Test notifications ON", Toast.LENGTH_SHORT).show();
            } else {
                // Test notifications are turned off
                cancel_notification_alarm();
                // Show a toast message
                Toast.makeText(NotifiactionSettings_Activity.this, "Test notifications OFF", Toast.LENGTH_SHORT).show();
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

    public void set_notification_alarm(long intervall, String name, String description) {
        long triggerAtMillis = System.currentTimeMillis() + intervall;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        if (Build.VERSION.SDK_INT >= 23) {
            alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervall, pending_intent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending_intent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarm_manager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending_intent);
        } else {
            alarm_manager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending_intent);
        }

        // Create a notification channel with the specified name and description
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Notification", name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void cancel_notification_alarm() {
        alarm_manager.cancel(pending_intent);
    }
}
