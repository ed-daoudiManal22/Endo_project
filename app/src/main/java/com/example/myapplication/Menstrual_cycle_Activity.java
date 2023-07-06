package com.example.myapplication;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
public class Menstrual_cycle_Activity extends AppCompatActivity{
    private CalendarView calendarView;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menstrual_cycle);

        calendarView = findViewById(R.id.calendar_view);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Create a date object from the selected date
            Date selectedDate = new Date(year - 1900, month, dayOfMonth);

            // Format the selected date as "dd/MM/yyyy"
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(selectedDate);

            // Display a toast message with the selected date
            Toast.makeText(Menstrual_cycle_Activity.this, "Selected date: " + formattedDate, Toast.LENGTH_SHORT).show();

            // Check if the selected date is within the woman's period
            checkPeriodDate(selectedDate);
        });
    }

    private void checkPeriodDate(Date selectedDate) {
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Get the period duration and average cycle length from Firestore
                                long periodDuration = document.getLong("periodDuration");
                                long cycleLength = document.getLong("cycleLength");

                                // Calculate the start and end dates of the woman's period
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(selectedDate);
                                calendar.add(Calendar.DAY_OF_MONTH, (int) -cycleLength);
                                Date periodStartDate = calendar.getTime();
                                calendar.add(Calendar.DAY_OF_MONTH, (int) periodDuration);
                                Date periodEndDate = calendar.getTime();

                                // Check if the selected date falls within the woman's period
                                if (selectedDate.after(periodStartDate) && selectedDate.before(periodEndDate)) {
                                    Toast.makeText(Menstrual_cycle_Activity.this, "You're on your period!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Menstrual_cycle_Activity.this, "You're not on your period", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
    }
}
