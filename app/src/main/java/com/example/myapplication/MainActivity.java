package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Authentification.Authentification;

public class MainActivity extends AppCompatActivity {
    private Button startJourneyButton;
    private Button circularButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startJourneyButton = findViewById(R.id.startJourneyButton);
        circularButton = findViewById(R.id.circularButton);

        startJourneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Get Started" button click event
                startActivity(new Intent(MainActivity.this, Authentification.class));
                finish();
            }
        });

        circularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the circular button click event
                startActivity(new Intent(MainActivity.this, Authentification.class));
                finish();
            }
        });
    }

}
