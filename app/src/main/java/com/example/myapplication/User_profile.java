package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class User_profile extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        TextView userNameTextView = findViewById(R.id.userName);
        TextView userEmailTextView = findViewById(R.id.userEmail);
        ImageView backButton = findViewById(R.id.backButton);
        androidx.constraintlayout.widget.ConstraintLayout remindersLayout = findViewById(R.id.reminders);
        androidx.constraintlayout.widget.ConstraintLayout takeTestLayout = findViewById(R.id.takeTest);
        androidx.constraintlayout.widget.ConstraintLayout languageLayout = findViewById(R.id.language);
        androidx.constraintlayout.widget.ConstraintLayout shareLayout = findViewById(R.id.share);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Check if the user is authenticated
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // Fetch additional user data from Firestore
            fetchUserDataFromFirestore(currentUser.getUid(), userNameTextView, userEmailTextView);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_profile.this, UserPage_Activity.class);
                startActivity(intent);
            }
        });

        remindersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_profile.this, ReminderActivity.class);
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
        languageLayout.setOnClickListener(new View.OnClickListener() {
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
    // Method to fetch user data from Firestore
    private void fetchUserDataFromFirestore(String userId, TextView userNameTextView, TextView userEmailTextView) {
        DocumentReference userRef = firestore.collection("Users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // User data exists, update the TextViews
                    String userName = document.getString("name");
                    String userEmail = document.getString("email");

                    // Set the user's name and email to the respective TextViews
                    userNameTextView.setText(userName);
                    userEmailTextView.setText(userEmail);
                }
            }
        });
    }
}
