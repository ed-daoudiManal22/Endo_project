package com.spmenais.paincare.AI.models;

import java.util.Date;
import java.util.List;

/**
 * Represents an AI-driven decision with recommendations and explanations
 */
public class AIDecision {
    private final double riskScore;
    private final String riskLevel;
    private final List<String> recommendations;
    private final XAIExplanation explanation;
    private final SymptomCluster cluster;
    private final PainPrediction prediction;
    private final Date timestamp;
    private final String decisionId;

    public AIDecision(double riskScore, String riskLevel, List<String> recommendations, 
                     XAIExplanation explanation, SymptomCluster cluster, PainPrediction prediction) {
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.recommendations = recommendations;
        this.explanation = explanation;
        this.cluster = cluster;
        this.prediction = prediction;
        this.timestamp = new Date();
        this.decisionId = generateDecisionId();
    }

    private String generateDecisionId() {
        return "AI_" + System.currentTimeMillis() + "_" + Math.abs(hashCode() % 10000);
    }

    // Getters
    public double getRiskScore() {
        return riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public XAIExplanation getExplanation() {
        return explanation;
    }

    public SymptomCluster getCluster() {
        return cluster;
    }

    public PainPrediction getPrediction() {
        return prediction;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDecisionId() {
        return decisionId;
    }

    public double getConfidenceScore() {
        // Calculate overall confidence based on prediction confidence and data quality
        double predictionConfidence = prediction != null ? prediction.getConfidence() : 0.5;
        double clusterConfidence = cluster != null ? cluster.getConfidence() : 0.5;
        return (predictionConfidence + clusterConfidence) / 2.0;
    }

    public String getRiskDescription() {
        switch (riskLevel) {
            case "HIGH":
                return "High risk of severe symptoms. Immediate attention recommended.";
            case "MODERATE":
                return "Moderate risk level. Monitor symptoms and consider preventive measures.";
            case "LOW":
                return "Low risk level. Continue current management strategies.";
            case "MINIMAL":
                return "Minimal risk detected. Maintain healthy lifestyle habits.";
            default:
                return "Risk level assessment unavailable.";
        }
    }

    @Override
    public String toString() {
        return "AIDecision{" +
                "decisionId='" + decisionId + '\'' +
                ", riskScore=" + riskScore +
                ", riskLevel='" + riskLevel + '\'' +
                ", recommendations=" + recommendations.size() + " items" +
                ", timestamp=" + timestamp +
                '}';
    }
}
