package com.spmenais.paincare.Models;

import androidx.annotation.NonNull;

public class Questions {
    private final String title;
    private final String answer;
    private boolean expandable;

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public Questions(String title, String answer, boolean expandable) {
        this.title = title;
        this.answer = answer;
        this.expandable = expandable;
    }


    public String getTitle() {
        return title;
    }

    public String getAnswer() {
        return answer;
    }

    @NonNull
    @Override
    public String toString() {
        return "Questions{" +
                "title='" + title + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }

}
