package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

public class Authentification extends AppCompatActivity {
    private AppCompatButton loginWithEmailButton;
    private AppCompatButton loginWithGoogleButton;
    private AppCompatButton signUpButton;
    private AppCompatTextView contactUsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentification);

        loginWithEmailButton = findViewById(R.id.loginWithEmailButton);
        loginWithGoogleButton = findViewById(R.id.loginWithGoogleButton);
        signUpButton = findViewById(R.id.signUpButton);
        //contactUsTextView = findViewById(R.id.contactus);

        loginWithEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to login_with_email view
                Intent intent = new Intent(Authentification.this, logInActivity.class);
                startActivity(intent);
            }
        });

        loginWithGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to login_with_google view
                Intent intent = new Intent(Authentification.this, GoogleLoginActivity.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to signUp view
                Intent intent = new Intent(Authentification.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        /*contactUsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ContactUs view
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });*/
    }
}
