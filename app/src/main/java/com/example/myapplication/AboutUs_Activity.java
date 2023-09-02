package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AboutUs_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ImageView backButton = findViewById(R.id.backButton);
        TextView link1TextView = findViewById(R.id.link1);
        TextView link2TextView = findViewById(R.id.link2);
        TextView link3TextView = findViewById(R.id.link3);

        backButton.setOnClickListener(v -> onBackPressed());

        link1TextView.setOnClickListener(v -> openUrl(getString(R.string.link1_url)));

        link2TextView.setOnClickListener(v -> openUrl(getString(R.string.link2_url)));

        link3TextView.setOnClickListener(v -> openUrl(getString(R.string.link3_url)));
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
