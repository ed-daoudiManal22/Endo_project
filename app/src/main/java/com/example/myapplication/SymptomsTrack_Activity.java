package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class SymptomsTrack_Activity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView leftIcon;
    private TextView currentDateTextView;
    private TextView cycleDayTextView;
    private ImageView rightIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptomstracking);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Left Icon
        leftIcon = findViewById(R.id.leftIcon);
        leftIcon.setOnClickListener(v -> onBackPressed());

        // Current Date and Cycle Day
        currentDateTextView = findViewById(R.id.currentDateTextView);
        cycleDayTextView = findViewById(R.id.cycleDayTextView);
        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        currentDateTextView.setText(currentDate);

        // Right Icon
        rightIcon = findViewById(R.id.rightIcon);
        rightIcon.setOnClickListener(v -> showHelpDialog());

        // Rest of your code...
    }

    private void showHelpDialog() {
        // Implement your help dialog logic here
    }
    @Override
    public void onBackPressed() {
        // Start the home activity
        Intent intent = new Intent(this, UserPage_Activity.class);
        startActivity(intent);
        finish(); // Optional: Finish the current activity if you don't want to keep it in the back stack
    }
}
