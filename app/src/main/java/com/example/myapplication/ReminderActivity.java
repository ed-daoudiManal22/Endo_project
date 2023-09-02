package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.ReminderAdapter;
import com.example.myapplication.Models.Reminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ReminderActivity extends AppCompatActivity implements ReminderAdapter.OnReminderDeleteListener, ReminderAdapter.OnReminderActiveStatusChangeListener{
    private AlarmManager alarmManager;
    private ReminderAdapter reminderAdapter;
    private FirebaseFirestore firestore;
    private String currentUserUid;
    private static final String USERS_COLLECTION = "Users";
    private static final String REMINDERS_COLLECTION = "reminders";
    private static final String FIELD_IS_ACTIVE = "isActive";
    private static final String TAG = "ReminderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminders_settings);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        ImageView leftIcon = findViewById(R.id.leftIcon);

        RecyclerView recyclerView = findViewById(R.id.remindersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reminderAdapter = new ReminderAdapter(new ArrayList<>());
        recyclerView.setAdapter(reminderAdapter);

        firestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUserUid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        // Fetch user-specific reminders from Firestore and populate the adapter
        fetchUserReminders();
        reminderAdapter.setOnReminderDeleteListener(this);
        reminderAdapter.setOnReminderActiveStatusChangeListener(this);

        ImageButton addReminderButton = findViewById(R.id.addReminderButton);
        addReminderButton.setOnClickListener(view -> {
            // Show a popup or dialog to add a new reminder
            showAddReminderDialog();
        });
        leftIcon.setOnClickListener(v -> {
            super.onBackPressed(); // This calls the default behavior (navigating back)
        });
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

    private void fetchUserReminders() {
        firestore.collection(USERS_COLLECTION).document(currentUserUid).collection(REMINDERS_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Reminder> reminders = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String id = documentSnapshot.getId(); // Retrieve the document ID
                        String title = documentSnapshot.getString("title");
                        String time = documentSnapshot.getString("time");
                        boolean isActive = Boolean.TRUE.equals(documentSnapshot.getBoolean(FIELD_IS_ACTIVE));
                        List<Boolean> repeatDays = (List<Boolean>) documentSnapshot.get("repeatDays");
                        boolean[] repeatDaysArray = new boolean[0];
                        if (repeatDays != null) {
                            repeatDaysArray = new boolean[repeatDays.size()];
                        }
                        if (repeatDays != null) {
                            for (int i = 0; i < repeatDays.size(); i++) {
                                repeatDaysArray[i] = repeatDays.get(i);
                            }
                        }

                        Reminder reminder = new Reminder(id,title, time,isActive,repeatDaysArray);
                        reminders.add(reminder);
                    }
                    // Sort the reminders by time
                    reminders.sort((reminder1, reminder2) -> {
                        // Parse the time strings and compare them
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        try {
                            Date time1 = sdf.parse(reminder1.getTime());
                            Date time2 = sdf.parse(reminder2.getTime());
                            return time1.compareTo(time2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });

                    // Update the list of reminders
                    reminderAdapter.setReminders(reminders);

                    // Notify the adapter that the dataset has changed
                    reminderAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching user reminders", e));
    }
    private void showAddReminderDialog() {
        // Show a dialog to add a new reminder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_reminder));

        // Inflate the custom dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_reminder, null);
        builder.setView(dialogView);

        // Find the views in the custom layout
        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        CheckBox Mon = dialogView.findViewById(R.id.checkBoxMo);
        CheckBox Tue = dialogView.findViewById(R.id.checkBoxTu);
        CheckBox Wed = dialogView.findViewById(R.id.checkBoxWe);
        CheckBox Thu = dialogView.findViewById(R.id.checkBoxTh);
        CheckBox Fri = dialogView.findViewById(R.id.checkBoxFr);
        CheckBox Sat = dialogView.findViewById(R.id.checkBoxSa);
        CheckBox Sun = dialogView.findViewById(R.id.checkBoxSu);

        // Add "Save" button to the dialog
        builder.setPositiveButton(getString(R.string.save), (dialog, which) -> {
            String title = titleEditText.getText().toString();

            // Get the selected time from the TimePicker
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();

            // Create a boolean array to store repeat days
            boolean[] repeatDays = new boolean[7];
            repeatDays[0] = Mon.isChecked();
            repeatDays[1] = Tue.isChecked();
            repeatDays[2] = Wed.isChecked();
            repeatDays[3] = Thu.isChecked();
            repeatDays[4] = Fri.isChecked();
            repeatDays[5] = Sat.isChecked();
            repeatDays[6] = Sun.isChecked();

            // Create a Calendar instance and set the selected time
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            // Format the time as "hh:mm" string
            String timeString = String.format("%02d:%02d", hour, minute);

            // Save the new reminder to Firestore for the current user
            saveReminder(title, timeString,repeatDays);
        });

        // Add "Cancel" button to the dialog
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveReminder(String title, String time,boolean[] repeatDays) {

        Map<String, Object> reminderData = new HashMap<>();
        reminderData.put("title", title);
        reminderData.put("time", time);
        reminderData.put(FIELD_IS_ACTIVE, true);
        // Convert the boolean array to a List
        List<Boolean> repeatDaysList = new ArrayList<>();
        for (boolean repeatDay : repeatDays) {
            repeatDaysList.add(repeatDay);
        }
        reminderData.put("repeatDays", repeatDaysList);

        firestore.collection(USERS_COLLECTION).document(currentUserUid).collection(REMINDERS_COLLECTION)
                .add(reminderData)
                .addOnSuccessListener(documentReference -> {
                    // Refresh the list of user reminders
                    fetchUserReminders();

                    // Schedule notifications for the new reminder
                    Reminder newReminder = new Reminder(documentReference.getId(), title, time, true, repeatDays);
                    scheduleReminderNotifications(newReminder);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error saving reminder", e));
    }
    @Override
    public void onReminderActiveStatusChange(Reminder reminder) {
        updateReminderActiveStatus(reminder.getId(), reminder.isActive());
    }
    public void onReminderDelete(int position) {
        Reminder reminder = reminderAdapter.getReminders().get(position);
        String reminderId = reminder.getId(); // Assuming you have an "id" field in the Reminder model

        // Delete the reminder from Firestore
        firestore.collection(USERS_COLLECTION).document(currentUserUid).collection(REMINDERS_COLLECTION)
                .document(reminderId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Cancel notifications for the deleted reminder
                    cancelReminderNotifications(reminder);
                    // Refresh the list of user reminders
                    fetchUserReminders();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting reminder", e));
    }
    private void updateReminderActiveStatus(String reminderId, boolean isActive) {
        firestore.collection(USERS_COLLECTION)
                .document(currentUserUid)
                .collection(REMINDERS_COLLECTION)
                .document(reminderId)
                .update(FIELD_IS_ACTIVE, isActive)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated the isActive value
                    // If the reminder is being activated, schedule notifications
                    if (isActive) {
                        fetchUserReminders(); // Refresh the reminders list
                        Reminder updatedReminder = findReminderById(reminderId);
                        if (updatedReminder != null) {
                            scheduleReminderNotifications(updatedReminder);
                        }
                    }else {
                        Reminder updatedReminder = findReminderById(reminderId);
                        if (updatedReminder != null) {
                            cancelReminderNotifications(updatedReminder);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("ReminderAdapter", "Error updating reminder isActive status", e));
    }
    private void scheduleReminderNotifications(Reminder reminder) {
        if (reminder.isActive()) {
            boolean[] repeatDays = reminder.getRepeatDays();
            String time = reminder.getTime();
            String[] timeParts = time.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            List<Integer> activeDays = new ArrayList<>();
            for (int i = 0; i < repeatDays.length; i++) {
                if (repeatDays[i]) {
                    activeDays.add(i);
                }
            }

            Collections.sort(activeDays);

            for (int dayIndex : activeDays) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                int dayOfWeek = (dayIndex + Calendar.MONDAY) % 7;
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                Intent notificationIntent = new Intent(this, NotificationReceiver.class);
                notificationIntent.putExtra("name", reminder.getTitle());
                notificationIntent.putExtra("description", "Don't forget your task");

                int requestCode = (reminder.getId() + "_" + dayOfWeek).hashCode();
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(),
                        requestCode,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

                long timeInMillis = calendar.getTimeInMillis();
                if (timeInMillis < System.currentTimeMillis()) {
                    timeInMillis += AlarmManager.INTERVAL_DAY; // Trigger immediately
                }

                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingIntent
                );
            }
        }
    }
    private void cancelReminderNotifications(Reminder reminder) {
        boolean[] repeatDays = reminder.getRepeatDays();
        String[] timeParts = reminder.getTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        for (int i = 0; i < repeatDays.length; i++) {
            if (repeatDays[i]) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                int dayOfWeek = (i + Calendar.MONDAY) % 7;
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                Intent notificationIntent = new Intent(this, NotificationReceiver.class);
                int requestCode = (reminder.getId() + "_" + dayOfWeek).hashCode();
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(),
                        requestCode,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

                alarmManager.cancel(pendingIntent);
            }
        }
    }
    private Reminder findReminderById(String reminderId) {
        List<Reminder> reminders = reminderAdapter.getReminders();
        for (Reminder reminder : reminders) {
            if (reminder.getId().equals(reminderId)) {
                return reminder;
            }
        }
        return null; // Return null if no matching reminder is found
    }

}