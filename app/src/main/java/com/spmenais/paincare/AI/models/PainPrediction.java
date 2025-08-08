package com.spmenais.paincare.AI.models;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents pain prediction results from AI analysis
 */
public class PainPrediction {
    private String predictionId;
    private double predictedPainLevel;
    private String trend; // "increasing", "decreasing", "stable"
    private double confidence;
    private Date predictionDate;
    private Date targetDate; // Date for which prediction is made
    private List<String> factorsInfluencing;
    private String methodology;
    private double accuracy; // Historical accuracy of similar predictions

    public PainPrediction() {
        this.predictionId = "PRED_" + System.currentTimeMillis();
        this.predictedPainLevel = 0.0;
        this.trend = "stable";
        this.confidence = 0.0;
        this.predictionDate = new Date();
        this.targetDate = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // 24 hours ahead
        this.factorsInfluencing = new ArrayList<>();
        this.methodology = "pattern_analysis";
        this.accuracy = 0.0;
    }

    public PainPrediction(double predictedPainLevel, String trend, double confidence, 
                         List<String> factorsInfluencing) {
        this.predictionId = "PRED_" + System.currentTimeMillis();
        this.predictedPainLevel = predictedPainLevel;
        this.trend = trend;
        this.confidence = confidence;
        this.predictionDate = new Date();
        this.targetDate = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        this.factorsInfluencing = new ArrayList<>(factorsInfluencing);
        this.methodology = "ml_pattern_analysis";
        this.accuracy = calculateExpectedAccuracy();
    }

    private double calculateExpectedAccuracy() {
        // Base accuracy on confidence and trend stability
        double baseAccuracy = 0.6; // 60% base accuracy
        
        if (confidence > 0.8) {
            baseAccuracy += 0.2; // High confidence adds 20%
        } else if (confidence > 0.6) {
            baseAccuracy += 0.1; // Medium confidence adds 10%
        }
        
        if (trend.equals("stable")) {
            baseAccuracy += 0.1; // Stable trends are easier to predict
        }
        
        return Math.min(0.95, baseAccuracy); // Cap at 95%
    }

    // Getters and Setters
    public String getPredictionId() {
        return predictionId;
    }

    public void setPredictionId(String predictionId) {
        this.predictionId = predictionId;
    }

    public double getPredictedPainLevel() {
        return predictedPainLevel;
    }

    public void setPredictedPainLevel(double predictedPainLevel) {
        this.predictedPainLevel = Math.max(0.0, Math.min(10.0, predictedPainLevel));
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = Math.max(0.0, Math.min(1.0, confidence));
        this.accuracy = calculateExpectedAccuracy();
    }

    public Date getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(Date predictionDate) {
        this.predictionDate = predictionDate;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public List<String> getFactorsInfluencing() {
        return new ArrayList<>(factorsInfluencing);
    }

    public void setFactorsInfluencing(List<String> factorsInfluencing) {
        this.factorsInfluencing = new ArrayList<>(factorsInfluencing);
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = Math.max(0.0, Math.min(1.0, accuracy));
    }

    // Utility methods
    public void addInfluencingFactor(String factor) {
        if (!factorsInfluencing.contains(factor)) {
            factorsInfluencing.add(factor);
        }
    }

    public String getPainLevelDescription() {
        if (predictedPainLevel <= 0) return "No pain";
        else if (predictedPainLevel <= 3) return "Mild pain";
        else if (predictedPainLevel <= 5) return "Moderate pain";
        else if (predictedPainLevel <= 7) return "Severe pain";
        else if (predictedPainLevel <= 9) return "Very severe pain";
        else return "Worst possible pain";
    }

    public String getTrendDescription() {
        switch (trend.toLowerCase()) {
            case "increasing":
                return "Pain levels are expected to increase";
            case "decreasing":
                return "Pain levels are expected to decrease";
            case "stable":
                return "Pain levels are expected to remain stable";
            default:
                return "Pain trend is uncertain";
        }
    }

    public String getConfidenceLevel() {
        if (confidence >= 0.9) return "VERY_HIGH";
        else if (confidence >= 0.7) return "HIGH";
        else if (confidence >= 0.5) return "MEDIUM";
        else if (confidence >= 0.3) return "LOW";
        else return "VERY_LOW";
    }

    public boolean isHighConfidence() {
        return confidence >= 0.7;
    }

    public boolean isPainIncreasing() {
        return "increasing".equals(trend);
    }

    public boolean isPainDecreasing() {
        return "decreasing".equals(trend);
    }

    public boolean isPainStable() {
        return "stable".equals(trend);
    }

    @Override
    public String toString() {
        return "PainPrediction{" +
                "predictionId='" + predictionId + '\'' +
                ", predictedPainLevel=" + predictedPainLevel +
                ", trend='" + trend + '\'' +
                ", confidence=" + confidence +
                ", accuracy=" + accuracy +
                ", factorsInfluencing=" + factorsInfluencing +
                '}';
    }
}
