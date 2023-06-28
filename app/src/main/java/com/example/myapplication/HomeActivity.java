package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout sideMenu;
    private TextView currentUserText;
    private TextView menuCycleOvulation;
    private TextView menuEndoInfos;
    private TextView menuSettings;
    private TextView menuReminders;
    private TextView menuHelp;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // Initialize views
        sideMenu = findViewById(R.id.sideMenu);
        currentUserText = findViewById(R.id.currentUserText);
        menuCycleOvulation = findViewById(R.id.menuCycleOvulation);
        menuEndoInfos = findViewById(R.id.menuEndoInfos);
        menuSettings = findViewById(R.id.menuSettings);
        menuReminders = findViewById(R.id.menuReminders);
        menuHelp = findViewById(R.id.menuHelp);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserText.setText(getCurrentUserName());

        // Set click listeners for menu items
        menuCycleOvulation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        menuEndoInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, Endo_InfoActivity.class);
                startActivity(intent);
            }
        });

        menuSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        menuReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        menuHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Toggle side menu visibility on button click
        Button openMenuButton = findViewById(R.id.openMenuButton);
        openMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sideMenu.getVisibility() == View.VISIBLE) {
                    sideMenu.setVisibility(View.INVISIBLE);
                } else {
                    sideMenu.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private String getCurrentUserName() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName != null) {
                return displayName;
            }
        }
        return "";
    }
}

