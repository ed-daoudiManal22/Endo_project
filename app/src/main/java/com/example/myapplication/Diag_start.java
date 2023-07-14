package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class Diag_start extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnostic_main);

        // Find the "Go" button by its ID
        AppCompatButton goButton = findViewById(R.id.GoButton);

        // Set an OnClickListener to the button
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the StartTestActivity
                Intent intent = new Intent(Diag_start.this, DiagTest_Activity .class);
                startActivity(intent);
            }
        });
    }
}
