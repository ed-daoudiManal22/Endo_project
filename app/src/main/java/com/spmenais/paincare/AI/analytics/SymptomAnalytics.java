package com.spmenais.paincare.AI.analytics;

import android.util.Log;
import com.spmenais.paincare.AI.models.SymptomCluster;

import java.util.*;

/**
 * Analytics module for symptom clustering and pattern recognition
 */
public class SymptomAnalytics {
    private static final String TAG = "SymptomAnalytics";
    
    // Symptom categories for clustering
    private static final Map<String, List<String>> SYMPTOM_CATEGORIES = new HashMap<String, List<String>>() {{
        put("pain_dominant", Arrays.asList("cramps", "severe_cramps", "back_pain", "pelvic_pain", "abdomen"));
        put("digestive_symptoms", Arrays.asList("nausea", "vomiting", "diarrhea", "bloating", "craving"));
        put("neurological_symptoms", Arrays.asList("headache", "dizzy", "fatigue", "mood_changes"));
        put("reproductive_symptoms", Arrays.asList("tender_breasts", "irregular_bleeding", "heavy_bleeding"));
        put("psychological_symptoms", Arrays.asList("anxious", "depressed", "mood_swings", "irritability"));
    }};

    // Severity weights for different symptoms
    private static final Map<String, Double> SYMPTOM_WEIGHTS = new HashMap<String, Double>() {{
        put("severe_cramps", 0.9);
        put("vomiting", 0.8);
        put("chronic_fatigue", 0.8);
        put("cramps", 0.7);
        put("nausea", 0.6);
        put("headache", 0.5);
        put("bloating", 0.4);
        put("tender_breasts", 0.3);
        put("mood_changes", 0.4);
    }};

    /**
     * Perform symptom clustering analysis
     */
    public SymptomCluster clusterSymptoms(Map<String, Object> symptomData) {
        Log.d(TAG, "Starting symptom clustering analysis");
        
        try {
            // Extract symptoms from data
            List<String> allSymptoms = extractSymptoms(symptomData);
            
            if (allSymptoms.isEmpty()) {
                return new SymptomCluster();
            }
            
            // Determine primary symptom category
            String primaryCategory = identifyPrimaryCategory(allSymptoms);
            
            // Calculate cluster metrics
            double severityScore = calculateOverallSeverity(allSymptoms);
            double confidence = calculateClusterConfidence(allSymptoms, primaryCategory);
            List<String> dominantSymptoms = identifyDominantSymptoms(allSymptoms);
            
            // Create cluster
            SymptomCluster cluster = new SymptomCluster(primaryCategory, dominantSymptoms, severityScore, confidence);
            
            // Add individual symptom weights
            Map<String, Double> symptomWeights = new HashMap<>();
            for (String symptom : allSymptoms) {
                symptomWeights.put(symptom, SYMPTOM_WEIGHTS.getOrDefault(symptom, 0.2));
            }
            cluster.setSymptomWeights(symptomWeights);
            
            Log.i(TAG, "Symptom clustering completed: " + cluster.toString());
            return cluster;
            
        } catch (Exception e) {
            Log.e(TAG, "Error during symptom clustering", e);
            return new SymptomCluster();
        }
    }

    /**
     * Extract symptoms from symptom data map
     */
    private List<String> extractSymptoms(Map<String, Object> symptomData) {
        List<String> allSymptoms = new ArrayList<>();
        
        // Extract from different symptom categories
        String[] symptomFields = {"symptoms", "pain_locations", "feelings", "pain_worse_title"};
        
        for (String field : symptomFields) {
            Object fieldData = symptomData.get(field);
            if (fieldData instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> symptoms = (List<String>) fieldData;
                allSymptoms.addAll(symptoms);
            } else if (fieldData instanceof String) {
                allSymptoms.add((String) fieldData);
            }
        }
        
        return allSymptoms;
    }

    /**
     * Identify the primary symptom category
     */
    private String identifyPrimaryCategory(List<String> symptoms) {
        Map<String, Integer> categoryScores = new HashMap<>();
        
        for (String category : SYMPTOM_CATEGORIES.keySet()) {
            categoryScores.put(category, 0);
        }
        
        // Count matches for each category
        for (String symptom : symptoms) {
            for (Map.Entry<String, List<String>> categoryEntry : SYMPTOM_CATEGORIES.entrySet()) {
                if (categoryEntry.getValue().contains(symptom)) {
                    categoryScores.put(categoryEntry.getKey(), 
                        categoryScores.get(categoryEntry.getKey()) + 1);
                }
            }
        }
        
        // Find category with highest score
        return categoryScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("unknown");
    }

    /**
     * Calculate overall severity score for the cluster
     */
    private double calculateOverallSeverity(List<String> symptoms) {
        if (symptoms.isEmpty()) return 0.0;
        
        double totalSeverity = 0.0;
        int weightedCount = 0;
        
        for (String symptom : symptoms) {
            double weight = SYMPTOM_WEIGHTS.getOrDefault(symptom, 0.2);
            totalSeverity += weight;
            weightedCount++;
        }
        
        double averageSeverity = weightedCount > 0 ? totalSeverity / weightedCount : 0.0;
        
        // Adjust for symptom count (more symptoms = higher severity)
        double countBonus = Math.min(0.3, symptoms.size() * 0.05);
        
        return Math.min(1.0, averageSeverity + countBonus);
    }

    /**
     * Calculate confidence in the clustering result
     */
    private double calculateClusterConfidence(List<String> symptoms, String primaryCategory) {
        if (symptoms.isEmpty()) return 0.0;
        
        List<String> categorySymptoms = SYMPTOM_CATEGORIES.get(primaryCategory);
        if (categorySymptoms == null) return 0.3;
        
        // Count how many symptoms match the primary category
        long matchingSymptoms = symptoms.stream()
            .filter(categorySymptoms::contains)
            .count();
        
        // Base confidence on match ratio
        double matchRatio = (double) matchingSymptoms / symptoms.size();
        
        // Higher confidence with more data points
        double dataBonus = Math.min(0.2, symptoms.size() * 0.02);
        
        return Math.min(1.0, matchRatio + dataBonus);
    }

    /**
     * Identify dominant symptoms based on weights and frequency
     */
    private List<String> identifyDominantSymptoms(List<String> symptoms) {
        Map<String, Integer> symptomCounts = new HashMap<>();
        
        // Count occurrences
        for (String symptom : symptoms) {
            symptomCounts.put(symptom, symptomCounts.getOrDefault(symptom, 0) + 1);
        }
        
        // Sort by weight and frequency
        return symptomCounts.entrySet().stream()
            .sorted((e1, e2) -> {
                double weight1 = SYMPTOM_WEIGHTS.getOrDefault(e1.getKey(), 0.2);
                double weight2 = SYMPTOM_WEIGHTS.getOrDefault(e2.getKey(), 0.2);
                double score1 = weight1 * e1.getValue();
                double score2 = weight2 * e2.getValue();
                return Double.compare(score2, score1);
            })
            .limit(5) // Top 5 symptoms
            .map(Map.Entry::getKey)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Analyze symptom co-occurrence patterns
     */
    public Map<String, Double> analyzeSymptomCorrelations(List<Map<String, Object>> historicalData) {
        Map<String, Double> correlations = new HashMap<>();
        
        if (historicalData.size() < 3) {
            Log.w(TAG, "Insufficient data for correlation analysis");
            return correlations;
        }
        
        // Extract all unique symptoms
        Set<String> allSymptoms = new HashSet<>();
        for (Map<String, Object> data : historicalData) {
            allSymptoms.addAll(extractSymptoms(data));
        }
        
        // Calculate pairwise correlations
        for (String symptom1 : allSymptoms) {
            for (String symptom2 : allSymptoms) {
                if (!symptom1.equals(symptom2)) {
                    double correlation = calculateSymptomCorrelation(symptom1, symptom2, historicalData);
                    if (correlation > 0.3) { // Only store significant correlations
                        correlations.put(symptom1 + "_" + symptom2, correlation);
                    }
                }
            }
        }
        
        Log.d(TAG, "Found " + correlations.size() + " significant symptom correlations");
        return correlations;
    }

    /**
     * Calculate correlation between two symptoms
     */
    private double calculateSymptomCorrelation(String symptom1, String symptom2, 
                                             List<Map<String, Object>> historicalData) {
        int bothPresent = 0;
        int symptom1Present = 0;
        int symptom2Present = 0;
        
        for (Map<String, Object> data : historicalData) {
            List<String> symptoms = extractSymptoms(data);
            boolean has1 = symptoms.contains(symptom1);
            boolean has2 = symptoms.contains(symptom2);
            
            if (has1) symptom1Present++;
            if (has2) symptom2Present++;
            if (has1 && has2) bothPresent++;
        }
        
        // Simple correlation calculation
        if (symptom1Present == 0 || symptom2Present == 0) return 0.0;
        
        double expectedBoth = (double) (symptom1Present * symptom2Present) / historicalData.size();
        double observedBoth = bothPresent;
        
        return Math.min(1.0, observedBoth / expectedBoth);
    }

    /**
     * Get symptom category insights
     */
    public Map<String, Object> getSymptomInsights(SymptomCluster cluster) {
        Map<String, Object> insights = new HashMap<>();
        
        insights.put("primary_category", cluster.getPrimarySymptomType());
        insights.put("severity_level", cluster.getSeverityLevel());
        insights.put("dominant_symptoms", cluster.getDominantSymptoms());
        insights.put("confidence", cluster.getConfidence());
        
        // Add category-specific insights
        String category = cluster.getPrimarySymptomType();
        switch (category) {
            case "pain_dominant":
                insights.put("category_insight", "Focus on pain management strategies");
                insights.put("recommendations", Arrays.asList(
                    "Apply heat therapy", "Consider gentle movement", "Practice relaxation techniques"
                ));
                break;
            case "digestive_symptoms":
                insights.put("category_insight", "Digestive symptoms are prominent");
                insights.put("recommendations", Arrays.asList(
                    "Monitor dietary triggers", "Stay hydrated", "Consider probiotics"
                ));
                break;
            case "neurological_symptoms":
                insights.put("category_insight", "Neurological symptoms detected");
                insights.put("recommendations", Arrays.asList(
                    "Ensure adequate rest", "Manage stress levels", "Monitor headache patterns"
                ));
                break;
            default:
                insights.put("category_insight", "Mixed symptom pattern detected");
                insights.put("recommendations", Arrays.asList(
                    "Continue comprehensive tracking", "Consult healthcare provider"
                ));
        }
        
        return insights;
    }
}
