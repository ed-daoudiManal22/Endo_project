package com.spmenais.paincare.NewsLibrary;

import java.util.ArrayList;
import java.util.List;

public class NewsModal {

    private int totalResults;
    private String status;
    private List<Articles> articles;

    public NewsModal(int totalResults, String status, List<Articles> articles) {
        this.totalResults = totalResults;
        this.status = status;
        this.articles = articles;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Articles> getArticles() {
        return articles;
    }

    public void setArticles(List<Articles> articles) {
        this.articles = articles;
    }
}

