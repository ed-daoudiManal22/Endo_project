package com.example.myapplication.Models;

public class Questions {
    private String title;
    private String answer;
    private boolean expandable;

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public Questions(String title, String answer) {
        this.title = title;
        this.answer = answer;
        this.expandable = false;
    }

    public String getTitle() {
        return title;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        return "Questions{" +
                "title='" + title + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }

}
