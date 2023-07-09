package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Menstrual_cycle_Activity extends AppCompatActivity{
    private CalendarView calendarView;
    private DatabaseReference userDataRef;
    private String userId;
    private List<Long> periodDays;
    private List<Long> ovulationDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menstrual_cycle);

        // Initialize views
        calendarView = findViewById(R.id.calendar_view);

        // Get the current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        // Get reference to the user's menstrual cycle data
        userDataRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        // Retrieve the user's menstrual cycle data from Firebase
        userDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve the start date of the last period and average cycle length
                String lastPeriodStartDate = dataSnapshot.child("lastPeriodStartDate").getValue(String.class);
                int averageCycleLength = dataSnapshot.child("averageCycleLength").getValue(Integer.class);

                // Calculate the days of periods and ovulation periods
                periodDays = calculatePeriodDays(lastPeriodStartDate, averageCycleLength);
                ovulationDays = calculateOvulationDays(periodDays);

                // Update the calendar to highlight the period and ovulation days
                updateCalendar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        Button trackSymptomsButton = findViewById(R.id.trackSymptomsButton);
        trackSymptomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menstrual_cycle_Activity.this, SymptomsTrack_Activity.class);
                startActivity(intent);
            }
        });
    }

    // Calculate the days of periods based on the start date of the last period and average cycle length
    private List<Long> calculatePeriodDays(String lastPeriodStartDate, int averageCycleLength) {
        List<Long> periodDays = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            // Parse the start date of the last period
            Date startDate = dateFormat.parse(lastPeriodStartDate);

            // Set the start date as the initial reference
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            // Calculate the period days based on the average cycle length
            for (int i = 0; i < averageCycleLength; i++) {
                periodDays.add(calendar.getTimeInMillis());
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return periodDays;
    }

    // Calculate the days of ovulation based on the days of periods
    private List<Long> calculateOvulationDays(List<Long> periodDays) {
        List<Long> ovulationDays = new ArrayList<>();

        for (Long periodDay : periodDays) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(periodDay);

            // Calculate the ovulation day
            calendar.add(Calendar.DAY_OF_MONTH, -14);
            ovulationDays.add(calendar.getTimeInMillis());
        }

        return ovulationDays;
    }

    // Update the calendar to highlight the period and ovulation days
    private void updateCalendar() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                // Customize the appearance of the selected calendar cell based on the day's classification
                // You can use the periodDays and ovulationDays lists to determine the classification of the selected day
                // Update the UI accordingly
            }
        });
    }
}