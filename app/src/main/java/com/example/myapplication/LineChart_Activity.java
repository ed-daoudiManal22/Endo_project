package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Models.DateAxisValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.util.Calendar;
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
    private TextView averagePainTextView;
    private ImageView leftIcon, notificationIcon;
    private String currentUserUid;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    private final int[] pastelColors = new int[]{
            Color.rgb(227, 227, 255),
            Color.rgb(223, 242, 253),
            Color.rgb(226, 252, 230),
            Color.rgb(252, 250, 222),
            Color.rgb(255, 238, 226),
            Color.rgb(255, 219, 219),
            // Add more pastel colors as needed
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        // Get the context of the activity
        Context context = getApplicationContext();

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserUid = firebaseAuth.getCurrentUser().getUid();

        // Calculate the date one month ago from the current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date oneMonthAgo = calendar.getTime();

        // Get the LineChart view from the layout
        LineChart lineChart = findViewById(R.id.lineChart);
        averagePainTextView = findViewById(R.id.averagePain);
        leftIcon = findViewById(R.id.leftIcon);
        notificationIcon = findViewById(R.id.notificationIcon);

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event, navigate to HelloActivity
                Intent intent = new Intent(LineChart_Activity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity after navigating
            }
        });
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event, navigate to HelloActivity
                Intent intent = new Intent(LineChart_Activity.this, ReminderActivity.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity after navigating
            }
        });

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

                        // Filter out entries that fall within the past month
                        if (date != null && date.after(oneMonthAgo)) {
                            // Get the pain score from the document
                            Double painScore = document.getDouble("painScore");

                            // Add the pain score entry to the list
                            if (painScore != null) {
                                entries.add(new Entry(date.getTime(), painScore.floatValue()));
                            }
                        }

                        // Calculate the average pain score
                        float totalPainScore = 0;
                        for (Entry entry : entries) {
                            totalPainScore += entry.getY();
                        }
                        float averagePainScore = totalPainScore / entries.size();
                        // Format the averagePainScore to show only two decimal places
                        String formattedAveragePainScore = String.format(Locale.US, "%.2f", averagePainScore);

                        // Display the average pain score in the TextView
                        averagePainTextView.setText("Pain Average : " + formattedAveragePainScore);
                    }

                    // Sort the entries by their timestamps
                    Collections.sort(entries, new Comparator<Entry>() {
                        @Override
                        public int compare(Entry entry1, Entry entry2) {
                            return Long.compare((long) entry1.getX(), (long) entry2.getX());
                        }
                    });

                    // Set the minimum and maximum values for the X-axis to display data points from one month ago till the current day
                    float minimumXValue = oneMonthAgo.getTime();
                    float maximumXValue = System.currentTimeMillis();

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
                    LineDataSet dataSet = new LineDataSet(entries, "Pain Scores");
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
                    // end of graph implementation

                    // Create a list to store all pain locations from all documents
                    List<String> allPainLocations = new ArrayList<>();
                    // Create a list to store pie chart entries (slices)
                    List<PieEntry> pieEntries = new ArrayList<>();

                    for (DocumentSnapshot document : task.getResult()) {
                        // Get the pain Location array from the document
                        List<String> painLocations = (List<String>) document.get("pain Location");

                        // Add all pain locations to the list
                        if (painLocations != null) {
                            allPainLocations.addAll(painLocations);
                        }
                    }

                    // Calculate the occurrences of each pain location
                    TreeMap<String, Integer> painLocationOccurrences = new TreeMap<>();
                    for (String location : allPainLocations) {
                        painLocationOccurrences.put(location, painLocationOccurrences.getOrDefault(location, 0) + 1);
                    }

                    // Calculate the total number of pain locations
                    int totalPainLocations = allPainLocations.size();

                    // Get references to the LinearLayout that will hold the TextViews
                    LinearLayout painLocationsLayout = findViewById(R.id.painLocationsLayout);

                    // Clear the LinearLayout in case it already has views (to avoid duplicates if you reload data)
                    painLocationsLayout.removeAllViews();

                    // Set the pain locations and their percentages in the respective TextViews
                    Set<String> uniquePainLocations = new HashSet<>(allPainLocations);
                    for (String location : uniquePainLocations) {
                        int occurrences = painLocationOccurrences.get(location);
                        float percentage = (occurrences * 100f) / totalPainLocations;

                        // Add the PieEntry for each pain location and percentage
                        pieEntries.add(new PieEntry(percentage, location));
                    }
                    // Create a dataset for the PieChart with the entries and customize it
                    PieDataSet pieDataSet = new PieDataSet(pieEntries, "Pain Locations");
                    pieDataSet.setColors(pastelColors);
                    // ... Customize other dataset properties as needed

                    // Create a PieData object with the dataset
                    PieData pieData = new PieData(pieDataSet);
                    pieData.setValueTextSize(12f); // Adjust the text size of the values inside the slices
                    // ... Customize other PieData properties as needed

                    // Get the PieChart view from the layout
                    PieChart pieChart = findViewById(R.id.pieChart);

                    // Set the PieData to the chart and refresh it
                    pieChart.setData(pieData);
                    pieChart.getDescription().setEnabled(false); // Disable the description
                    pieChart.setDrawEntryLabels(false); // Disable labels inside the slices
                    pieChart.setDrawHoleEnabled(false); // Disable the center hole
                    pieChart.invalidate();
                    //end pain location Chart
                } else {
                    Log.e("LineChart_Activity", "Error getting symptoms subcollection: ", task.getException());
                }
            }
        });
    }
}
