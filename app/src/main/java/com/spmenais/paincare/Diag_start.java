package com.spmenais.paincare;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.spmenais.paincare.R;

public class Diag_start extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnostic_main);

        // Find the "Go" button by its ID
        AppCompatButton goButton = findViewById(R.id.GoButton);

        // Set an OnClickListener to the button
        goButton.setOnClickListener(v -> {
            // Create an Intent to start the StartTestActivity
            Intent intent = new Intent(Diag_start.this, DiagTest_Activity .class);
            startActivity(intent);
        });
    }
}
