package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.ReminderAdapter;
import com.example.myapplication.Models.Reminder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ReminderActivity extends AppCompatActivity implements ReminderAdapter.OnReminderDeleteListener, ReminderAdapter.OnReminderActiveStatusChangeListener{

    private ImageView leftIcon;
    private RecyclerView recyclerView;
    private ReminderAdapter reminderAdapter;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserUid;
    private ImageButton addReminderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminders_settings);

        leftIcon = findViewById(R.id.leftIcon);

        recyclerView = findViewById(R.id.remindersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reminderAdapter = new ReminderAdapter(new ArrayList<>());
        recyclerView.setAdapter(reminderAdapter);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserUid = firebaseAuth.getCurrentUser().getUid();

        // Fetch user-specific reminders from Firestore and populate the adapter
        fetchUserReminders();
        reminderAdapter.setOnReminderDeleteListener(this);
        reminderAdapter.setOnReminderActiveStatusChangeListener(this);

        addReminderButton = findViewById(R.id.addReminderButton);
        addReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a popup or dialog to add a new reminder
                showAddReminderDialog();
            }
        });

        // Set an OnClickListener to the leftIcon ImageView
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event and redirect to UserPage_Activity
                Intent intent = new Intent(ReminderActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchUserReminders() {
        firestore.collection("Users").document(currentUserUid).collection("reminders")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Reminder> reminders = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String id = documentSnapshot.getId(); // Retrieve the document ID
                            String title = documentSnapshot.getString("title");
                            String time = documentSnapshot.getString("time");
                            boolean isActive = documentSnapshot.getBoolean("isActive");
                            List<Boolean> repeatDays = (List<Boolean>) documentSnapshot.get("repeatDays");
                            boolean[] repeatDaysArray = new boolean[repeatDays.size()];
                            for (int i = 0; i < repeatDays.size(); i++) {
                                repeatDaysArray[i] = repeatDays.get(i);
                            }

                            Reminder reminder = new Reminder(id,title, time,isActive,repeatDaysArray);
                            reminders.add(reminder);
                        }

                        // Update the list of reminders
                        reminderAdapter.setReminders(reminders);

                        // Notify the adapter that the dataset has changed
                        reminderAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ReminderActivity", "Error fetching user reminders", e);
                    }
                });
    }
    private void showAddReminderDialog() {
        // Show a dialog to add a new reminder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Reminder");

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
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
            }
        });

        // Add "Cancel" button to the dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void saveReminder(String title, String time,boolean[] repeatDays) {

        Map<String, Object> reminderData = new HashMap<>();
        reminderData.put("title", title);
        reminderData.put("time", time);
        reminderData.put("isActive", true);
        // Convert the boolean array to a List
        List<Boolean> repeatDaysList = new ArrayList<>();
        for (boolean repeatDay : repeatDays) {
            repeatDaysList.add(repeatDay);
        }
        reminderData.put("repeatDays", repeatDaysList);

        firestore.collection("Users").document(currentUserUid).collection("reminders")
                .add(reminderData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Refresh the list of user reminders
                        fetchUserReminders();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ReminderActivity", "Error saving reminder", e);
                    }
                });

        //scheduleNotification(title, timeString, description);
    }
    @Override
    public void onReminderActiveStatusChange(Reminder reminder) {
        updateReminderActiveStatus(reminder.getId(), reminder.isActive());
    }
    public void onReminderDelete(int position) {
        Reminder reminder = reminderAdapter.getReminders().get(position);
        String reminderId = reminder.getId(); // Assuming you have an "id" field in the Reminder model

        // Delete the reminder from Firestore
        firestore.collection("Users").document(currentUserUid).collection("reminders")
                .document(reminderId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Refresh the list of user reminders
                        fetchUserReminders();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ReminderActivity", "Error deleting reminder", e);
                    }
                });
    }
    private void updateReminderActiveStatus(String reminderId, boolean isActive) {
        firestore.collection("Users")
                .document(currentUserUid)
                .collection("reminders")
                .document(reminderId)
                .update("isActive", isActive)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Successfully updated the isActive value
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ReminderAdapter", "Error updating reminder isActive status", e);
                    }
                });
    }
}
