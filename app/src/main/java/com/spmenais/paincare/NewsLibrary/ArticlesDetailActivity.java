package com.spmenais.paincare.NewsLibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spmenais.paincare.R;
import com.squareup.picasso.Picasso;

public class ArticlesDetailActivity extends AppCompatActivity {

    String title;
    String desc;
    String content;
    String imageURL;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_detail);
        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        content = getIntent().getStringExtra("content");
        imageURL = getIntent().getStringExtra("image");
        url = getIntent().getStringExtra("url");
        TextView titleTV = findViewById(R.id.idTVTitle);
        TextView subDescTV = findViewById(R.id.idTVSubDesc);
        TextView contentTV = findViewById(R.id.idTVContent);
        ImageView articlesIV = findViewById(R.id.idIVArticles);
        Button readArticlesBtn = findViewById(R.id.idBtnRead);
        titleTV.setText(title);
        subDescTV.setText(desc);
        contentTV.setText(content);
        Picasso.get().load(imageURL).into(articlesIV);
        readArticlesBtn.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }
}