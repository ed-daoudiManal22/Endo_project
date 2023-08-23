package com.example.myapplication.Authentification;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.HomeActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class logInActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_with_email);

        emailInput = findViewById(R.id.emailinput);
        passwordInput = findViewById(R.id.passwordinput);
        Button loginButton = findViewById(R.id.loginbutton);
        TextView forgotPassword = findViewById(R.id.forgotpassword);
        TextView signUp = findViewById(R.id.signup);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                // Email is verified and login is successful
                                Intent intent = new Intent(logInActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Email is not verified
                                Toast.makeText(logInActivity.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Login failed, display an error message
                            Toast.makeText(logInActivity.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(logInActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(logInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}