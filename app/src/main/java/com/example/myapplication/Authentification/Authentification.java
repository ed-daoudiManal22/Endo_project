package com.example.myapplication.Authentification;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.myapplication.R;

public class Authentification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentification);

        AppCompatButton loginWithEmailButton = findViewById(R.id.loginWithEmailButton);
        AppCompatButton loginWithGoogleButton = findViewById(R.id.loginWithGoogleButton);
        AppCompatButton signUpButton = findViewById(R.id.signUpButton);

        loginWithEmailButton.setOnClickListener(v -> {
            // Redirect to login_with_email view
            Intent intent = new Intent(Authentification.this, logInActivity.class);
            startActivity(intent);
        });

        loginWithGoogleButton.setOnClickListener(v -> {
            // Redirect to login_with_google view
            Intent intent = new Intent(Authentification.this, GoogleLoginActivity.class);
            startActivity(intent);
        });

        signUpButton.setOnClickListener(v -> {
            // Redirect to signUp view
            Intent intent = new Intent(Authentification.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}
