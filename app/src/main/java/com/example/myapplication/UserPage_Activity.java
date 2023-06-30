package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class UserPage_Activity extends AppCompatActivity {
    private Button doctorsButton;
    private Button setReminderButton;
    private Button diagnosticsButton;
    private Button calendarButton;
    private Button myInfoButton;
    private Button contactButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page);

        // Initialize views
        doctorsButton = findViewById(R.id.button1);
        setReminderButton = findViewById(R.id.button2);
        diagnosticsButton = findViewById(R.id.button3);
        calendarButton = findViewById(R.id.button4);
        myInfoButton = findViewById(R.id.button5);
        contactButton = findViewById(R.id.button6);

        // Set up click listeners for the buttons
        doctorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the Doctors button
                // Add your logic here
            }
        });

        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the Set Reminder button
                // Add your logic here
            }
        });

        diagnosticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the Diagnostics button
                // Add your logic here
            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the Calendar button
                // Add your logic here
            }
        });

        myInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the My Information button
                // Add your logic here
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the Contact button
                // Add your logic here
            }
        });
    }
}
