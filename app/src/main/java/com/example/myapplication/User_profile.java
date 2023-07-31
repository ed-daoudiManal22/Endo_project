package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Authentification.Authentification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

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
        androidx.constraintlayout.widget.ConstraintLayout editProfileLayout = findViewById(R.id.editProfile);
        androidx.constraintlayout.widget.ConstraintLayout languageLayout = findViewById(R.id.language);
        androidx.constraintlayout.widget.ConstraintLayout shareLayout = findViewById(R.id.share);
        androidx.constraintlayout.widget.ConstraintLayout logoutLayout = findViewById(R.id.logout);

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
                Intent intent = new Intent(User_profile.this, HomeActivity.class);
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
        editProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_profile.this, EditProfile_Activity.class);
                startActivity(intent);
            }
        });
        languageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the language selection dialog when the language layout is clicked
                showLanguageSelectionDialog();
            }
        });
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_profile.this, UserPage_Activity.class);
                startActivity(intent);
            }
        });
        // Inside onCreate method after other click listeners
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the logout method when the logout layout is clicked
                logoutUser();
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
    // Method to show the language selection dialog
    private void showLanguageSelectionDialog() {
        final String[] languages = {"English", "French", "Arabic"};

        AlertDialog.Builder builder = new AlertDialog.Builder(User_profile.this);
        builder.setTitle("Choose a Language");
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedLanguageCode = getLanguageCodeFromName(languages[i]);
                if (selectedLanguageCode != null) {
                    changeLanguage(selectedLanguageCode);
                } else {
                    Toast.makeText(User_profile.this, "Language selection failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }
    // Helper method to get the language code based on the language name
    private String getLanguageCodeFromName(String languageName) {
        switch (languageName) {
            case "English":
                return "en";
            case "French":
                return "fr";
            default:
                return null;
        }
    }

    // Method to change the language
    private void changeLanguage(String languageCode) {
        // Save the selected language in SharedPreferences to persist the choice
        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", languageCode);
        editor.apply();

        // Update the app's locale
        Locale newLocale = new Locale(languageCode);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(newLocale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        // Restart the activity to apply the new language
        recreate();
    }

    private void logoutUser() {
        // Sign out the user from Firebase
        firebaseAuth.signOut();
        // Redirect the user to the login page or any other appropriate screen
        Intent intent = new Intent(User_profile.this, Authentification.class);
        // Add any flags if needed, for example, to clear the activity stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish the current activity so that the user cannot navigate back to it after logging out
    }

}
