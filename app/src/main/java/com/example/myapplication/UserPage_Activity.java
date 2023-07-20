package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserPage_Activity extends AppCompatActivity {
    private Button quizButton;
    private Button setReminderButton;
    private Button diagnosticsButton;
    private Button calendarButton;
    private Button myInfoButton;
    private Button EndoButton;
    private Button SymptomButton;
    private Button ForumButton;
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page);

        // Initialize views
        quizButton = findViewById(R.id.button1);
        setReminderButton = findViewById(R.id.button2);
        diagnosticsButton = findViewById(R.id.button3);
        calendarButton = findViewById(R.id.button4);
        myInfoButton = findViewById(R.id.button5);
        EndoButton = findViewById(R.id.button6);
        SymptomButton = findViewById(R.id.button7);
        ForumButton = findViewById(R.id.button8);
        welcomeTextView = findViewById(R.id.textView);

        // Retrieve the current user from Firebase Authentication
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userName = currentUser.getDisplayName();

            if (userName != null && !userName.isEmpty()) {
                String welcomeMessage = "Welcome back " + userName + "!";
                welcomeTextView.setText(welcomeMessage);
            }
        }

        // Set up click listeners for the buttons
        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPage_Activity.this, Quiz_main.class);
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
                Intent intent = new Intent(UserPage_Activity.this, Diag_start.class);
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
                Intent intent = new Intent(UserPage_Activity.this, User_profile.class);
                startActivity(intent);
            }
        });

        EndoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPage_Activity.this, Endo_InfoActivity.class);
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
