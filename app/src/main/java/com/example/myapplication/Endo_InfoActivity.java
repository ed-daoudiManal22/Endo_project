package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Endo_InfoActivity extends AppCompatActivity {
    private ImageView leftIcon, rightIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endo_infos);

        leftIcon = findViewById(R.id.leftIcon);
        leftIcon.setOnClickListener(v -> startActivity(new Intent(Endo_InfoActivity.this, HomeActivity.class)));

        // Right Icon
        rightIcon = findViewById(R.id.rightIcon);
        //rightIcon.setOnClickListener(v -> showHelpDialog());

        // Box 1 click listener
        CardView box1 = findViewById(R.id.EndoFAQ);
        box1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new activity or perform desired action
                startActivity(new Intent(Endo_InfoActivity.this, EndoFAQ_Activity.class));
            }
        });

        // Box 2 click listener
        CardView box2 = findViewById(R.id.article1);
        box2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new activity or perform desired action
                startActivity(new Intent(Endo_InfoActivity.this, HomeActivity.class));
            }
        });

        // Box 3 click listener
        CardView box3 = findViewById(R.id.article2);
        box3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new activity or perform desired action
                startActivity(new Intent(Endo_InfoActivity.this, HomeActivity.class));
            }
        });
    }
}
