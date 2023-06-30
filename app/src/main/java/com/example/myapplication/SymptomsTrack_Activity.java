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
        setContentView(R.layout.symptomstracking);

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

        //weight button
        AppCompatButton weightButton = findViewById(R.id.weightButton);
        weightButton.setOnClickListener(v -> showInputDialog("Enter Number", "weight", "Submit", (dialog, value) -> {
            int weight = Integer.parseInt(value);
            saveToFirestore("weight", weight);
        }));
        // Sleep Button
        AppCompatButton sleepButton = findViewById(R.id.sleepButton);
        sleepButton.setOnClickListener(v -> {
            showInputDialog("Enter Sleep Time", "sleepTime", "Next", (dialog, sleepTime) -> {
                showInputDialog("Enter Wake-up Time", "wakeUpTime", "Submit", (dialog1, wakeUpTime) -> {
                    saveSleepDataToFirestore(sleepTime, wakeUpTime);
                });
            });
        });

        // water button
        AppCompatButton waterButton = findViewById(R.id.waterButton);
        waterButton.setOnClickListener(v -> showInputDialog("Enter Water Consumption", "cups", "Submit", (dialog, cups) -> {
            int cupCount = Integer.parseInt(cups);
            double totalWaterConsumed = cupCount * 0.25;
            saveToFirestore("waterConsumption", totalWaterConsumed);
        }));
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
    private void showInputDialog(String title, String field, String positiveButtonLabel, final InputDialogListener listener) {
        final EditText editText = new EditText(this);
        editText.setHint("Enter a value");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(title)
                .setView(editText)
                .setPositiveButton(positiveButtonLabel, (dialog, which) -> {
                    String value = editText.getText().toString();
                    listener.onInputEntered(dialog, value);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
    private void saveToFirestore(String field, Object value) {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("Users").document(userId)
                .update(field, value)
                .addOnSuccessListener(aVoid -> {
                    // Data saved successfully
                })
                .addOnFailureListener(e -> {
                    // Handle error while saving data
                });
    }
    private void saveSleepDataToFirestore(String sleepTime, String wakeUpTime) {
        saveToFirestore("sleepTime", sleepTime);
        saveToFirestore("wakeUpTime", wakeUpTime);
    }

    interface InputDialogListener {
        void onInputEntered(DialogInterface dialog, String value);
    }
}
