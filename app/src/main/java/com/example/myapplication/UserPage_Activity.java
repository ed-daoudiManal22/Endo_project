package com.example.myapplication;

import android.content.Intent;
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
    private Button SymptomButton;
    private Button ForumButton;

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
        SymptomButton = findViewById(R.id.button7);
        ForumButton = findViewById(R.id.button8);

        // Set up click listeners for the buttons
        doctorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPage_Activity.this, SymptomsTrack_Activity.class);
                startActivity(intent);
            }
        });

        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPage_Activity.this, ReminderActivity.class);
                startActivity(intent);
            }
        });

        diagnosticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPage_Activity.this, QuizActivity.class);
                startActivity(intent);
            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPage_Activity.this, Menstrual_cycle_Activity.class);
                startActivity(intent);
            }
        });

        myInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPage_Activity.this, SymptomsTrack_Activity.class);
                startActivity(intent);
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPage_Activity.this, SymptomsTrack_Activity.class);
                startActivity(intent);
            }
        });
        SymptomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPage_Activity.this, SymptomsTrack_Activity.class);
                startActivity(intent);
            }
        });
        ForumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPage_Activity.this, Forum_Activity.class);
                startActivity(intent);
            }
        });
    }
}
