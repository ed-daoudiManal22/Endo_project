package com.spmenais.paincare.Authentification;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.spmenais.paincare.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String DEFAULT_PROFILE_IMAGE_URL =
            "https://firebasestorage.googleapis.com/v0/b/endo-project-1acae.appspot.com/o/profile_images%2Funknown_pic.jpg?alt=media&token=41f82f66-f50e-44d3-b020-07487bedeba7";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        Button signUpButton = findViewById(R.id.signUpButton);
        TextView loginLink = findViewById(R.id.loginLink);

        signUpButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (name.isEmpty()) {
                nameInput.setError("Please enter your name");
                nameInput.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                emailInput.setError("Please enter your email");
                emailInput.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                passwordInput.setError("Please enter a password");
                passwordInput.requestFocus();
                return;
            }

            if (confirmPassword.isEmpty()) {
                confirmPasswordInput.setError("Please confirm your password");
                confirmPasswordInput.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                confirmPasswordInput.requestFocus();
                return;
            }

            signUpUser(name, email, password);
        });

        loginLink.setOnClickListener(v -> {
            // Redirect to login activity
            Intent intent = new Intent(SignUpActivity.this, logInActivity.class);
            startActivity(intent);
        });
    }private void signUpUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            updateUserProfile(name, user);
                        }
                    } else {
                        handleSignUpException(task);
                    }
                });
    }

    private void updateUserProfile(String name, FirebaseUser user) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sendEmailVerification(user);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to update user profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        createUserInFirestore(user);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createUserInFirestore(FirebaseUser user) {
        String name = user.getDisplayName();
        String email = user.getEmail();

        // Create a user object with name and email
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("name", name);
        newUser.put("email", email);
        newUser.put("imageUrl", DEFAULT_PROFILE_IMAGE_URL);

        // Add the user object to the "users" collection
        db.collection("Users")
                .document(user.getUid())
                .set(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, Email_verification.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to register user in Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleSignUpException(Task<AuthResult> task) {
        try {
            throw Objects.requireNonNull(task.getException());
        } catch (FirebaseAuthWeakPasswordException e) {
            passwordInput.setError("Weak password. Please enter a stronger password.");
            passwordInput.requestFocus();
        } catch (FirebaseAuthInvalidCredentialsException e) {
            emailInput.setError("Invalid email address");
            emailInput.requestFocus();
        } catch (FirebaseAuthUserCollisionException e) {
            emailInput.setError("Email address already in use");
            emailInput.requestFocus();
        } catch (Exception e) {
            Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
        }
    }

}
