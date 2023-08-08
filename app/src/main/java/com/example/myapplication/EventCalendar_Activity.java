package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventCalendar_Activity extends AppCompatActivity {

    private CalendarView calendarView;
    private String stringDateSelected;
    private ImageView addEventButton;
    private FirebaseFirestore db;;
    private CollectionReference eventsCollection;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_calendar);

        calendarView = findViewById(R.id.calendarView);
        addEventButton = findViewById(R.id.addEventButton);

        // Get the current authenticated user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        eventsCollection = db.collection("Users").document(currentUser.getUid()).collection("Events");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                // Format the date as "dd-mm-yyyy"
                stringDateSelected = String.format("%02d-%02d-%d", dayOfMonth, month + 1, year);
                calendarClicked();
            }
        });
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogToAddEvent();
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                stringDateSelected = String.format("%02d-%02d-%d", dayOfMonth, month + 1, year);
                calendarClicked();
            }
        });
    }

    private void calendarClicked(){
        TextView eventDetailsTextView = findViewById(R.id.eventDetailsTextView);

        eventsCollection.document(stringDateSelected).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String eventTitle = document.getString("title");
                        String eventNote = document.getString("note");
                        String eventDetails = "Title: " + eventTitle + "\nNote: " + eventNote;
                        eventDetailsTextView.setText(eventDetails);
                    } else {
                        eventDetailsTextView.setText("No events");
                    }
                } else {
                    Toast.makeText(EventCalendar_Activity.this, "Error getting document.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showDialogToAddEvent() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_event, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(dialogView);

        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextNote = dialogView.findViewById(R.id.editTextNote);
        Button buttonAddEvent = dialogView.findViewById(R.id.buttonAddEvent);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        buttonAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String note = editTextNote.getText().toString();
                addEventToFirestore(title, note);

                alertDialog.dismiss();
            }
        });
    }
    private void addEventToFirestore(String title, String note) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("title", title);
        eventMap.put("note", note);

        // Construct the document reference for the specific date
        DocumentReference eventDocumentRef = eventsCollection.document(stringDateSelected);

        eventDocumentRef.set(eventMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EventCalendar_Activity.this, "Event added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EventCalendar_Activity.this, "Error adding event", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
