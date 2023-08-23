package com.example.myapplication.Models;

public class Reminder {
    private String id;
    private String title;
    private String time;
    private boolean isActive;
    private boolean[] repeatDays;

    public Reminder(String title, String time, boolean[] repeatDays) {
        this.title = title;
        this.time = time;
        this.repeatDays = repeatDays;
    }

    public String getId() {
        return id;
    }

    public Reminder(String id, String title, String time, boolean isActive, boolean[] repeatDays) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.isActive = isActive;
        this.repeatDays = repeatDays;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean[] getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(boolean[] repeatDays) {
        this.repeatDays = repeatDays;
    }
}
