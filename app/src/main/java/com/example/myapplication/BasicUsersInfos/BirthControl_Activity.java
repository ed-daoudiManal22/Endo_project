package com.example.myapplication.BasicUsersInfos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class BirthControl_Activity extends AppCompatActivity{
    private Spinner birthControlMethodPicker;
    private Button nextButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthcontrol);

        // Initialize Firestore and Firebase Auth
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize views
        birthControlMethodPicker = findViewById(R.id.birthControlMethodPicker);
        nextButton = findViewById(R.id.nextbutton);

        // Set up click listener for the Next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected birth control method
                String selectedMethod = birthControlMethodPicker.getSelectedItem().toString();

                // Store the selected method in Firestore
                storeBirthControlMethod(selectedMethod);
            }
        });
    }

    private void storeBirthControlMethod(String method) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getUid();
        if (!userId.isEmpty()) {
            firestore.collection("Users").document(userId)
                    .update("birthControlMethod", method)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(BirthControl_Activity.this, "Birth control method stored successfully", Toast.LENGTH_SHORT).show();
                            // Move to the next activity
                            Intent intent = new Intent(BirthControl_Activity.this, Choices_Activity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(BirthControl_Activity.this, "Failed to store birth control method", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle the case when the user ID is empty or not available
            Toast.makeText(BirthControl_Activity.this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }
}
