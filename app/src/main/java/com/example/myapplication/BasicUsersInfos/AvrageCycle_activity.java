package com.example.myapplication.BasicUsersInfos;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.Endo_InfoActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AvrageCycle_activity extends AppCompatActivity{
    private NumberPicker cycleLengthPicker;
    private TextView daysTextView;
    private AppCompatButton nextButton;
    private ImageButton backButton;

    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.averagecycle);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Find views
        cycleLengthPicker = findViewById(R.id.cycleLengthPicker);
        daysTextView = findViewById(R.id.daysTextView);
        nextButton = findViewById(R.id.nextbutton);
        backButton = findViewById(R.id.backButton);

        // Set number picker properties
        cycleLengthPicker.setMinValue(24);
        cycleLengthPicker.setMaxValue(38);

        // Set click listener for the Next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected cycle length
                int cycleLength = cycleLengthPicker.getValue();

                // Store the answer in Firestore
                storeCycleLength(cycleLength);

                // Start the next activity
                Intent intent = new Intent(AvrageCycle_activity.this, LastPeriod_Activity.class);
                startActivity(intent);
            }
        });

        // Set click listener for the Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Finish the current activity and go back to the previous activity

            }
        });
    }

    private void storeCycleLength(int cycleLength) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        if (!userId.isEmpty()) {
            firestore.collection("Users").document(userId)
                    .update("cycleLength", cycleLength)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AvrageCycle_activity.this, "Cycle length stored successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AvrageCycle_activity.this, "Failed to store cycle length", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle the case when the user ID is empty or not available
            Toast.makeText(AvrageCycle_activity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }
}
