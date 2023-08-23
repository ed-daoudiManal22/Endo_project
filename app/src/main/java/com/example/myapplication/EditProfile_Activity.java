package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfile_Activity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText birthdayEditText;

    private Uri imageUri;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        profileImageView = findViewById(R.id.iv_profile_fragment);
        ImageView selectImageButton = findViewById(R.id.btn_profile_image_change);
        nameEditText = findViewById(R.id.nameTxt);
        emailEditText = findViewById(R.id.emailTxt);
        birthdayEditText = findViewById(R.id.birthdayTxt);
        Button saveButton = findViewById(R.id.save_button);

        // Populate user data into EditText fields
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = null;
        if (currentUser != null) {
            userId = currentUser.getUid();
        }
        DocumentReference userRef = null;
        if (userId != null) {
            userRef = db.collection("Users").document(userId);
        }
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String birthday = documentSnapshot.getString("birthday");
                String imageUrl = documentSnapshot.getString("imageUrl");

                nameEditText.setText(name);
                emailEditText.setText(email);
                birthdayEditText.setText(birthday);

                if (imageUrl != null) {
                    // Load the profile image using Glide or your preferred image loading library
                    Glide.with(EditProfile_Activity.this)
                            .load(imageUrl)
                            .into(profileImageView);
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(EditProfile_Activity.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show());

        selectImageButton.setOnClickListener(v -> openImagePicker());

        birthdayEditText.setOnClickListener(v -> showDatePicker());

        String finalUserId = userId;
        saveButton.setOnClickListener(v -> saveProfile(finalUserId));
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    String selectedDate = dayOfMonth1 + "/" + (monthOfYear + 1) + "/" + year1;
                    birthdayEditText.setText(selectedDate);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void saveProfile(String userId) {
        final String name = nameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String birthday = birthdayEditText.getText().toString();

        if (name.isEmpty() || email.isEmpty() || birthday.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            final String imageFileName = userId + ".jpg";
            final StorageReference imageRef = storageRef.child(imageFileName);

            // Upload the new image
            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                // Get the old image URL from the document
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId1 = currentUser.getUid();
                    DocumentReference userRef = db.collection("Users").document(userId1);
                    userRef.get().addOnSuccessListener(documentSnapshot -> {
                        String oldImageUrl = documentSnapshot.getString("imageUrl");

                        // Update the user data with the new image URL
                        saveUserData(name, email, birthday, imageUrl);
                    });
                }
            })).addOnFailureListener(e -> Toast.makeText(EditProfile_Activity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show());
        } else {
            // If no new image is selected, save user data without updating the image URL
            saveUserData(name, email, birthday, null);
        }
    }

    private void saveUserData(String name, String email, String birthday, @Nullable String imageUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("Users").document(userId);

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("birthday", birthday);

        if (imageUrl != null) {
            user.put("imageUrl", imageUrl);
        }

        userRef.update(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfile_Activity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    // Finish the activity and go back to the previous screen
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EditProfile_Activity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }
}