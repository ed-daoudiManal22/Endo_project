package com.example.myapplication;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class NotifiactionSettings_Activity extends AppCompatActivity {
    PendingIntent pending_intent;
    AlarmManager alarm_manager;
    private SwitchMaterial CommunityNotif, TestNotif ;
    private boolean isCommunityNotificationOn = false;
    private ListenerRegistration blogsListenerRegistration;
    private static final String COMMUNITY_NOTIFICATION = "CommunityNotif";
    private static final String NOTIFICATION = "TEST_NOTIFICATION";
    private static final String TEST_NOTIFICATION = "TestNotif";
    private static final String NOTIFICATION_DESCRIPTION = "Reminder notifications";
    private static final String SWITCH_STATES_KEY = "SwitchStates";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings);
        ImageView leftIcon = findViewById(R.id.leftIcon);

        notification_channel();
        pending_intent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(this, NotificationReceiver.class), PendingIntent.FLAG_IMMUTABLE);
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        CommunityNotif = findViewById(R.id.communityNotifiSwitch);
        TestNotif = findViewById(R.id.DiagTestSwitch);

        // Load the saved switch states from shared preferences and apply them
        SharedPreferences sharedPreferences = getSharedPreferences(SWITCH_STATES_KEY, MODE_PRIVATE);

        CommunityNotif.setChecked(sharedPreferences.getBoolean(COMMUNITY_NOTIFICATION, false));
        TestNotif.setChecked(sharedPreferences.getBoolean(TEST_NOTIFICATION, false));

        // Set up a snapshot listener for "Blogs" collection
        setupBlogsSnapshotListener();

        setupCommunityNotifClickListener();
        setupTestNotifClickListener();

        leftIcon.setOnClickListener(v -> {
            // Handle the click event, navigate to HelloActivity
            Intent intent = new Intent(NotifiactionSettings_Activity.this, User_profile.class);
            startActivity(intent);
            finish(); // Optional: Close the current activity after navigating
        });
    }

    private void notification_channel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = NOTIFICATION;
            String description = NOTIFICATION_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Notification", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void set_notification_alarm(long delayMillis, String name, String description, String type) {
        long triggerAtMillis = System.currentTimeMillis() + delayMillis;

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("name", name);
        notificationIntent.putExtra("description", description);

        int requestCode = type.hashCode(); // Generate a unique request code based on the type
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                requestCode,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);

        // Create a notification channel with the specified name and description
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION, NOTIFICATION, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NOTIFICATION_DESCRIPTION);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    protected void onDestroy() {
        // Save the current switch states to shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(SWITCH_STATES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(COMMUNITY_NOTIFICATION, CommunityNotif.isChecked());
        editor.putBoolean(TEST_NOTIFICATION, TestNotif.isChecked());
        editor.apply();

        // Unregister the snapshot listener to avoid memory leaks
        if (blogsListenerRegistration != null) {
            blogsListenerRegistration.remove();
        }
        super.onDestroy();
    }

    public void cancel_notification_alarm(String type) {
        int requestCode = type.hashCode(); // Generate a unique request code based on the type
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                requestCode,
                new Intent(this, NotificationReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        alarm_manager.cancel(pendingIntent);
    }

    public void set_test_notification_alarm(long intervalMillis, String name, String description, String type) {
        long triggerAtMillis = System.currentTimeMillis() + intervalMillis;

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("name", name);
        notificationIntent.putExtra("description", description);

        int requestCode = type.hashCode(); // Generate a unique request code based on the type
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                requestCode,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);

        // Create a notification channel with the specified name and description
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION, NOTIFICATION, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NOTIFICATION_DESCRIPTION);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void setupBlogsSnapshotListener() {
        CollectionReference blogsCollection = FirebaseFirestore.getInstance().collection("Blogs");
        blogsListenerRegistration = blogsCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore Listener", "Error listening for new blogs", error);
                return;
            }
            if (isCommunityNotificationOn && value != null) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        // A new blog has been added, trigger the notification
                        set_notification_alarm(0, "New Blog Added", "A member has added a new blog", "community");
                        // Show a toast message
                        Toast.makeText(NotifiactionSettings_Activity.this, "Community notifications ON", Toast.LENGTH_SHORT).show();
                        // Exit the loop after the first added document is found
                        break;
                    }
                }
            }
        });
    }
    private void setupCommunityNotifClickListener() {
        CommunityNotif.setOnClickListener(e -> {
            isCommunityNotificationOn = CommunityNotif.isChecked();
            if (!isCommunityNotificationOn) {
                cancel_notification_alarm("community");
                // Show a toast message
                Toast.makeText(NotifiactionSettings_Activity.this, "Community notifications OFF", Toast.LENGTH_SHORT).show();
            }
            // Save the switch state to shared preferences
            getSharedPreferences(SWITCH_STATES_KEY, MODE_PRIVATE)
                    .edit()
                    .putBoolean(COMMUNITY_NOTIFICATION, isCommunityNotificationOn)
                    .apply();
        });
    }
    private void setupTestNotifClickListener() {
        TestNotif.setOnClickListener(e -> {
            if (TestNotif.isChecked()) {
                // Set the alarm to trigger once a month (30 days) with custom name and description
                set_test_notification_alarm(30L * 24 * 60 * 60 * 1000, "Test Reminder", "Take diagnostic test", "Test");
                // Show a toast message
                Toast.makeText(NotifiactionSettings_Activity.this, "Test notifications ON", Toast.LENGTH_SHORT).show();
            } else {
                // Test notifications are turned off
                cancel_notification_alarm("Test");
                // Show a toast message
                Toast.makeText(NotifiactionSettings_Activity.this, "Test notifications OFF", Toast.LENGTH_SHORT).show();
            }
            // Save the switch state to shared preferences
            getSharedPreferences(SWITCH_STATES_KEY, MODE_PRIVATE)
                    .edit()
                    .putBoolean(TEST_NOTIFICATION, TestNotif.isChecked())
                    .apply();
        });
    }
}
