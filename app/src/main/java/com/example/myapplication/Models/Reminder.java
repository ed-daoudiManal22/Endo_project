package com.example.myapplication.Models;

import java.util.Date;

public class Reminder {
    private String id;
    private String title;
    private Date datetime;
    private String description;
    private boolean isActive;

    // Add constructors, getters, and setters as needed

    // Example of a constructor
    public Reminder(String title, Date datetime, String description) {
        this.title = title;
        this.datetime = datetime;
        this.description = description;
    }
    public String getId() {
        return id;
    }

    public Reminder(String id, String title, Date datetime, String description, boolean isActive) {
        this.id = id;
        this.title = title;
        this.datetime = datetime;
        this.description = description;
        this.isActive = isActive;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
