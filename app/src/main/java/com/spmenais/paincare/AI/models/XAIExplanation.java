package com.spmenais.paincare.AI.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Explainable AI (XAI) explanation for AI decisions
 * Provides transparency into how AI recommendations were generated
 */
public class XAIExplanation {
    private String explanationId;
    private String primaryReason;
    private Map<String, Double> featureImportance;
    private List<String> evidencePoints;
    private List<String> keyFactors;
    private String methodology;
    private double explanationConfidence;
    private Map<String, String> detailedReasons;

    public XAIExplanation() {
        this.explanationId = "XAI_" + System.currentTimeMillis();
        this.primaryReason = "Insufficient data for detailed explanation";
        this.featureImportance = new HashMap<>();
        this.evidencePoints = new ArrayList<>();
        this.keyFactors = new ArrayList<>();
        this.methodology = "rule_based";
        this.explanationConfidence = 0.0;
        this.detailedReasons = new HashMap<>();
    }

    public XAIExplanation(String primaryReason, Map<String, Double> featureImportance, 
                         List<String> evidencePoints) {
        this.explanationId = "XAI_" + System.currentTimeMillis();
        this.primaryReason = primaryReason;
        this.featureImportance = new HashMap<>(featureImportance);
        this.evidencePoints = new ArrayList<>(evidencePoints);
        this.keyFactors = new ArrayList<>();
        this.methodology = "feature_importance_analysis";
        this.explanationConfidence = calculateConfidence();
        this.detailedReasons = new HashMap<>();
        generateKeyFactors();
    }

    private double calculateConfidence() {
        if (featureImportance.isEmpty()) return 0.2;
        
        // Higher confidence when we have clear dominant features
        double maxImportance = featureImportance.values().stream()
            .mapToDouble(Double::doubleValue)
            .max().orElse(0.0);
        
        // More evidence points increase confidence
        double evidenceBonus = Math.min(0.3, evidencePoints.size() * 0.1);
        
        return Math.min(1.0, maxImportance + evidenceBonus);
    }

    private void generateKeyFactors() {
        // Extract top contributing factors
        featureImportance.entrySet().stream()
            .filter(entry -> entry.getValue() > 0.2) // Only significant factors
            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
            .limit(5)
            .forEach(entry -> keyFactors.add(humanReadableFeature(entry.getKey())));
    }

    private String humanReadableFeature(String feature) {
        Map<String, String> featureTranslations = new HashMap<String, String>() {{
            put("pain_score", "Current pain level");
            put("symptom_severity", "Symptom intensity");
            put("temporal_pattern", "Pain pattern over time");
            put("trigger_factors", "Identified triggers");
            put("historical_trend", "Historical pain trend");
        }};
        
        return featureTranslations.getOrDefault(feature, feature.replace("_", " "));
    }

    // Getters and Setters
    public String getExplanationId() {
        return explanationId;
    }

    public void setExplanationId(String explanationId) {
        this.explanationId = explanationId;
    }

    public String getPrimaryReason() {
        return primaryReason;
    }

    public void setPrimaryReason(String primaryReason) {
        this.primaryReason = primaryReason;
    }

    public Map<String, Double> getFeatureImportance() {
        return new HashMap<>(featureImportance);
    }

    public void setFeatureImportance(Map<String, Double> featureImportance) {
        this.featureImportance = new HashMap<>(featureImportance);
        this.explanationConfidence = calculateConfidence();
        generateKeyFactors();
    }

    public List<String> getEvidencePoints() {
        return new ArrayList<>(evidencePoints);
    }

    public void setEvidencePoints(List<String> evidencePoints) {
        this.evidencePoints = new ArrayList<>(evidencePoints);
        this.explanationConfidence = calculateConfidence();
    }

    public List<String> getKeyFactors() {
        return new ArrayList<>(keyFactors);
    }

    public void setKeyFactors(List<String> keyFactors) {
        this.keyFactors = new ArrayList<>(keyFactors);
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public double getExplanationConfidence() {
        return explanationConfidence;
    }

    public void setExplanationConfidence(double explanationConfidence) {
        this.explanationConfidence = Math.max(0.0, Math.min(1.0, explanationConfidence));
    }

    public Map<String, String> getDetailedReasons() {
        return new HashMap<>(detailedReasons);
    }

    public void setDetailedReasons(Map<String, String> detailedReasons) {
        this.detailedReasons = new HashMap<>(detailedReasons);
    }

    // Utility methods
    public void addEvidencePoint(String evidence) {
        if (!evidencePoints.contains(evidence)) {
            evidencePoints.add(evidence);
            this.explanationConfidence = calculateConfidence();
        }
    }

    public void addFeatureImportance(String feature, double importance) {
        featureImportance.put(feature, importance);
        this.explanationConfidence = calculateConfidence();
        generateKeyFactors();
    }

    public void addDetailedReason(String category, String reason) {
        detailedReasons.put(category, reason);
    }

    public String getTopContributingFactor() {
        return featureImportance.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> humanReadableFeature(entry.getKey()))
            .orElse("Unknown factor");
    }

    public double getTopFactorImportance() {
        return featureImportance.values().stream()
            .mapToDouble(Double::doubleValue)
            .max().orElse(0.0);
    }

    public String getFormattedExplanation() {
        StringBuilder explanation = new StringBuilder();
        
        explanation.append("Decision Explanation:\n");
        explanation.append("Primary Reason: ").append(primaryReason).append("\n\n");
        
        if (!keyFactors.isEmpty()) {
            explanation.append("Key Contributing Factors:\n");
            for (int i = 0; i < keyFactors.size(); i++) {
                explanation.append((i + 1)).append(". ").append(keyFactors.get(i)).append("\n");
            }
            explanation.append("\n");
        }
        
        if (!evidencePoints.isEmpty()) {
            explanation.append("Supporting Evidence:\n");
            for (String evidence : evidencePoints) {
                explanation.append("• ").append(evidence).append("\n");
            }
        }
        
        explanation.append("\nConfidence Level: ")
                   .append(String.format("%.1f%%", explanationConfidence * 100));
        
        return explanation.toString();
    }

    public boolean isHighConfidenceExplanation() {
        return explanationConfidence >= 0.7;
    }

    public String getConfidenceLevel() {
        if (explanationConfidence >= 0.9) return "VERY_HIGH";
        else if (explanationConfidence >= 0.7) return "HIGH";
        else if (explanationConfidence >= 0.5) return "MEDIUM";
        else if (explanationConfidence >= 0.3) return "LOW";
        else return "VERY_LOW";
    }

    @Override
    public String toString() {
        return "XAIExplanation{" +
                "explanationId='" + explanationId + '\'' +
                ", primaryReason='" + primaryReason + '\'' +
                ", keyFactors=" + keyFactors +
                ", explanationConfidence=" + explanationConfidence +
                '}';
    }
}
