package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GoogleLoginActivity extends AppCompatActivity {
    private Button googleSignInButton;
    private Button regularLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_with_google);

        googleSignInButton = findViewById(R.id.googleSignInButton);
        regularLoginButton = findViewById(R.id.regularLoginButton);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Google sign-in functionality here
                // TODO: Implement Google sign-in logic
            }
        });

        regularLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to regular login activity
                Intent intent = new Intent(GoogleLoginActivity.this, logInActivity.class);
                startActivity(intent);
            }
        });
    }
}
