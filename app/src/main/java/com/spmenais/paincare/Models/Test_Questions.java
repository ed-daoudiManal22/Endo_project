package com.spmenais.paincare.Models;

import java.util.List;
import java.util.Map;

public class Test_Questions {
    private String text;
    private List<String> options;
    private Map<String, Object> optionScores; // Changed from Long to Object to handle mixed types
    private String type;

    public Test_Questions() {
    }

    public Test_Questions(String text, List<String> options, Map<String, Object> optionScores, String type) {
        this.text = text;
        this.options = options;
        this.optionScores = optionScores;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
    public Map<String, Object> getOptionScores() {
        return optionScores;
    }

    public void setOptionScores(Map<String, Object> optionScores) {
        this.optionScores = optionScores;
    }
}
