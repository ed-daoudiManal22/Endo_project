package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class SymptomsTrack_Activity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView leftIcon;
    private TextView currentDateTextView;
    private TextView cycleDayTextView;
    private ImageView rightIcon;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptoms_tracking);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Left Icon
        leftIcon = findViewById(R.id.leftIcon);
        leftIcon.setOnClickListener(v -> startActivity(new Intent(SymptomsTrack_Activity.this, UserPage_Activity.class)));

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


    }
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
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

    private void saveWaterToFirestore(String currentDate, double waterConsumed) {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("Users").document(userId)
                .collection("water")
                .document(currentDate)
                .set(new HashMap<String, Object>() {{
                    put("waterConsumption", waterConsumed);
                }})
                .addOnSuccessListener(aVoid -> {
                    System.out.println("water saved successfully");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error while saving water: " + e.getMessage());
                });
    }

    interface InputDialogListener {
        void onInputEntered(DialogInterface dialog, String value);
    }
}
