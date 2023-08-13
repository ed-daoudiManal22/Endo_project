package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
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

        String link1 = "<a href=\"" + getString(R.string.link1_url) + "\">" + getString(R.string.link1_url) + "</a>";
        String link2 = "<a href=\"" + getString(R.string.link2_url) + "\">" + getString(R.string.link2_url) + "</a>";
        String link3 = "<a href=\"" + getString(R.string.link3_url) + "\">" + getString(R.string.link3_url) + "</a>";

        link1TextView.setText(Html.fromHtml(link1, Html.FROM_HTML_MODE_COMPACT));
        link2TextView.setText(Html.fromHtml(link2, Html.FROM_HTML_MODE_COMPACT));
        link3TextView.setText(Html.fromHtml(link3, Html.FROM_HTML_MODE_COMPACT));

        link1TextView.setMovementMethod(LinkMovementMethod.getInstance());
        link2TextView.setMovementMethod(LinkMovementMethod.getInstance());
        link3TextView.setMovementMethod(LinkMovementMethod.getInstance());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutUs_Activity.this, User_profile.class);
                startActivity(intent);
            }
        });
    }
}
