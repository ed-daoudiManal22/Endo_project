package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

public class MainActivity extends AppCompatActivity {

    private AppCompatButton loginWithEmailButton;
    private AppCompatButton loginWithGoogleButton;
    private AppCompatButton signUpButton;
    private AppCompatTextView contactUsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginWithEmailButton = findViewById(R.id.loginWithEmailButton);
        loginWithGoogleButton = findViewById(R.id.loginWithGoogleButton);
        signUpButton = findViewById(R.id.signUpButton);
        contactUsTextView = findViewById(R.id.contactus);

        loginWithEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle login with email button click
                // Redirect to another view or perform necessary actions
            }
        });

        loginWithGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle login with Google button click
                // Redirect to another view or perform necessary actions
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign up button click
                // Redirect to another view or perform necessary actions
            }
        });

        contactUsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle contact us text click
                // Redirect to another view or perform necessary actions
                String url = "https://www.example.com"; // Replace with your website URL
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
    }
}
