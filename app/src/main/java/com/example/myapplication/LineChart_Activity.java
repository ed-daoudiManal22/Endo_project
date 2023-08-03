package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        // Get the context of the activity
        final Context context = getApplicationContext();

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
                    // end of graph implementation ------------------------------------------------------------

                    // Create a list to store all pain locations from all documents and to store pie chart entries (slices)
                    List<String> allPainLocations = new ArrayList<>();
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

                    // Set the pain locations and their percentages in the respective TextViews
                    Set<String> uniquePainLocations = new HashSet<>(allPainLocations);
                    for (String location : uniquePainLocations) {
                        int occurrences = painLocationOccurrences.get(location);
                        float percentage = (occurrences * 100f) / totalPainLocations;

                        int resourceId = context.getResources().getIdentifier(location, "string", getPackageName());
                        String localizedLocation;
                        if (resourceId != 0) {
                            // Use the localized name from the strings.xml if it exists
                            localizedLocation = context.getString(resourceId);
                        } else {
                            // Use the original location string if the resource name is not found
                            localizedLocation = location;
                        }
                        // Add the PieEntry for each pain location and percentage with the localized name
                        pieEntries.add(new PieEntry(percentage, localizedLocation));
                    }
                    // Create a dataset for the PieChart with the entries and customize it
                    PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
                    pieDataSet.setColors(pastelColors);

                    // Create a PieData object with the dataset
                    PieData pieData = new PieData(pieDataSet);
                    pieData.setValueTextSize(12f); // Adjust the text size of the values inside the slices

                    // Get the PieChart view from the layout
                    PieChart pieChart = findViewById(R.id.pieChart);

                    // Set the PieData to the chart and refresh it
                    pieChart.setData(pieData);
                    pieChart.getDescription().setEnabled(false); // Disable the description
                    pieChart.setDrawEntryLabels(false); // Disable labels inside the slices
                    pieChart.setDrawHoleEnabled(false); // Disable the center hole
                    pieChart.invalidate();
                    //end pain location Chart --------------------------------------------------

                    // Create a list to store all symptoms from all documents and to store pie chart entries (slices)
                    List<String> allSymptoms = new ArrayList<>();
                    List<PieEntry> pieEntriesSymptoms = new ArrayList<>();

                    for (DocumentSnapshot document : task.getResult()) {
                        // Get the symptoms array from the document
                        Object symptomsObject = document.get("symptoms");

                        if (symptomsObject instanceof List) {
                            List<String> symptoms = (List<String>) symptomsObject;

                            // Add all symptoms to the list
                            if (symptoms != null) {
                                allSymptoms.addAll(symptoms);
                            }
                        } else if (symptomsObject instanceof String) {
                            // Handle the case when "symptoms" is a single String instead of a List
                            String singleSymptom = (String) symptomsObject;
                            allSymptoms.add(singleSymptom);
                        }
                    }

                    // Calculate the occurrences of each symptom
                    TreeMap<String, Integer> symptomOccurrences = new TreeMap<>();
                    for (String symptom : allSymptoms) {
                        symptomOccurrences.put(symptom, symptomOccurrences.getOrDefault(symptom, 0) + 1);
                    }

                    // Calculate the total number of symptoms
                    int totalSymptoms = allSymptoms.size();

                    // Set the symptoms and their percentages in the respective TextViews
                    Set<String> uniqueSymptoms = new HashSet<>(allSymptoms);
                    for (String symptom : uniqueSymptoms) {
                        int occurrences = symptomOccurrences.get(symptom);
                        float percentage = (occurrences * 100f) / totalSymptoms;

                        // Add the PieEntry for each symptom and percentage
                        pieEntriesSymptoms.add(new PieEntry(percentage, symptom));
                    }
                    // Create a dataset for the PieChart with the entries and customize it
                    PieDataSet pieDataSetSymptoms = new PieDataSet(pieEntriesSymptoms, "");
                    pieDataSetSymptoms.setColors(pastelColors);

                    // Create a PieData object with the dataset
                    PieData pieDataSymptoms = new PieData(pieDataSetSymptoms);
                    pieDataSymptoms.setValueTextSize(12f); // Adjust the text size of the values inside the slices

                    // Get the PieChart view from the layout
                    PieChart pieChartSymptoms = findViewById(R.id.pieChartsymptoms);

                    // Set the PieData to the chart and refresh it
                    pieChartSymptoms.setData(pieDataSymptoms);
                    pieChartSymptoms.getDescription().setEnabled(false); // Disable the description
                    pieChartSymptoms.setDrawEntryLabels(false); // Disable labels inside the slices
                    pieChartSymptoms.setDrawHoleEnabled(false); // Disable the center hole
                    pieChartSymptoms.invalidate();
                    //end symptoms Chart --------------------------------------------------

                    // Create a list to store all painWorse from all documents and to store pie chart entries (slices)
                    List<String> allPainWorse = new ArrayList<>();
                    List<PieEntry> pieEntriesPainWorse = new ArrayList<>();

                    for (DocumentSnapshot document : task.getResult()) {
                        // Get the painWorse array from the document
                        Object painWorseObject = document.get("What Made Your Pain Worse?");

                        if (painWorseObject instanceof List) {
                            List<String> painWorse = (List<String>) painWorseObject;

                            // Add all painWorse to the list
                            if (painWorse != null) {
                                allPainWorse.addAll(painWorse);
                            }
                        } else if (painWorseObject instanceof String) {
                            // Handle the case when "painWorse" is a single String instead of a List
                            String singlePainWorse = (String) painWorseObject;
                            allPainWorse.add(singlePainWorse);
                        }
                    }

                    // Calculate the occurrences of each painWorse
                    TreeMap<String, Integer> painWorseOccurrences = new TreeMap<>();
                    for (String painWorse : allPainWorse) {
                        painWorseOccurrences.put(painWorse, painWorseOccurrences.getOrDefault(painWorse, 0) + 1);
                    }

                    // Calculate the total number of painWorse
                    int totalPainWorse = allPainWorse.size();

                    // Set the painWorse and their percentages in the respective TextViews
                    Set<String> uniquePainWorse = new HashSet<>(allPainWorse);
                    for (String painWorse : uniquePainWorse) {
                        int occurrences = painWorseOccurrences.get(painWorse);
                        float percentage = (occurrences * 100f) / totalPainWorse;

                        // Add the PieEntry for each painWorse and percentage
                        pieEntriesPainWorse.add(new PieEntry(percentage, painWorse));
                    }
                    // Create a dataset for the PieChart with the entries and customize it
                    PieDataSet pieDataSetPainWorse = new PieDataSet(pieEntriesPainWorse, "");
                    pieDataSetPainWorse.setColors(pastelColors);

                    // Create a PieData object with the dataset
                    PieData pieDataPainWorse = new PieData(pieDataSetPainWorse);
                    pieDataPainWorse.setValueTextSize(12f); // Adjust the text size of the values inside the slices

                    // Get the PieChart view from the layout
                    PieChart pieChartPainWorse = findViewById(R.id.pieChartPainWorse);

                    // Set the PieData to the chart and refresh it
                    pieChartPainWorse.setData(pieDataPainWorse);
                    pieChartPainWorse.getDescription().setEnabled(false); // Disable the description
                    pieChartPainWorse.setDrawEntryLabels(false); // Disable labels inside the slices
                    pieChartPainWorse.setDrawHoleEnabled(false); // Disable the center hole
                    pieChartPainWorse.invalidate();
                    //end painWorse Chart---------------------------------------------------

                    // Create a list to store all feelings from all documents and to store pie chart entries (slices)
                    List<String> allFeelings = new ArrayList<>();
                    List<PieEntry> pieEntriesFeelings = new ArrayList<>();

                    for (DocumentSnapshot document : task.getResult()) {
                        // Get the feelings array from the document
                        Object feelingsObject = document.get("How You Feel Today?");

                        if (feelingsObject instanceof List) {
                            List<String> feelings = (List<String>) feelingsObject;

                            // Add all feelings to the list
                            if (feelings != null) {
                                allFeelings.addAll(feelings);
                            }
                        } else if (feelingsObject instanceof String) {
                            // Handle the case when "feelings" is a single String instead of a List
                            String singleFeeling = (String) feelingsObject;
                            allFeelings.add(singleFeeling);
                        }
                    }

                    // Calculate the occurrences of each feeling
                    TreeMap<String, Integer> feelingOccurrences = new TreeMap<>();
                    for (String feeling : allFeelings) {
                        feelingOccurrences.put(feeling, feelingOccurrences.getOrDefault(feeling, 0) + 1);
                    }

                    // Calculate the total number of feelings
                    int totalFeelings = allFeelings.size();

                    // Set the feelings and their percentages in the respective TextViews
                    Set<String> uniqueFeelings = new HashSet<>(allFeelings);
                    for (String feeling : uniqueFeelings) {
                        int occurrences = feelingOccurrences.get(feeling);
                        float percentage = (occurrences * 100f) / totalFeelings;

                        // Add the PieEntry for each feeling and percentage
                        pieEntriesFeelings.add(new PieEntry(percentage, feeling));
                    }
                    // Create a dataset for the PieChart with the entries and customize it
                    PieDataSet pieDataSetFeelings = new PieDataSet(pieEntriesFeelings, "");
                    pieDataSetFeelings.setColors(pastelColors);

                    // Create a PieData object with the dataset
                    PieData pieDataFeelings = new PieData(pieDataSetFeelings);
                    pieDataFeelings.setValueTextSize(12f); // Adjust the text size of the values inside the slices

                    // Get the PieChart view from the layout
                    PieChart pieChartFeelings = findViewById(R.id.pieChartFeelings);

                    // Set the PieData to the chart and refresh it
                    pieChartFeelings.setData(pieDataFeelings);
                    pieChartFeelings.getDescription().setEnabled(false); // Disable the description
                    pieChartFeelings.setDrawEntryLabels(false); // Disable labels inside the slices
                    pieChartFeelings.setDrawHoleEnabled(false); // Disable the center hole
                    pieChartFeelings.invalidate();
                    //end feelings Chart---------------------------------------------------------

                    // Create a list to store all medications from all documents and to store pie chart entries (slices)
                    List<String> allMedications = new ArrayList<>();
                    List<PieEntry> pieEntriesMedications = new ArrayList<>();

                    for (DocumentSnapshot document : task.getResult()) {
                        // Get the medications object from the document
                        Object medicationsObject = document.get("What Medication Did You Try for Your Pain?");

                        if (medicationsObject instanceof List) {
                            List<String> medications = (List<String>) medicationsObject;

                            // Add all medications to the list
                            if (medications != null) {
                                allMedications.addAll(medications);
                            }
                        } else if (medicationsObject instanceof String) {
                            // Handle the case when "medicationsObject" is a single String instead of a List
                            String singleMedication = (String) medicationsObject;
                            allMedications.add(singleMedication);
                        }
                    }

                    // Calculate the occurrences of each medication
                    TreeMap<String, Integer> medicationOccurrences = new TreeMap<>();
                    for (String medication : allMedications) {
                        medicationOccurrences.put(medication, medicationOccurrences.getOrDefault(medication, 0) + 1);
                    }

                    // Calculate the total number of medications
                    int totalMedications = allMedications.size();

                    // Set the medications and their percentages in the respective TextViews
                    Set<String> uniqueMedications = new HashSet<>(allMedications);
                    for (String medication : uniqueMedications) {
                        int occurrences = medicationOccurrences.get(medication);
                        float percentage = (occurrences * 100f) / totalMedications;

                        // Add the PieEntry for each medication and percentage
                        pieEntriesMedications.add(new PieEntry(percentage, medication));
                    }
                    // Create a dataset for the PieChart with the entries and customize it
                    PieDataSet pieDataSetMedications = new PieDataSet(pieEntriesMedications, "");
                    pieDataSetMedications.setColors(pastelColors);

                    // Create a PieData object with the dataset
                    PieData pieDataMedications = new PieData(pieDataSetMedications);
                    pieDataMedications.setValueTextSize(12f); // Adjust the text size of the values inside the slices

                    // Get the PieChart view from the layout
                    PieChart pieChartMedications = findViewById(R.id.pieChartMedications);

                    // Set the PieData to the chart and refresh it
                    pieChartMedications.setData(pieDataMedications);
                    pieChartMedications.getDescription().setEnabled(false); // Disable the description
                    pieChartMedications.setDrawEntryLabels(false); // Disable labels inside the slices
                    pieChartMedications.setDrawHoleEnabled(false); // Disable the center hole
                    pieChartMedications.invalidate();
                    //end medications Chart-------------------------------------------

                    /*// Create a list to store all symptoms from all documents
                    List<String> allSymptoms = new ArrayList<>();

                    for (DocumentSnapshot document : task.getResult()) {
                        // Get the symptoms array from the document
                        Object symptomsObject = document.get("symptoms");

                        if (symptomsObject instanceof List) {
                            List<String> symptoms = (List<String>) symptomsObject;

                            // Add all symptoms to the list
                            if (symptoms != null) {
                                allSymptoms.addAll(symptoms);
                            }
                        } else if (symptomsObject instanceof String) {
                            // Handle the case when "pain Location" is a single String instead of a List
                            String singlesymptoms = (String) symptomsObject;
                            allPainLocations.add(singlesymptoms);
                        }
                    }

                    // Calculate the occurrences of each symptom
                    TreeMap<String, Integer> symptomOccurrences = new TreeMap<>();
                    for (String symptom : allSymptoms) {
                        symptomOccurrences.put(symptom, symptomOccurrences.getOrDefault(symptom, 0) + 1);
                    }

                    // Calculate the total number of symptoms
                    int totalSymptoms = allSymptoms.size();

                    // Get references to the LinearLayout that will hold the TextViews
                    LinearLayout symptomsLayout = findViewById(R.id.symptomsLayout);

                    // Clear the LinearLayout in case it already has views (to avoid duplicates if you reload data)
                    symptomsLayout.removeAllViews();

                    // Set the symptoms and their percentages in the respective TextViews
                    Set<String> uniqueSymptoms = new HashSet<>(allSymptoms);
                    for (String symptom : uniqueSymptoms) {
                        int occurrences = symptomOccurrences.get(symptom);
                        float percentage = (occurrences * 100f) / totalSymptoms;

                        // Create a new TextView for each symptom and percentage
                        TextView symptomTextView = new TextView(context); // Use the context here
                        symptomTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));

                        // Set the text for the TextView
                        symptomTextView.setTypeface(null, Typeface.BOLD);
                        String symptomInfo = symptom + " : " + String.format(Locale.US, "%.1f%%", percentage);
                        symptomTextView.setText(symptomInfo);

                        // Add the TextView to the LinearLayout
                        symptomsLayout.addView(symptomTextView);
                    }
                    //end symptoms percentage

                    // Create a list to store all painWorse from all documents
                    List<String> allPainWorse = new ArrayList<>();

                    for (DocumentSnapshot document : task.getResult()) {
                        // Get the painWorse array from the document
                        Object painWorseObject = document.get("What Made Your Pain Worse?");

                        if (painWorseObject instanceof List) {
                            List<String> painWorse = (List<String>) painWorseObject;

                            // Add all painWorse to the list
                            if (painWorse != null) {
                                allPainWorse.addAll(painWorse);
                            }
                        } else if (painWorseObject instanceof String) {
                            // Handle the case when "painWorse" is a single String instead of a List
                            String singlePainWorse = (String) painWorseObject;
                            allPainWorse.add(singlePainWorse);
                        }
                    }

                    // Calculate the occurrences of each painWorse
                    TreeMap<String, Integer> painWorseOccurrences = new TreeMap<>();
                    for (String painWorse : allPainWorse) {
                        painWorseOccurrences.put(painWorse, painWorseOccurrences.getOrDefault(painWorse, 0) + 1);
                    }

                    // Calculate the total number of painWorse
                    int totalPainWorse = allPainWorse.size();

                    // Get references to the LinearLayout that will hold the TextViews
                    LinearLayout painWorseLayout = findViewById(R.id.painWorseLayout);

                    // Clear the LinearLayout in case it already has views (to avoid duplicates if you reload data)
                    painWorseLayout.removeAllViews();

                    // Set the painWorse and their percentages in the respective TextViews
                    Set<String> uniquePainWorse = new HashSet<>(allPainWorse);
                    for (String painWorse : uniquePainWorse) {
                        int occurrences = painWorseOccurrences.get(painWorse);
                        float percentage = (occurrences * 100f) / totalPainWorse;

                        // Create a new TextView for each painWorse and percentage
                        TextView painWorseTextView = new TextView(context); // Use the context here
                        painWorseTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));

                        // Set the text for the TextView
                        painWorseTextView.setTypeface(null, Typeface.BOLD);
                        String painWorseInfo = painWorse + " : " + String.format(Locale.US, "%.1f%%", percentage);
                        painWorseTextView.setText(painWorseInfo);

                        // Add the TextView to the LinearLayout
                        painWorseLayout.addView(painWorseTextView);
                    }
                    //end painWorse percentage

                    // Create a list to store all feelings from all documents
                    List<String> allFeelings = new ArrayList<>();

                    for (DocumentSnapshot document : task.getResult()) {
                        // Get the feelings array from the document
                        Object feelingsObject = document.get("How You Feel Today?");

                        if (feelingsObject instanceof List) {
                            List<String> feelings = (List<String>) feelingsObject;

                            // Add all feelings to the list
                            if (feelings != null) {
                                allFeelings.addAll(feelings);
                            }
                        } else if (feelingsObject instanceof String) {
                            // Handle the case when "feelings" is a single String instead of a List
                            String singleFeeling = (String) feelingsObject;
                            allFeelings.add(singleFeeling);
                        }
                    }

                    // Calculate the occurrences of each feelings
                    TreeMap<String, Integer> feelingsOccurrences = new TreeMap<>();
                    for (String feeling : allFeelings) {
                        feelingsOccurrences.put(feeling, feelingsOccurrences.getOrDefault(feeling, 0) + 1);
                    }

                    // Calculate the total number of feelings
                    int totalFeelings = allFeelings.size();

                    // Get references to the LinearLayout that will hold the TextViews
                    LinearLayout feelingsLayout = findViewById(R.id.feelingsLayout);

                    // Clear the LinearLayout in case it already has views (to avoid duplicates if you reload data)
                    feelingsLayout.removeAllViews();

                    // Set the feelings and their percentages in the respective TextViews
                    Set<String> uniqueFeelings = new HashSet<>(allFeelings);
                    for (String feeling : uniqueFeelings) {
                        int occurrences = feelingsOccurrences.get(feeling);
                        float percentage = (occurrences * 100f) / totalFeelings;

                        // Create a new TextView for each feeling and percentage
                        TextView feelingTextView = new TextView(context); // Use the context here
                        feelingTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));

                        // Set the text for the TextView
                        feelingTextView.setTypeface(null, Typeface.BOLD);
                        String feelingInfo = feeling + " : " + String.format(Locale.US, "%.1f%%", percentage);
                        feelingTextView.setText(feelingInfo);

                        // Add the TextView to the LinearLayout
                        feelingsLayout.addView(feelingTextView);
                    }
                    //end feelings percentage

                    // Create a list to store all medications from all documents
                    List<String> allMedications = new ArrayList<>();

                    for (DocumentSnapshot document : task.getResult()) {
                        // Get the medications array from the document
                        Object medicationsObject = document.get("What Medication Did You Try for Your Pain?");

                        if (medicationsObject instanceof List) {
                            List<String> medications = (List<String>) medicationsObject;

                            // Add all medications to the list
                            if (medications != null) {
                                allMedications.addAll(medications);
                            }
                        } else if (medicationsObject instanceof String) {
                            // Handle the case when "medications" is a single String instead of a List
                            String singleMedication = (String) medicationsObject;
                            allMedications.add(singleMedication);
                        }
                    }

                    // Calculate the occurrences of each medications
                    TreeMap<String, Integer> medicationOccurrences = new TreeMap<>();
                    for (String medication : allMedications) {
                        medicationOccurrences.put(medication, medicationOccurrences.getOrDefault(medication, 0) + 1);
                    }

                    // Calculate the total number of medications
                    int totalMedications = allMedications.size();

                    // Get references to the LinearLayout that will hold the TextViews
                    LinearLayout medicationLayout = findViewById(R.id.MedicationsLayout);

                    // Clear the LinearLayout in case it already has views (to avoid duplicates if you reload data)
                    medicationLayout.removeAllViews();

                    // Set the medications and their percentages in the respective TextViews
                    Set<String> uniqueMedications = new HashSet<>(allMedications);
                    for (String medication : uniqueMedications) {
                        int occurrences = medicationOccurrences.get(medication);
                        float percentage = (occurrences * 100f) / totalMedications;

                        // Create a new TextView for each medication and percentage
                        TextView medicationTextView = new TextView(context); // Use the context here
                        medicationTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));

                        // Set the text for the TextView
                        medicationTextView.setTypeface(null, Typeface.BOLD);
                        String medicationInfo = medication + " : " + String.format(Locale.US, "%.1f%%", percentage);
                        medicationTextView.setText(medicationInfo);

                        // Add the TextView to the LinearLayout
                        medicationLayout.addView(medicationTextView);
                    }
                    //end medications percentage*/
                } else {
                    Log.e("LineChart_Activity", "Error getting symptoms subcollection: ", task.getException());
                }
            }
        });
    }
}
