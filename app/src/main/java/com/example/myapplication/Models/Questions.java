package com.example.myapplication.Models;

import android.content.Context;

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

    public Questions(int titleResId, int answerResId, Context context) {
        this.title = context.getString(titleResId);
        this.answer = context.getString(answerResId);
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
