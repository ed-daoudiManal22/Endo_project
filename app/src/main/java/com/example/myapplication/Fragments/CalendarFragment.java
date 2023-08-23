package com.example.myapplication.Fragments;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.myapplication.EventDecorator;
import com.example.myapplication.R;
import com.example.myapplication.ReminderActivity;
import com.example.myapplication.User_profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class CalendarFragment extends Fragment {
    private MaterialCalendarView calendarView;
    private String stringDateSelected;
    private CollectionReference eventsCollection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        ImageView addEventButton = view.findViewById(R.id.addEventButton);
        ImageView leftIcon = view.findViewById(R.id.leftIcon);
        ImageView notificationIcon = view.findViewById(R.id.notificationIcon);

        // Initialize the MaterialCalendarView
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            // Format the date as "dd-mm-yyyy"
            stringDateSelected = String.format("%02d-%02d-%d", date.getDay(), date.getMonth() + 1, date.getYear());
            calendarClicked(); // Call the method without passing any argument
        });
        addEventButton.setOnClickListener(v -> showDialogToAddEvent());
        leftIcon.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), User_profile.class);
            startActivity(intent);
        });

        notificationIcon.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ReminderActivity.class);
            startActivity(intent);
        });

        // Get the current authenticated user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (currentUser != null) {
            eventsCollection = db.collection("Users").document(currentUser.getUid()).collection("Events");
        }

        List<CalendarDay> eventDates = new ArrayList<>();
        // Populate eventDates based on your event data  argb(100,255,91,116)

        DotSpan dotSpan = new DotSpan(5, ContextCompat.getColor(requireContext(), R.color.pink)); // Customize the dot size and color
        // Get the available event dates from Firestore and populate the eventDates list
        eventsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String date = document.getId(); // Get the date string from the document ID
                    String[] dateParts = date.split("-"); // Split the date into parts
                    int year = Integer.parseInt(dateParts[2]);
                    int month = Integer.parseInt(dateParts[1]) - 1; // Months are 0-indexed
                    int day = Integer.parseInt(dateParts[0]);
                    CalendarDay calendarDay = CalendarDay.from(year, month, day);
                    eventDates.add(calendarDay);
                }

                // Apply the event decorators to the calendarView
                EventDecorator eventDecorator = new EventDecorator(eventDates, dotSpan);
                calendarView.addDecorator(eventDecorator);
            }
        });

        return view;
    }
    private void calendarClicked() {
        // Use getView() to get the root view of the fragment
        View rootView = getView();
        if (rootView == null) {
            return; // Return if the root view is null
        }

        TextView eventDetailsTextView = rootView.findViewById(R.id.eventDetailsTextView);
        CardView eventDetailsCardView = rootView.findViewById(R.id.eventDetailsCardView);
        eventDetailsCardView.setOnLongClickListener(v -> {
            showUpdateDeleteDialog(stringDateSelected);
            return true; // Return true to indicate that the event has been consumed
        });

        eventsCollection.document(stringDateSelected).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String eventTitle = document.getString("title");
                    String eventNote = document.getString("note");
                    String eventDetails = "Title: " + eventTitle + "\n\nNote: " + eventNote;
                    eventDetailsTextView.setText(eventDetails);
                    // Show the event details TextView
                    eventDetailsCardView.setVisibility(View.VISIBLE);
                } else {
                    eventDetailsTextView.setText("No events");
                    // Show the event details TextView
                    eventDetailsCardView.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(requireContext(), "Error getting document.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogToAddEvent() {
        View rootView = getView();
        if (rootView == null) {
            return; // Return if the root view is null
        }

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_event, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setView(dialogView);

        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextNote = dialogView.findViewById(R.id.editTextNote);
        Button buttonAddEvent = dialogView.findViewById(R.id.buttonAddEvent);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        buttonAddEvent.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString();
            String note = editTextNote.getText().toString();
            addEventToFirestore(title, note);

            alertDialog.dismiss();
        });
    }
    private void addEventToFirestore(String title, String note) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("title", title);
        eventMap.put("note", note);

        // Construct the document reference for the specific date
        DocumentReference eventDocumentRef = eventsCollection.document(stringDateSelected);

        eventDocumentRef.set(eventMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Event added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error adding event", Toast.LENGTH_SHORT).show());
    }
    private void showUpdateDeleteDialog(String date) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        String[] options = {getString(R.string.update_button_label), getString(R.string.delete)};
        alertDialogBuilder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showUpdateDialog(date);
                    break;
                case 1:
                    showDeleteDialog(date);
                    break;
            }
            dialog.dismiss();
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void showUpdateDialog(String date) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_update_event, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setView(dialogView);

        EditText editTextUpdatedTitle = dialogView.findViewById(R.id.editTextUpdateTitle);
        EditText editTextUpdatedNote = dialogView.findViewById(R.id.editTextUpdateNote);
        Button buttonUpdateEvent = dialogView.findViewById(R.id.buttonUpdateEvent);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        buttonUpdateEvent.setOnClickListener(v -> {
            String updatedTitle = editTextUpdatedTitle.getText().toString();
            String updatedNote = editTextUpdatedNote.getText().toString();
            updateEventInFirestore(date, updatedTitle, updatedNote);

            alertDialog.dismiss();
        });
    }

    private void showDeleteDialog(String date) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setTitle(R.string.delete_confirmation_title);
        alertDialogBuilder.setMessage(R.string.delete_confirmation_message);
        alertDialogBuilder.setPositiveButton(R.string.delete, (dialog, which) -> {
            DocumentReference eventDocumentRef = eventsCollection.document(date);

            eventDocumentRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), R.string.event_deleted_successfully, Toast.LENGTH_SHORT).show();
                        // You might want to update your UI or event list here
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), R.string.error_deleting_event, Toast.LENGTH_SHORT).show());
            dialog.dismiss();
        });
        alertDialogBuilder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void updateEventInFirestore(String date, String updatedTitle, String updatedNote) {
        DocumentReference eventDocumentRef = eventsCollection.document(date);

        Map<String, Object> updatedEventMap = new HashMap<>();
        updatedEventMap.put("title", updatedTitle);
        updatedEventMap.put("note", updatedNote);

        eventDocumentRef.update(updatedEventMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Event updated successfully", Toast.LENGTH_SHORT).show();
                    // You might want to update your UI or event list here
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error updating event", Toast.LENGTH_SHORT).show());
    }

}
