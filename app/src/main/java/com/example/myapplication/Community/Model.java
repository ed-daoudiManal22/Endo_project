package com.example.myapplication.Community;

import com.example.myapplication.Models.Comment;

import java.util.ArrayList;
import java.util.List;

public class Model {
    String title, desc, author, date, img, share_count,id,timestamp;
    List<Comment> comments;

    public Model(String title, String desc, String author, String date, String img, String share_count, String id, String timestamp) {
        this.title = title;
        this.desc = desc;
        this.author = author;
        this.date = date;
        this.img = img;
        this.share_count = share_count;
        this.id = id;
        this.timestamp = timestamp;
        this.comments = new ArrayList<>(); // Initialize the comments list
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Model() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getShare_count() {
        return share_count;
    }

    public void setShare_count(String share_count) {
        this.share_count = share_count;
    }
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
