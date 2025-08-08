package com.spmenais.paincare.AI.explainability;

import android.util.Log;
import com.spmenais.paincare.AI.models.XAIExplanation;
import com.spmenais.paincare.AI.models.SymptomCluster;
import com.spmenais.paincare.AI.models.PainPrediction;

import java.util.*;

/**
 * Explainable AI (XAI) module for providing transparent AI decision explanations
 */
public class XAIModule {
    private static final String TAG = "XAIModule";

    /**
     * Generate comprehensive explanation for AI decision
     */
    public XAIExplanation generateExplanation(Map<String, Double> features, 
                                            SymptomCluster cluster, 
                                            PainPrediction prediction, 
                                            List<String> recommendations) {
        Log.d(TAG, "Generating XAI explanation");
        
        try {
            // Determine primary reason for the decision
            String primaryReason = determinePrimaryReason(features, cluster, prediction);
            
            // Calculate feature importance
            Map<String, Double> featureImportance = calculateFeatureImportance(features);
            
            // Generate evidence points
            List<String> evidencePoints = generateEvidencePoints(features, cluster, prediction);
            
            // Create explanation object
            XAIExplanation explanation = new XAIExplanation(primaryReason, featureImportance, evidencePoints);
            
            // Add detailed reasons for each aspect
            addDetailedReasons(explanation, features, cluster, prediction, recommendations);
            
            // Set methodology
            explanation.setMethodology("multi_factor_analysis");
            
            Log.i(TAG, "XAI explanation generated successfully");
            return explanation;
            
        } catch (Exception e) {
            Log.e(TAG, "Error generating XAI explanation", e);
            return createFallbackExplanation();
        }
    }

    /**
     * Determine the primary reason for the AI decision
     */
    private String determinePrimaryReason(Map<String, Double> features, 
                                        SymptomCluster cluster, 
                                        PainPrediction prediction) {
        
        // Find the most influential factor
        String topFeature = features.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("unknown");
        
        double topFeatureValue = features.getOrDefault(topFeature, 0.0);
        
        if (topFeatureValue > 0.7) {
            switch (topFeature) {
                case "pain_score":
                    return "High current pain level is the primary driver of this assessment";
                case "symptom_severity":
                    return "Severity and combination of reported symptoms indicate elevated risk";
                case "temporal_pattern":
                    return "Irregular pain patterns over time suggest heightened concern";
                case "trigger_factors":
                    return "Multiple identified triggers are contributing to increased risk";
                case "historical_trend":
                    return "Historical pain trend analysis indicates evolving condition";
                default:
                    return "Multiple combined factors contribute to this assessment";
            }
        } else if (cluster.getSeverityScore() > 0.6) {
            return "Symptom clustering analysis reveals " + cluster.getPrimarySymptomType() + 
                   " pattern with " + cluster.getSeverityLevel().toLowerCase() + " severity";
        } else if (prediction.getConfidence() > 0.7) {
            return "Pain trend prediction shows " + prediction.getTrend() + 
                   " pattern with high confidence";
        } else {
            return "Comprehensive analysis of symptoms, patterns, and risk factors";
        }
    }

    /**
     * Calculate relative importance of each feature
     */
    private Map<String, Double> calculateFeatureImportance(Map<String, Double> features) {
        Map<String, Double> importance = new HashMap<>();
        
        double totalValue = features.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
        
        if (totalValue == 0) {
            // Equal importance if no clear dominant features
            double equalWeight = 1.0 / features.size();
            for (String feature : features.keySet()) {
                importance.put(feature, equalWeight);
            }
        } else {
            // Normalize to show relative importance
            for (Map.Entry<String, Double> entry : features.entrySet()) {
                importance.put(entry.getKey(), entry.getValue() / totalValue);
            }
        }
        
        return importance;
    }

    /**
     * Generate evidence points supporting the decision
     */
    private List<String> generateEvidencePoints(Map<String, Double> features, 
                                               SymptomCluster cluster, 
                                               PainPrediction prediction) {
        List<String> evidencePoints = new ArrayList<>();
        
        // Pain score evidence
        double painScore = features.getOrDefault("pain_score", 0.0);
        if (painScore > 0.7) {
            evidencePoints.add("Current pain level (" + String.format("%.1f", painScore * 10) + 
                             "/10) indicates high discomfort");
        } else if (painScore > 0.4) {
            evidencePoints.add("Moderate pain level reported (" + String.format("%.1f", painScore * 10) + "/10)");
        }
        
        // Symptom severity evidence
        double symptomSeverity = features.getOrDefault("symptom_severity", 0.0);
        if (symptomSeverity > 0.6) {
            evidencePoints.add("Multiple high-severity symptoms detected");
        } else if (symptomSeverity > 0.3) {
            evidencePoints.add("Moderate symptom burden identified");
        }
        
        // Cluster evidence
        if (cluster.getConfidence() > 0.6) {
            evidencePoints.add("Clear symptom pattern identified: " + cluster.getPrimarySymptomType());
            evidencePoints.add("Symptom cluster confidence: " + 
                             String.format("%.0f%%", cluster.getConfidence() * 100));
        }
        
        // Prediction evidence
        if (prediction.getConfidence() > 0.6) {
            evidencePoints.add("Pain trend analysis shows " + prediction.getTrend() + " pattern");
            evidencePoints.add("Prediction confidence: " + 
                             String.format("%.0f%%", prediction.getConfidence() * 100));
        }
        
        // Temporal pattern evidence
        double temporalPattern = features.getOrDefault("temporal_pattern", 0.0);
        if (temporalPattern > 0.5) {
            evidencePoints.add("Irregular pain patterns detected over time");
        }
        
        // Trigger factor evidence
        double triggerFactors = features.getOrDefault("trigger_factors", 0.0);
        if (triggerFactors > 0.4) {
            evidencePoints.add("Multiple pain triggers identified");
        }
        
        // Historical trend evidence
        double historicalTrend = features.getOrDefault("historical_trend", 0.0);
        if (historicalTrend > 0.6) {
            evidencePoints.add("Historical data shows increasing pain trend");
        } else if (historicalTrend < -0.6) {
            evidencePoints.add("Historical data shows improving pain trend");
        }
        
        return evidencePoints;
    }

    /**
     * Add detailed reasons for specific aspects of the decision
     */
    private void addDetailedReasons(XAIExplanation explanation, 
                                   Map<String, Double> features,
                                   SymptomCluster cluster, 
                                   PainPrediction prediction,
                                   List<String> recommendations) {
        
        // Risk assessment reasoning
        double overallRisk = calculateOverallRisk(features);
        if (overallRisk > 0.8) {
            explanation.addDetailedReason("risk_level", 
                "High risk due to combination of severe pain, multiple symptoms, and concerning patterns");
        } else if (overallRisk > 0.5) {
            explanation.addDetailedReason("risk_level", 
                "Moderate risk identified through symptom analysis and pattern recognition");
        } else {
            explanation.addDetailedReason("risk_level", 
                "Low to moderate risk based on current symptom profile");
        }
        
        // Clustering reasoning
        explanation.addDetailedReason("symptom_clustering", 
            "Symptoms grouped into '" + cluster.getPrimarySymptomType() + 
            "' category with " + String.format("%.0f%%", cluster.getConfidence() * 100) + " confidence");
        
        // Prediction reasoning
        explanation.addDetailedReason("pain_prediction", 
            "Trend analysis predicts " + prediction.getTrend() + " pain pattern based on " +
            "historical data analysis");
        
        // Recommendation reasoning
        explanation.addDetailedReason("recommendations", 
            "Personalized recommendations generated based on risk level, symptom patterns, " +
            "and evidence-based management strategies");
    }

    /**
     * Calculate overall risk from features
     */
    private double calculateOverallRisk(Map<String, Double> features) {
        return features.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
    }

    /**
     * Create fallback explanation when full analysis fails
     */
    private XAIExplanation createFallbackExplanation() {
        XAIExplanation fallback = new XAIExplanation();
        fallback.setPrimaryReason("AI analysis completed with limited explainability");
        fallback.addEvidencePoint("System operating in fallback mode");
        fallback.addEvidencePoint("Basic pattern recognition applied");
        fallback.setMethodology("fallback_analysis");
        return fallback;
    }

    /**
     * Generate explanation summary for user display
     */
    public String generateUserFriendlyExplanation(XAIExplanation explanation) {
        StringBuilder summary = new StringBuilder();
        
        summary.append("🤖 AI Analysis Summary\n\n");
        summary.append("Why this assessment?\n");
        summary.append(explanation.getPrimaryReason()).append("\n\n");
        
        List<String> keyFactors = explanation.getKeyFactors();
        if (!keyFactors.isEmpty()) {
            summary.append("🔍 Key Factors:\n");
            for (int i = 0; i < Math.min(3, keyFactors.size()); i++) {
                summary.append("• ").append(keyFactors.get(i)).append("\n");
            }
            summary.append("\n");
        }
        
        summary.append("📊 Analysis Confidence: ")
                .append(String.format("%.0f%%", explanation.getExplanationConfidence() * 100))
                .append("\n\n");
        
        summary.append("💡 This assessment is based on pattern recognition, ")
                .append("symptom analysis, and historical data trends.");
        
        return summary.toString();
    }

    /**
     * Generate technical explanation for healthcare providers
     */
    public String generateTechnicalExplanation(XAIExplanation explanation) {
        StringBuilder technical = new StringBuilder();
        
        technical.append("Technical AI Analysis Report\n");
        technical.append("===============================\n\n");
        
        technical.append("Methodology: ").append(explanation.getMethodology()).append("\n");
        technical.append("Analysis ID: ").append(explanation.getExplanationId()).append("\n\n");
        
        technical.append("Feature Importance Analysis:\n");
        Map<String, Double> importance = explanation.getFeatureImportance();
        importance.entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
            .forEach(entry -> technical.append(String.format("- %s: %.3f\n", 
                entry.getKey(), entry.getValue())));
        
        technical.append("\nEvidence Points:\n");
        for (String evidence : explanation.getEvidencePoints()) {
            technical.append("- ").append(evidence).append("\n");
        }
        
        technical.append("\nConfidence Metrics:\n");
        technical.append("- Explanation Confidence: ")
                .append(String.format("%.3f", explanation.getExplanationConfidence()))
                .append("\n");
        
        return technical.toString();
    }

    /**
     * Validate explanation quality
     */
    public boolean validateExplanation(XAIExplanation explanation) {
        // Check if explanation has minimum required components
        boolean hasReason = explanation.getPrimaryReason() != null && 
                           !explanation.getPrimaryReason().isEmpty();
        boolean hasEvidence = !explanation.getEvidencePoints().isEmpty();
        boolean hasConfidence = explanation.getExplanationConfidence() > 0;
        
        return hasReason && hasEvidence && hasConfidence;
    }
}
