package com.example.myapplication.BasicUsersInfos;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.HomeActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Choices_Activity extends AppCompatActivity {
    private LinearLayout button1, button2, button3;
    private ImageButton backButton;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choices);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Find views
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        backButton = findViewById(R.id.backButton);

        // Set click listeners for buttons
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeChoice("Track my period");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeChoice("Try to conceive");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeChoice("Follow my pregnancy");
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

    private void storeChoice(String choice) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        if (!userId.isEmpty()) {
            firestore.collection("Users").document(userId)
                    .update("choice", choice)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Choices_Activity.this, "Choice stored successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Choices_Activity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(Choices_Activity.this, "Failed to store choice", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle the case when the user ID is empty or not available
            Toast.makeText(Choices_Activity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }
}
