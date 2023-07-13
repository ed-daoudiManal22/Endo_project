package com.example.myapplication;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderActivity extends AppCompatActivity implements ReminderAdapter.OnReminderDeleteListener, ReminderAdapter.OnReminderActiveStatusChangeListener{

    private ImageView leftIcon;
    private RecyclerView recyclerView;
    private ReminderAdapter reminderAdapter;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserUid;

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

        Button addReminderButton = findViewById(R.id.addReminderButton);
        addReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a popup or dialog to add a new reminder
                showAddReminderDialog();
            }
        });

        // Set an OnClickListener to the leftIcon ImageView
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event and redirect to UserPage_Activity
                Intent intent = new Intent(ReminderActivity.this, UserPage_Activity.class);
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
                            String description = documentSnapshot.getString("description");
                            Timestamp datetimeTimestamp = documentSnapshot.getTimestamp("datetime");
                            boolean isActive = documentSnapshot.getBoolean("isActive");

                            // Convert Firestore Timestamp to Date
                            Date datetime = datetimeTimestamp != null ? datetimeTimestamp.toDate() : null;

                            Reminder reminder = new Reminder(id,title, datetime, description, isActive);
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
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);

        // Add "Save" button to the dialog
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();

                // Get the selected date from the DatePicker
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                // Get the selected time from the TimePicker
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                // Create a Calendar instance and set the selected date and time
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, minute);

                // Get the Date object from the Calendar
                Date datetime = calendar.getTime();

                // Save the new reminder to Firestore for the current user
                saveReminder(title, datetime, description);
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


    private void saveReminder(String title, Date datetime, String description) {

        Timestamp datetimeTimestamp = new Timestamp(datetime);

        Map<String, Object> reminderData = new HashMap<>();
        reminderData.put("title", title);
        reminderData.put("datetime", datetimeTimestamp);
        reminderData.put("description", description);

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