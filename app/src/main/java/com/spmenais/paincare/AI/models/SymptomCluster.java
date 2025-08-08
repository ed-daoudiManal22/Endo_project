package com.spmenais.paincare.AI.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a cluster of symptoms for pattern analysis
 */
public class SymptomCluster {
    private String clusterId;
    private String primarySymptomType;
    private List<String> dominantSymptoms;
    private double severityScore;
    private double confidence;
    private Map<String, Double> symptomWeights;
    private String clusterDescription;

    public SymptomCluster() {
        this.clusterId = "CLUSTER_" + System.currentTimeMillis();
        this.primarySymptomType = "unknown";
        this.dominantSymptoms = new ArrayList<>();
        this.severityScore = 0.0;
        this.confidence = 0.0;
        this.symptomWeights = new HashMap<>();
        this.clusterDescription = "No symptoms clustered";
    }

    public SymptomCluster(String primarySymptomType, List<String> dominantSymptoms, 
                         double severityScore, double confidence) {
        this.clusterId = "CLUSTER_" + System.currentTimeMillis();
        this.primarySymptomType = primarySymptomType;
        this.dominantSymptoms = new ArrayList<>(dominantSymptoms);
        this.severityScore = severityScore;
        this.confidence = confidence;
        this.symptomWeights = new HashMap<>();
        this.clusterDescription = generateDescription();
    }

    private String generateDescription() {
        if (dominantSymptoms.isEmpty()) {
            return "No significant symptom patterns detected";
        }
        
        StringBuilder description = new StringBuilder();
        description.append("Primary pattern: ").append(primarySymptomType);
        
        if (severityScore > 0.7) {
            description.append(" (High severity)");
        } else if (severityScore > 0.4) {
            description.append(" (Moderate severity)");
        } else {
            description.append(" (Mild severity)");
        }
        
        description.append(". Key symptoms: ");
        description.append(String.join(", ", dominantSymptoms.subList(0, Math.min(3, dominantSymptoms.size()))));
        
        return description.toString();
    }

    // Getters and Setters
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getPrimarySymptomType() {
        return primarySymptomType;
    }

    public void setPrimarySymptomType(String primarySymptomType) {
        this.primarySymptomType = primarySymptomType;
        this.clusterDescription = generateDescription();
    }

    public List<String> getDominantSymptoms() {
        return new ArrayList<>(dominantSymptoms);
    }

    public void setDominantSymptoms(List<String> dominantSymptoms) {
        this.dominantSymptoms = new ArrayList<>(dominantSymptoms);
        this.clusterDescription = generateDescription();
    }

    public double getSeverityScore() {
        return severityScore;
    }

    public void setSeverityScore(double severityScore) {
        this.severityScore = Math.max(0.0, Math.min(1.0, severityScore));
        this.clusterDescription = generateDescription();
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = Math.max(0.0, Math.min(1.0, confidence));
    }

    public Map<String, Double> getSymptomWeights() {
        return new HashMap<>(symptomWeights);
    }

    public void setSymptomWeights(Map<String, Double> symptomWeights) {
        this.symptomWeights = new HashMap<>(symptomWeights);
    }

    public String getClusterDescription() {
        return clusterDescription;
    }

    public void addSymptom(String symptom, double weight) {
        if (!dominantSymptoms.contains(symptom)) {
            dominantSymptoms.add(symptom);
        }
        symptomWeights.put(symptom, weight);
        this.clusterDescription = generateDescription();
    }

    public boolean hasSymptom(String symptom) {
        return dominantSymptoms.contains(symptom);
    }

    public double getSymptomWeight(String symptom) {
        return symptomWeights.getOrDefault(symptom, 0.0);
    }

    public String getSeverityLevel() {
        if (severityScore >= 0.8) return "SEVERE";
        else if (severityScore >= 0.6) return "HIGH";
        else if (severityScore >= 0.4) return "MODERATE";
        else if (severityScore >= 0.2) return "MILD";
        else return "MINIMAL";
    }

    @Override
    public String toString() {
        return "SymptomCluster{" +
                "clusterId='" + clusterId + '\'' +
                ", primarySymptomType='" + primarySymptomType + '\'' +
                ", dominantSymptoms=" + dominantSymptoms +
                ", severityScore=" + severityScore +
                ", confidence=" + confidence +
                '}';
    }
}
