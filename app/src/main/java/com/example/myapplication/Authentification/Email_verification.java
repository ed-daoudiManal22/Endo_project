package com.example.myapplication.Authentification;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Email_verification extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification);

        mAuth = FirebaseAuth.getInstance();
        Button resendEmailButton = findViewById(R.id.resendEmailButton);
        Button loginButton = findViewById(R.id.loginButton);

        resendEmailButton.setOnClickListener(v -> sendVerificationEmail());

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(Email_verification.this, logInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Email_verification.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Email_verification.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
