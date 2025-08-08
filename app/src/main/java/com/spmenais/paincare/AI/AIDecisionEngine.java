package com.spmenais.paincare.AI;

import android.util.Log;
import com.spmenais.paincare.AI.models.SymptomCluster;
import com.spmenais.paincare.AI.models.PainPrediction;
import com.spmenais.paincare.AI.models.AIDecision;
import com.spmenais.paincare.AI.models.XAIExplanation;
import com.spmenais.paincare.AI.analytics.SymptomAnalytics;
import com.spmenais.paincare.AI.analytics.PainPatternAnalyzer;
import com.spmenais.paincare.AI.explainability.XAIModule;

import java.util.*;

/**
 * Core AI Decision Engine for PainCare
 * Integrates symptom clustering, pain prediction, and explainable AI features
 */
public class AIDecisionEngine {
    private static final String TAG = "AIDecisionEngine";
    
    private final SymptomAnalytics symptomAnalytics;
    private final PainPatternAnalyzer painPatternAnalyzer;
    private final XAIModule xaiModule;
    private static AIDecisionEngine instance;
    
    // AI Model Parameters
    private static final double PAIN_THRESHOLD_HIGH = 7.0;
    private static final double PAIN_THRESHOLD_MODERATE = 4.0;
    private static final int MIN_DATA_POINTS_FOR_PREDICTION = 5;
    
    // Feature weights for decision making
    private static final Map<String, Double> FEATURE_WEIGHTS = new HashMap<String, Double>() {{
        put("pain_score", 0.35);
        put("symptom_severity", 0.25);
        put("temporal_pattern", 0.20);
        put("trigger_factors", 0.15);
        put("historical_trend", 0.05);
    }};

    private AIDecisionEngine() {
        this.symptomAnalytics = new SymptomAnalytics();
        this.painPatternAnalyzer = new PainPatternAnalyzer();
        this.xaiModule = new XAIModule();
        Log.i(TAG, "AI Decision Engine initialized");
    }

    public static synchronized AIDecisionEngine getInstance() {
        if (instance == null) {
            instance = new AIDecisionEngine();
        }
        return instance;
    }

    /**
     * Main decision-making method that analyzes symptoms and provides AI-driven recommendations
     */
    public AIDecision analyzeSymptoms(Map<String, Object> symptomData, List<Map<String, Object>> historicalData) {
        Log.d(TAG, "Starting AI analysis for symptom data");
        
        try {
            // 1. Extract and normalize features
            Map<String, Double> features = extractFeatures(symptomData, historicalData);
            
            // 2. Perform symptom clustering
            SymptomCluster cluster = symptomAnalytics.clusterSymptoms(symptomData);
            
            // 3. Analyze pain patterns
            PainPrediction prediction = painPatternAnalyzer.predictPainTrend(historicalData);
            
            // 4. Calculate risk score
            double riskScore = calculateRiskScore(features, cluster, prediction);
            
            // 5. Generate recommendations
            List<String> recommendations = generateRecommendations(riskScore, cluster, prediction);
            
            // 6. Create XAI explanation
            XAIExplanation explanation = xaiModule.generateExplanation(features, cluster, prediction, recommendations);
            
            // 7. Build AI decision object
            AIDecision decision = new AIDecision(
                riskScore,
                determineRiskLevel(riskScore),
                recommendations,
                explanation,
                cluster,
                prediction
            );
            
            Log.i(TAG, "AI analysis completed successfully. Risk Score: " + riskScore);
            return decision;
            
        } catch (Exception e) {
            Log.e(TAG, "Error during AI analysis", e);
            return createFallbackDecision();
        }
    }

    /**
     * Extract and normalize features from symptom data
     */
    private Map<String, Double> extractFeatures(Map<String, Object> symptomData, List<Map<String, Object>> historicalData) {
        Map<String, Double> features = new HashMap<>();
        
        // Pain score feature
        Object painScoreObj = symptomData.get("painScore");
        double painScore = (painScoreObj instanceof Number) ? ((Number) painScoreObj).doubleValue() : 0.0;
        features.put("pain_score", normalizeScore(painScore, 0, 10));
        
        // Symptom severity (based on number and type of symptoms)
        double symptomSeverity = calculateSymptomSeverity(symptomData);
        features.put("symptom_severity", symptomSeverity);
        
        // Temporal pattern analysis
        double temporalPattern = analyzeTemporalPatterns(historicalData);
        features.put("temporal_pattern", temporalPattern);
        
        // Trigger factors
        double triggerFactors = analyzeTriggerFactors(symptomData);
        features.put("trigger_factors", triggerFactors);
        
        // Historical trend
        double historicalTrend = calculateHistoricalTrend(historicalData);
        features.put("historical_trend", historicalTrend);
        
        Log.d(TAG, "Extracted features: " + features.toString());
        return features;
    }

    /**
     * Calculate overall risk score using weighted features
     */
    private double calculateRiskScore(Map<String, Double> features, SymptomCluster cluster, PainPrediction prediction) {
        double riskScore = 0.0;
        
        // Weighted sum of normalized features
        for (Map.Entry<String, Double> feature : features.entrySet()) {
            Double weight = FEATURE_WEIGHTS.get(feature.getKey());
            if (weight != null) {
                riskScore += feature.getValue() * weight;
            }
        }
        
        // Adjust based on cluster severity
        riskScore += cluster.getSeverityScore() * 0.1;
        
        // Adjust based on prediction confidence
        if (prediction.getConfidence() > 0.7) {
            riskScore += prediction.getPredictedPainLevel() * 0.05;
        }
        
        // Ensure score is within bounds [0, 1]
        return Math.max(0.0, Math.min(1.0, riskScore));
    }

    /**
     * Calculate symptom severity based on reported symptoms
     */
    private double calculateSymptomSeverity(Map<String, Object> symptomData) {
        double severity = 0.0;
        int symptomCount = 0;
        
        // High severity symptoms
        List<String> highSeveritySymptoms = Arrays.asList(
            "severe_cramps", "chronic_fatigue", "nausea", "vomiting", "diarrhea"
        );
        
        // Medium severity symptoms
        List<String> mediumSeveritySymptoms = Arrays.asList(
            "headache", "bloating", "tender_breasts", "mood_changes"
        );
        
        for (Map.Entry<String, Object> entry : symptomData.entrySet()) {
            if (entry.getValue() instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> symptoms = (List<String>) entry.getValue();
                for (String symptom : symptoms) {
                    symptomCount++;
                    if (highSeveritySymptoms.contains(symptom)) {
                        severity += 0.8;
                    } else if (mediumSeveritySymptoms.contains(symptom)) {
                        severity += 0.5;
                    } else {
                        severity += 0.3;
                    }
                }
            }
        }
        
        return symptomCount > 0 ? Math.min(1.0, severity / symptomCount) : 0.0;
    }

    /**
     * Analyze temporal patterns in historical data
     */
    private double analyzeTemporalPatterns(List<Map<String, Object>> historicalData) {
        if (historicalData.size() < 3) return 0.0;
        
        double patternScore = 0.0;
        List<Double> painScores = new ArrayList<>();
        
        // Extract pain scores from historical data
        for (Map<String, Object> data : historicalData) {
            Object painScoreObj = data.get("painScore");
            if (painScoreObj instanceof Number) {
                painScores.add(((Number) painScoreObj).doubleValue());
            }
        }
        
        if (painScores.size() >= 3) {
            // Calculate variance to detect pattern irregularity
            double mean = painScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double variance = painScores.stream()
                .mapToDouble(score -> Math.pow(score - mean, 2))
                .average().orElse(0.0);
            
            // Higher variance indicates less predictable patterns
            patternScore = Math.min(1.0, variance / 10.0);
        }
        
        return patternScore;
    }

    /**
     * Analyze trigger factors from symptom data
     */
    private double analyzeTriggerFactors(Map<String, Object> symptomData) {
        double triggerScore = 0.0;
        
        // High-risk triggers
        List<String> highRiskTriggers = Arrays.asList(
            "stress", "lack_of_sleep", "sitting", "standing"
        );
        
        Object painWorseObj = symptomData.get("pain_worse_title");
        if (painWorseObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> triggers = (List<String>) painWorseObj;
            
            for (String trigger : triggers) {
                if (highRiskTriggers.contains(trigger)) {
                    triggerScore += 0.3;
                } else {
                    triggerScore += 0.1;
                }
            }
        }
        
        return Math.min(1.0, triggerScore);
    }

    /**
     * Calculate historical trend from pain data
     */
    private double calculateHistoricalTrend(List<Map<String, Object>> historicalData) {
        if (historicalData.size() < MIN_DATA_POINTS_FOR_PREDICTION) return 0.0;
        
        List<Double> painScores = new ArrayList<>();
        for (Map<String, Object> data : historicalData) {
            Object painScoreObj = data.get("painScore");
            if (painScoreObj instanceof Number) {
                painScores.add(((Number) painScoreObj).doubleValue());
            }
        }
        
        if (painScores.size() < 2) return 0.0;
        
        // Simple trend calculation (positive = increasing pain)
        double firstHalf = painScores.subList(0, painScores.size() / 2)
            .stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double secondHalf = painScores.subList(painScores.size() / 2, painScores.size())
            .stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        return normalizeScore(secondHalf - firstHalf, -10, 10);
    }

    /**
     * Generate personalized recommendations based on AI analysis
     */
    private List<String> generateRecommendations(double riskScore, SymptomCluster cluster, PainPrediction prediction) {
        List<String> recommendations = new ArrayList<>();
        
        if (riskScore > 0.8) {
            recommendations.add("🚨 High pain risk detected. Consider consulting your healthcare provider.");
            recommendations.add("💊 Review your current pain management strategy with your doctor.");
            recommendations.add("📊 Monitor symptoms closely and maintain detailed records.");
        } else if (riskScore > 0.5) {
            recommendations.add("⚠️ Moderate risk level. Implement preventive measures.");
            recommendations.add("🧘 Consider stress reduction techniques like meditation or yoga.");
            recommendations.add("💤 Ensure adequate sleep (7-9 hours) to help manage symptoms.");
        } else {
            recommendations.add("✅ Current symptom levels appear manageable.");
            recommendations.add("🎯 Continue current self-care routines that are working well.");
            recommendations.add("📈 Track patterns to identify potential triggers early.");
        }
        
        // Add cluster-specific recommendations
        if (cluster.getPrimarySymptomType().equals("pain_dominant")) {
            recommendations.add("🌡️ Apply heat therapy to affected areas for pain relief.");
            recommendations.add("🚶 Gentle movement and stretching may help reduce stiffness.");
        } else if (cluster.getPrimarySymptomType().equals("digestive_symptoms")) {
            recommendations.add("🥗 Consider dietary modifications to reduce digestive symptoms.");
            recommendations.add("💧 Stay well-hydrated and consider probiotics.");
        }
        
        // Add prediction-based recommendations
        if (prediction.getTrend().equals("increasing") && prediction.getConfidence() > 0.7) {
            recommendations.add("📈 Pain levels may increase. Prepare preventive strategies.");
            recommendations.add("👩‍⚕️ Schedule a check-in with your healthcare team.");
        }
        
        return recommendations;
    }

    /**
     * Determine risk level based on score
     */
    private String determineRiskLevel(double riskScore) {
        if (riskScore >= 0.8) return "HIGH";
        else if (riskScore >= 0.5) return "MODERATE";
        else if (riskScore >= 0.3) return "LOW";
        else return "MINIMAL";
    }

    /**
     * Normalize a score to [0, 1] range
     */
    private double normalizeScore(double value, double min, double max) {
        if (max <= min) return 0.0;
        return Math.max(0.0, Math.min(1.0, (value - min) / (max - min)));
    }

    /**
     * Create a fallback decision when AI analysis fails
     */
    private AIDecision createFallbackDecision() {
        List<String> fallbackRecommendations = Arrays.asList(
            "Unable to perform full AI analysis.",
            "Continue monitoring symptoms and consult healthcare provider if needed.",
            "Maintain regular symptom tracking for better insights."
        );
        
        XAIExplanation fallbackExplanation = new XAIExplanation(
            "AI analysis temporarily unavailable",
            new HashMap<>(),
            Arrays.asList("System fallback mode activated", "Basic recommendations provided")
        );
        
        return new AIDecision(
            0.5, // neutral risk score
            "UNKNOWN",
            fallbackRecommendations,
            fallbackExplanation,
            new SymptomCluster(), // empty cluster
            new PainPrediction() // empty prediction
        );
    }

    /**
     * Get AI model information for transparency
     */
    public Map<String, Object> getModelInfo() {
        Map<String, Object> modelInfo = new HashMap<>();
        modelInfo.put("version", "1.0.0");
        modelInfo.put("features", FEATURE_WEIGHTS.keySet());
        modelInfo.put("feature_weights", FEATURE_WEIGHTS);
        modelInfo.put("min_data_points", MIN_DATA_POINTS_FOR_PREDICTION);
        modelInfo.put("last_updated", new Date().toString());
        return modelInfo;
    }
}
