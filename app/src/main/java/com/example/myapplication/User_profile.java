package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class User_profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        androidx.constraintlayout.widget.ConstraintLayout notificationLayout = findViewById(R.id.notifications);
        androidx.constraintlayout.widget.ConstraintLayout calendarLayout = findViewById(R.id.calendar);
        androidx.constraintlayout.widget.ConstraintLayout takeTestLayout = findViewById(R.id.takeTest);
        androidx.constraintlayout.widget.ConstraintLayout downloadLayout = findViewById(R.id.download);
        androidx.constraintlayout.widget.ConstraintLayout shareLayout = findViewById(R.id.share);

        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_profile.this, ReminderActivity.class);
                startActivity(intent);
            }
        });
        calendarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_profile.this, LineChart_Activity.class);
                startActivity(intent);
            }
        });
        takeTestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_profile.this, Diag_start.class);
                startActivity(intent);
            }
        });
        downloadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_profile.this, UserPage_Activity.class);
                startActivity(intent);
            }
        });
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_profile.this, UserPage_Activity.class);
                startActivity(intent);
            }
        });
    }
}
