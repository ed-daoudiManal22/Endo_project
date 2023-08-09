package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Authentification.Authentification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Locale;

public class User_profile extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private static final String TAG = "DeleteUserData";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        TextView userNameTextView = findViewById(R.id.userName);
        ImageView backButton = findViewById(R.id.backButton);
        androidx.constraintlayout.widget.ConstraintLayout remindersLayout = findViewById(R.id.reminders);
        androidx.constraintlayout.widget.ConstraintLayout editProfileLayout = findViewById(R.id.editProfile);
        androidx.constraintlayout.widget.ConstraintLayout languageLayout = findViewById(R.id.language);
        androidx.constraintlayout.widget.ConstraintLayout shareLayout = findViewById(R.id.share);
        androidx.constraintlayout.widget.ConstraintLayout logoutLayout = findViewById(R.id.logout);
        androidx.constraintlayout.widget.ConstraintLayout deleteAccountLayout = findViewById(R.id.deleteAccount);
        androidx.constraintlayout.widget.ConstraintLayout CalendarLayout = findViewById(R.id.calendar);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Check if the user is authenticated
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // Fetch additional user data from Firestore
            fetchUserDataFromFirestore(currentUser.getUid(), userNameTextView);
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
        // Inside onCreate method after other click listeners
        deleteAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the delete account dialog when the delete account layout is clicked
                showDeleteAccountDialog();
            }
        });
        CalendarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_profile.this, EventCalendar_Activity.class);
                startActivity(intent);
            }
        });
    }
    // Method to fetch user data from Firestore
    private void fetchUserDataFromFirestore(String userId, TextView userNameTextView) {
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
                }
            }
        });
    }
    // Method to show the language selection dialog
    private void showLanguageSelectionDialog() {
        final String[] languages = {"English", "French"};

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
    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(User_profile.this);
        builder.setTitle(getString(R.string.delete_account_title));
        builder.setMessage(getString(R.string.delete_account_message));

        // Inflate a custom layout for the dialog that contains the radio buttons
        View view = getLayoutInflater().inflate(R.layout.dialog_delete_account, null);
        RadioButton deleteDataRadioButton = view.findViewById(R.id.deleteDataRadioButton);
        RadioButton keepDataRadioButton = view.findViewById(R.id.keepDataRadioButton);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            // Disable the positive button initially until the user selects a radio button
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        });

        // Set a click listener for the positive button
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.delete_button_label), (dialogInterface, i) -> {
            // Check which radio button is selected and perform the corresponding action
            int selectedId = ((RadioGroup) view.findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
            if (selectedId == R.id.deleteDataRadioButton) {
                // Call the method to delete the user's data from Firestore
                deleteAccount();
            } else if (selectedId == R.id.keepDataRadioButton) {
                // Call the method to deactivate the user's account
                keepAccountData();
            } else {
                // Handle the case when neither radio button is selected (optional)
                Toast.makeText(User_profile.this, "Please select an option.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set a click listener for the negative button (Cancel button)
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), (dialogInterface, i) -> {
            // Dismiss the dialog when the "Cancel" button is clicked
            alertDialog.dismiss();
        });

        // Set a click listener for the radio buttons
        deleteDataRadioButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            // Enable the positive button when the user selects a radio button
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(isChecked || keepDataRadioButton.isChecked());
        });
        keepDataRadioButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            // Enable the positive button when the user selects a radio button
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(isChecked || deleteDataRadioButton.isChecked());
        });

        alertDialog.show();
    }
    private void keepAccountData() {
        if (currentUser != null) {
            // Delete the user's account
            currentUser.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Call the logout method to sign out the user and redirect to the login page
                    logoutUser();
                    Toast.makeText(User_profile.this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(User_profile.this, "Failed to delete account.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void deleteAccount() {
        if (currentUser != null) {
            if (currentUser != null) {
                // Step 1: Delete the profile picture after account deletion
                deleteProfilePicture(currentUser.getUid(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Profile picture deleted successfully, continue with the rest of the deletion process
                        deleteUserData(currentUser.getUid());
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Error deleting profile picture, show error message and do not proceed with data deletion
                        Toast.makeText(User_profile.this, "Failed to delete profile picture.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    private void deleteUserData(String userId) {
        // Step 2: Delete subcollections (e.g., "reminders", "symptoms", "events")
        deleteSubcollections(userId, "reminders");
        deleteSubcollections(userId, "symptoms");
        deleteSubcollections(userId, "events");

        // Step 3: Delete the main document from the "Users" collection
        DocumentReference userDocumentRef = FirebaseFirestore.getInstance().collection("Users").document(userId);
        userDocumentRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Step 4: Delete the user's account
                currentUser.delete().addOnCompleteListener(accountDeletionTask -> {
                    if (accountDeletionTask.isSuccessful()) {
                        // Call the logout method to sign out the user and redirect to the login page
                        logoutUser();
                        Toast.makeText(User_profile.this, "Account and data deleted successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(User_profile.this, "Failed to delete account and data.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(User_profile.this, "Failed to delete account and data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSubcollections(String userId, String subcollectionName) {
        CollectionReference subcollectionRef = FirebaseFirestore.getInstance()
                .collection("Users").document(userId).collection(subcollectionName);

        subcollectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Delete each document in the subcollection
                    document.getReference().delete();
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
    private void deleteProfilePicture(String userId, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        // Get the user document from Firestore
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String imageUrl = documentSnapshot.getString("imageUrl");
                Log.d("DeleteProfile", "imageUrl: " + imageUrl);
                // Check if the image URL is the default image URL
                String defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/endo-project-1acae.appspot.com/o/profile_images%2Funknown_pic.jpg?alt=media&token=41f82f66-f50e-44d3-b020-07487bedeba7";
                if (!TextUtils.isEmpty(imageUrl) && !imageUrl.equals(defaultImageUrl)) {
                    StorageReference storageRef = storage.getReference();
                    StorageReference photoRef = storageRef.child("profile_images/" + userId + ".jpg");
                    Log.d("DeleteProfile", "photoRef: " + photoRef.getPath());
                    photoRef.delete().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
                } else {
                    // The image is the default image or empty, so we don't need to delete it
                    Log.d("DeleteProfile", "Profile picture is the default image or empty.");
                    successListener.onSuccess(null); // Call successListener directly since there's no deletion needed
                }
            } else {
                Log.d("DeleteProfile", "User document does not exist.");
                failureListener.onFailure(new Exception("User document does not exist.")); // Call failureListener with an error message
            }
        }).addOnFailureListener(e -> {
            // An error occurred while retrieving the user document
            Log.d("DeleteProfile", "Error retrieving user document: " + e.getMessage());
            failureListener.onFailure(e); // Call failureListener with the exception
        });
    }
}
