package com.spmenais.paincare.Models;

import com.spmenais.paincare.Adapters.NestedAdapter;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private final List<String> optionsList;
    private final String title;
    private boolean isExpandable;
    private final List<String> nestedList;
    private List<Integer> selectedPositions;
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
