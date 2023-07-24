package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private List<String> optionsList;
    private String title;
    private boolean isExpandable;
    private List<String> nestedList;

    public DataModel(List<String> optionsList, String title) {
        this.optionsList = optionsList;
        this.title = title;
        this.isExpandable = false; // Set the initial value to false
        this.nestedList = new ArrayList<>(); // Initialize the nested list
    }

    public List<String> getOptionsList() {
        return optionsList;
    }

    public String getTitle() {
        return title;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public List<String> getNestedList() {
        return nestedList;
    }
}