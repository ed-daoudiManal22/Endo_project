package com.example.myapplication.BasicUsersInfos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.HomeActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class UserAge_Activity extends AppCompatActivity {
    private DatePicker lastPeriodDatePicker;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_age);

        // Initialize Firestore and Firebase Auth
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize views
        lastPeriodDatePicker = findViewById(R.id.ageDatePicker);
        Button nextButton = findViewById(R.id.nextbutton);

        // Set up click listener for the Next button
        nextButton.setOnClickListener(v -> {
            // Get the selected date from the DatePicker
            int day = lastPeriodDatePicker.getDayOfMonth();
            int month = lastPeriodDatePicker.getMonth() + 1; // Months are zero-based, so add 1
            int year = lastPeriodDatePicker.getYear();

            // Store the selected date in Firestore
            storeBirthday(day, month, year);
        });
    }

    private void storeBirthday(int day, int month, int year) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = null;
        if (currentUser != null) {
            userId = currentUser.getUid();
        }
        if (userId != null) {
            if (!userId.isEmpty()) {
                // Create a Date object from the selected day, month, and year
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day); // Months are zero-based, so subtract 1
                Date birthday = calendar.getTime();

                // Store the birthday in Firestore
                firestore.collection("Users").document(userId)
                        .update("birthday", birthday)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(UserAge_Activity.this, "Birthday stored successfully", Toast.LENGTH_SHORT).show();
                            // Move to the next activity
                            Intent intent = new Intent(UserAge_Activity.this, HomeActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> Toast.makeText(UserAge_Activity.this, "Failed to store birthday", Toast.LENGTH_SHORT).show());
            } else {
                // Handle the case when the user ID is empty or not available
                Toast.makeText(UserAge_Activity.this, "User ID not found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
