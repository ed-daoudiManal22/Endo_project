package com.example.myapplication.Fragments;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
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

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

        // Initialize the calendar with localized month names
        Locale locale = getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL yyyy", locale);
        calendarView.setTitleFormatter(day -> dateFormat.format(day.getDate()));

        // Set localized day names
        SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEE", locale);
        String[] dayNames = new String[7];
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        for (int i = 0; i < 7; i++) {
            dayNames[i] = dayNameFormat.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }
        calendarView.setWeekDayLabels(dayNames);

        // Set localized day numbers
        calendarView.setDayFormatter((day) -> {
            SimpleDateFormat dayNumberFormat = new SimpleDateFormat("d", locale);
            return dayNumberFormat.format(day.getDate());
        });

        fetchAndUpdateDecorators(); // Fetch and apply decorators

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
                    String eventDetails = getString(R.string.title_prefix) + eventTitle + "\n\n" + getString(R.string.note_prefix) + eventNote;                    eventDetailsTextView.setText(eventDetails);
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
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Event added successfully", Toast.LENGTH_SHORT).show();
                    clearDecorators(); // Clear decorators before adding the event
                    fetchAndUpdateDecorators(); // Fetch and apply decorators again after adding the event
                })
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

        // Fetch existing event details from Firestore
        eventsCollection.document(date).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String eventTitle = document.getString("title");
                    String eventNote = document.getString("note");

                    // Populate EditText fields with existing data
                    editTextUpdatedTitle.setText(eventTitle);
                    editTextUpdatedNote.setText(eventNote);
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        buttonUpdateEvent.setOnClickListener(v -> {
            String updatedTitle = editTextUpdatedTitle.getText().toString();
            String updatedNote = editTextUpdatedNote.getText().toString();
            updateEventInFirestore(date, updatedTitle, updatedNote);

            resetEventDetailsTextView(); // Reset the event details view
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
                        clearDecorators(); // Clear decorators before deleting the event
                        fetchAndUpdateDecorators(); // Fetch and apply decorators again
                        resetEventDetailsTextView(); // Reset the event details view
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
    private void clearDecorators() {
        if (calendarView != null) {
            calendarView.removeDecorators();
        }
    }
    private void fetchAndUpdateDecorators() {
        List<CalendarDay> eventDates = new ArrayList<>();
        // Populate eventDates based on your event data

        DotSpan dotSpan = new DotSpan(5, ContextCompat.getColor(requireContext(), R.color.pink));

        eventsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String date = document.getId();
                    String[] dateParts = date.split("-");
                    int year = Integer.parseInt(dateParts[2]);
                    int month = Integer.parseInt(dateParts[1]) - 1;
                    int day = Integer.parseInt(dateParts[0]);
                    CalendarDay calendarDay = CalendarDay.from(year, month, day);
                    eventDates.add(calendarDay);
                }

                calendarView.removeDecorators(); // Clear existing decorators
                EventDecorator eventDecorator = new EventDecorator(eventDates, dotSpan);
                calendarView.addDecorator(eventDecorator); // Apply updated decorators
            }
        });
    }
    private void resetEventDetailsTextView() {
        View rootView = getView();
        if (rootView != null) {
            TextView eventDetailsTextView = rootView.findViewById(R.id.eventDetailsTextView);
            eventDetailsTextView.setText(""); // Clear the text
            CardView eventDetailsCardView = rootView.findViewById(R.id.eventDetailsCardView);
            eventDetailsCardView.setVisibility(View.INVISIBLE); // Hide the card view
        }
    }

}
