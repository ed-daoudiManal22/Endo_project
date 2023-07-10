package com.example.myapplication;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.PeriodAdapter;
import com.example.myapplication.Models.PeriodDay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Menstrual_cycle_Activity extends AppCompatActivity{
    private RecyclerView periodRecyclerView;
    private List<PeriodDay> periodDayList;
    private PeriodAdapter periodAdapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menstrual_cycle);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        // Initialize RecyclerView
        periodRecyclerView = findViewById(R.id.periodRecyclerView);
        periodDayList = new ArrayList<>();
        periodAdapter = new PeriodAdapter(periodDayList);
        periodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        periodRecyclerView.setAdapter(periodAdapter);

        // Fetch period and ovulation data from Firestore
        fetchPeriodData();
    }

    private void fetchPeriodData() {
        db.collection("Users").document(userId).collection("periods")
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    periodDayList.clear();

                    // Calculate the upcoming period days based on last period and average cycle
                    Date lastPeriodDate = null;
                    int averageCycle = 28; // Change this value to the user's average cycle

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String date = documentSnapshot.getString("date");
                        boolean isPeriodDay = documentSnapshot.getBoolean("isPeriodDay");
                        boolean isOvulationDay = documentSnapshot.getBoolean("isOvulationDay");

                        PeriodDay periodDay = new PeriodDay(date, isPeriodDay || isOvulationDay);
                        periodDayList.add(periodDay);

                        // Set the color of the date on the CalendarView based on the type of day
                        Calendar calendar = Calendar.getInstance();
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date parsedDate = sdf.parse(date);
                            calendar.setTime(parsedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (isPeriodDay) {
                            // Set the background color of the period day
                            int redColor = Color.parseColor("#FF0000");
                            // TODO: Set the background color of the date on the CalendarView
                        } else if (isOvulationDay) {
                            // Set the background color of the ovulation day
                            int blueColor = Color.parseColor("#0000FF");
                            // TODO: Set the background color of the date on the CalendarView
                        }

                        if (isPeriodDay && lastPeriodDate == null) {
                            lastPeriodDate = calendar.getTime();
                        }
                    }

                    // Calculate the upcoming period days
                    if (lastPeriodDate != null) {
                        Calendar upcomingPeriodCalendar = Calendar.getInstance();
                        upcomingPeriodCalendar.setTime(lastPeriodDate);
                        upcomingPeriodCalendar.add(Calendar.DAY_OF_MONTH, averageCycle);

                        // Set the background color of the upcoming period day
                        int redColor = Color.parseColor("#FF0000");
                        // TODO: Set the background color of the date on the CalendarView

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String upcomingPeriodDate = sdf.format(upcomingPeriodCalendar.getTime());

                        PeriodDay upcomingPeriodDay = new PeriodDay(upcomingPeriodDate, false);
                        periodDayList.add(upcomingPeriodDay);
                    }

                    periodAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                });
    }

}