package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Models.DateAxisValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

public class LineChart_Activity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserUid;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserUid = firebaseAuth.getCurrentUser().getUid();

        // Get the LineChart view from the layout
        LineChart lineChart = findViewById(R.id.lineChart);

        // Create a list to store the pain score entries
        List<Entry> entries = new ArrayList<>();

        // Get the symptoms subcollection for the current user
        CollectionReference symptomsCollection = firestore.collection("Users")
                .document(currentUserUid)
                .collection("symptoms");

        // Query the symptoms subcollection to retrieve the pain scores
        symptomsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // ...
                    for (DocumentSnapshot document : task.getResult()) {
                        // Get the document name representing the date
                        String documentName = document.getId();

                        // Parse the date from the document name
                        Date date = null;
                        try {
                            date = dateFormat.parse(documentName);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // Get the pain score from the document
                        Double painScore = document.getDouble("painScore");

                        // Add the pain score entry to the list
                        if (date != null && painScore != null) {
                            entries.add(new Entry(date.getTime(), painScore.floatValue()));
                        }
                    }

                    // Create a TreeMap to store the entries with timestamps as keys
                    TreeMap<Float, Entry> entryMap = new TreeMap<>();

                    // Add the entries to the TreeMap
                    for (Entry entry : entries) {
                        entryMap.put(entry.getX(), entry);
                    }

                    // Retrieve the sorted entries from the TreeMap
                    List<Entry> sortedEntries = new ArrayList<>(entryMap.values());

                    // Set the minimum and maximum values for the Y-axis
                    lineChart.getAxisLeft().setAxisMinimum(0f);
                    lineChart.getAxisLeft().setAxisMaximum(10f);

                    // Create a ValueFormatter to format the X-axis values as dates
                    ValueFormatter xAxisFormatter = new ValueFormatter() {
                        private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM", Locale.US);

                        @Override
                        public String getFormattedValue(float value) {
                            long millis = (long) value;
                            Date date = new Date(millis);
                            return dateFormatter.format(date);
                        }
                    };

                    // Set the X-axis value formatter
                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setValueFormatter(xAxisFormatter);

                    // Create a dataset with the entries and customize it
                    LineDataSet dataSet = new LineDataSet(sortedEntries, "Pain Scores");
                    dataSet.setColor(Color.RED);
                    dataSet.setValueTextColor(Color.BLACK);
                    // ... Customize other dataset properties as needed

                    // Create a LineData object with the dataset
                    LineData lineData = new LineData(dataSet);

                    // Set the LineData to the chart and refresh it
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    lineChart.setData(lineData);
                    lineChart.getXAxis().setDrawGridLines(false);
                    lineChart.getAxisLeft().setDrawGridLines(false);
                    lineChart.getAxisRight().setEnabled(false);
                    lineChart.invalidate();
                } else {
                    Log.e("LineChart_Activity", "Error getting symptoms subcollection: ", task.getException());
                }
            }
        });
    }
}
