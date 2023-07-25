package com.example.myapplication;

import com.example.myapplication.Adapters.NestedAdapter;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private List<String> optionsList;
    private String title;
    private boolean isExpandable;
    private List<String> nestedList;
    private List<Integer> selectedPositions = new ArrayList<>();
    private NestedAdapter nestedAdapter;
    private List<String> selectedOptions = new ArrayList<>();

    public DataModel(List<String> optionsList, String title) {
        this.optionsList = optionsList;
        this.title = title;
        this.isExpandable = false;
        this.nestedList = new ArrayList<>();
        this.selectedPositions = new ArrayList<>(); // Initialize the selectedPositions list
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

    public void setSelectedPositions(List<Integer> selectedPositions) {
        this.selectedPositions = selectedPositions;
    }

    public List<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    public void setNestedAdapter(NestedAdapter nestedAdapter) {
        this.nestedAdapter = nestedAdapter;
    }

    public NestedAdapter getNestedAdapter() {
        return nestedAdapter;
    }

    public void setSelectedOptions(List<String> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public List<String> getSelectedOptions() {
        return selectedOptions;
    }
}
