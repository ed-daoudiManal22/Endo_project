package com.example.myapplication.NewsLibrary;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface Api {
    @GET
    Call<NewsModal> getAllArticles(@Url String url);

    @GET
    Call<NewsModal> getArticlesByCategory(@Url String url);
}
