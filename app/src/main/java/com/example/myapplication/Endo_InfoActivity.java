package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.NewsLibrary.Api;
import com.example.myapplication.NewsLibrary.Articles;
import com.example.myapplication.NewsLibrary.ArticlesRVAdapter;
import com.example.myapplication.NewsLibrary.NewsModal;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class  Endo_InfoActivity extends AppCompatActivity {
    private ImageView leftIcon, notificationIcon;
    private RecyclerView articlesRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private ArticlesRVAdapter articlesRVAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endo_infos);

        leftIcon = findViewById(R.id.leftIcon);
        leftIcon.setOnClickListener(v -> startActivity(new Intent(Endo_InfoActivity.this, HomeActivity.class)));
        notificationIcon = findViewById(R.id.notificationIcon);
        notificationIcon.setOnClickListener(v -> startActivity(new Intent(Endo_InfoActivity.this, ReminderActivity.class)));
        //articles
        articlesRV = findViewById(R.id.idRVArticles);
        loadingPB = findViewById(R.id.idPBLoading);
        articlesArrayList = new ArrayList<>();
        articlesRVAdapter = new ArticlesRVAdapter(articlesArrayList, this);
        articlesRV.setLayoutManager(new LinearLayoutManager(this));
        articlesRV.setAdapter(articlesRVAdapter);

        // Box 1 click listener
        CardView box1 = findViewById(R.id.EndoFAQ);
        box1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new activity or perform desired action
                startActivity(new Intent(Endo_InfoActivity.this, EndoFAQ_Activity.class));
            }
        });
        // Fetch articles related to endometriosis
        getArticles();
        articlesRVAdapter.notifyDataSetChanged();
    }
    private void getArticles() {
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();

        // Use the URL to fetch articles related to endometriosis
        //String url = "https://newsapi.org/v2/everything?q=endometriosis&sortBy=publishedAt&apiKey=bb820970d3114ca1903eac14d6826b26";
        String apiKey = "bb820970d3114ca1903eac14d6826b26";
        String query = "endometriosis women  health";  // Combine keywords
        String sortBy = "publishedAt";
        String url = "https://newsapi.org/v2/everything?q=" + query + "&apiKey=" + apiKey;
        String BASE_URL = "https://newsapi.org/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api = retrofit.create(Api.class);

        Call<NewsModal> call = api.getAllArticles(url);

        call.enqueue(new Callback<NewsModal>() {
            @Override
            public void onResponse(Call<NewsModal> call, Response<NewsModal> response) {
                NewsModal newsModal = response.body();
                loadingPB.setVisibility(View.GONE);
                if (newsModal != null && newsModal.getArticles() != null) {
                    ArrayList<Articles> articles = newsModal.getArticles();
                    for (int i = 0; i < articles.size(); i++) {
                        articlesArrayList.add(new Articles(articles.get(i).getTitle(), articles.get(i).getDescription(), articles.get(i).getUrlToImage(),
                                articles.get(i).getUrl(), articles.get(i).getContent()));
                    }
                    articlesRVAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(Endo_InfoActivity.this, "No articles found for endometriosis", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsModal> call, Throwable t) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(Endo_InfoActivity.this, "Failed to get articles", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
