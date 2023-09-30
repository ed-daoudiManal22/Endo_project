package com.spmenais.paincare.NewsLibrary;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spmenais.paincare.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ArticlesRVAdapter extends RecyclerView.Adapter<ArticlesRVAdapter.ViewHolder> {

    private final List<Articles> articlesArrayList;
    private final Context context;

    public ArticlesRVAdapter(List<Articles> articlesArrayList, Context context) {
        this.articlesArrayList = articlesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ArticlesRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.articles_rv_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticlesRVAdapter.ViewHolder holder, int position) {
        Articles articles = articlesArrayList.get(position);
        holder.subTitleTv.setText(articles.getDescription());
        holder.titleTV.setText(articles.getTitle());
        Picasso.get().load(articles.getUrlToImage()).into(holder.articlesIV);
        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, ArticlesDetailActivity.class);
            i.putExtra("title", articles.getTitle());
            i.putExtra("content", articles.getContent());
            i.putExtra("desc", articles.getDescription());
            i.putExtra("image", articles.getUrlToImage());
            i.putExtra("url", articles.getUrl());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return articlesArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView titleTV;
        private final TextView subTitleTv;
        private final ImageView articlesIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.idTVArticlesHeading);
            subTitleTv = itemView.findViewById(R.id.idTVSubtitles);
            articlesIV = itemView.findViewById(R.id.idIVArticles);
        }
    }
}

