package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventCalendar_Activity extends AppCompatActivity {

    private CalendarView calendarView;
    private EditText editText;
    private String stringDateSelected;
    private FirebaseFirestore db;;
    private CollectionReference eventsCollection;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_calendar);

        calendarView = findViewById(R.id.calendarView);
        editText = findViewById(R.id.editText);

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
    }

    private void calendarClicked(){
        eventsCollection.document(stringDateSelected).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        editText.setText(document.getString("event"));
                    } else {
                        editText.setText("");
                    }
                } else {
                    Toast.makeText(EventCalendar_Activity.this, "Error getting document.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void buttonSaveEvent(View view){
        if (currentUser != null) {
            String eventText = editText.getText().toString();
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("event", eventText);

            eventsCollection.document(stringDateSelected).set(eventMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EventCalendar_Activity.this, "Event saved.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EventCalendar_Activity.this, "Error saving event.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
        }    }
}
