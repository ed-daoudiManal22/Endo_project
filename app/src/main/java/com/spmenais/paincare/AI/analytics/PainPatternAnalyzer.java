package com.spmenais.paincare.AI.analytics;

import android.util.Log;
import com.spmenais.paincare.AI.models.PainPrediction;

import java.util.*;

/**
 * Analytics module for pain pattern analysis and prediction
 */
public class PainPatternAnalyzer {
    private static final String TAG = "PainPatternAnalyzer";
    
    private static final int MIN_DATA_POINTS = 3;
    private static final int TREND_ANALYSIS_WINDOW = 7; // days
    
    /**
     * Predict pain trends based on historical data
     */
    public PainPrediction predictPainTrend(List<Map<String, Object>> historicalData) {
        Log.d(TAG, "Starting pain trend prediction");
        
        try {
            if (historicalData.size() < MIN_DATA_POINTS) {
                Log.w(TAG, "Insufficient data for prediction");
                return createLowConfidencePrediction();
            }
            
            // Extract pain scores
            List<Double> painScores = extractPainScores(historicalData);
            
            if (painScores.size() < MIN_DATA_POINTS) {
                return createLowConfidencePrediction();
            }
            
            // Analyze trend
            String trend = analyzeTrend(painScores);
            double predictedLevel = predictNextPainLevel(painScores);
            double confidence = calculatePredictionConfidence(painScores, trend);
            List<String> influencingFactors = identifyInfluencingFactors(historicalData);
            
            PainPrediction prediction = new PainPrediction(predictedLevel, trend, confidence, influencingFactors);
            prediction.setMethodology("time_series_analysis");
            
            Log.i(TAG, "Pain prediction completed: " + prediction.toString());
            return prediction;
            
        } catch (Exception e) {
            Log.e(TAG, "Error during pain prediction", e);
            return createLowConfidencePrediction();
        }
    }

    /**
     * Extract pain scores from historical data
     */
    private List<Double> extractPainScores(List<Map<String, Object>> historicalData) {
        List<Double> painScores = new ArrayList<>();
        
        for (Map<String, Object> data : historicalData) {
            Object painScoreObj = data.get("painScore");
            if (painScoreObj instanceof Number) {
                double score = ((Number) painScoreObj).doubleValue();
                if (score >= 0 && score <= 10) { // Valid pain score range
                    painScores.add(score);
                }
            }
        }
        
        return painScores;
    }

    /**
     * Analyze trend in pain scores
     */
    private String analyzeTrend(List<Double> painScores) {
        if (painScores.size() < 2) return "stable";
        
        // Simple linear regression to determine trend
        double slope = calculateTrendSlope(painScores);
        
        if (slope > 0.3) return "increasing";
        else if (slope < -0.3) return "decreasing";
        else return "stable";
    }

    /**
     * Calculate trend slope using linear regression
     */
    private double calculateTrendSlope(List<Double> painScores) {
        int n = painScores.size();
        if (n < 2) return 0.0;
        
        // Calculate means
        double xMean = (n - 1) / 2.0; // Time points are 0, 1, 2, ..., n-1
        double yMean = painScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        // Calculate slope using least squares method
        double numerator = 0.0;
        double denominator = 0.0;
        
        for (int i = 0; i < n; i++) {
            double xi = i;
            double yi = painScores.get(i);
            numerator += (xi - xMean) * (yi - yMean);
            denominator += (xi - xMean) * (xi - xMean);
        }
        
        return denominator != 0 ? numerator / denominator : 0.0;
    }

    /**
     * Predict next pain level
     */
    private double predictNextPainLevel(List<Double> painScores) {
        if (painScores.isEmpty()) return 0.0;
        
        // Weighted average with more weight on recent values
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        
        for (int i = 0; i < painScores.size(); i++) {
            double weight = Math.pow(0.8, painScores.size() - 1 - i); // Exponential decay
            weightedSum += painScores.get(i) * weight;
            totalWeight += weight;
        }
        
        double baselinePrediction = weightedSum / totalWeight;
        
        // Apply trend adjustment
        double trendSlope = calculateTrendSlope(painScores);
        double trendAdjustment = trendSlope * 0.5; // Dampen the trend effect
        
        double prediction = baselinePrediction + trendAdjustment;
        
        // Ensure prediction is within valid range
        return Math.max(0.0, Math.min(10.0, prediction));
    }

    /**
     * Calculate confidence in the prediction
     */
    private double calculatePredictionConfidence(List<Double> painScores, String trend) {
        if (painScores.size() < MIN_DATA_POINTS) return 0.2;
        
        // Base confidence on data consistency
        double variance = calculateVariance(painScores);
        double consistencyScore = Math.max(0.0, 1.0 - (variance / 25.0)); // Normalize by max possible variance
        
        // Trend stability bonus
        double trendBonus = 0.0;
        if ("stable".equals(trend)) {
            trendBonus = 0.2; // Stable trends are easier to predict
        } else {
            // Check if trend is consistent
            double trendConsistency = calculateTrendConsistency(painScores);
            trendBonus = trendConsistency * 0.3;
        }
        
        // Data volume bonus
        double volumeBonus = Math.min(0.3, (painScores.size() - MIN_DATA_POINTS) * 0.05);
        
        return Math.min(1.0, consistencyScore + trendBonus + volumeBonus);
    }

    /**
     * Calculate variance in pain scores
     */
    private double calculateVariance(List<Double> painScores) {
        if (painScores.size() < 2) return 0.0;
        
        double mean = painScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = painScores.stream()
            .mapToDouble(score -> Math.pow(score - mean, 2))
            .average().orElse(0.0);
        
        return variance;
    }

    /**
     * Calculate consistency of trend direction
     */
    private double calculateTrendConsistency(List<Double> painScores) {
        if (painScores.size() < 3) return 0.5;
        
        int consistentChanges = 0;
        int totalChanges = 0;
        
        boolean lastIncreasing = painScores.get(1) > painScores.get(0);
        
        for (int i = 2; i < painScores.size(); i++) {
            boolean currentIncreasing = painScores.get(i) > painScores.get(i - 1);
            if (currentIncreasing == lastIncreasing) {
                consistentChanges++;
            }
            totalChanges++;
            lastIncreasing = currentIncreasing;
        }
        
        return totalChanges > 0 ? (double) consistentChanges / totalChanges : 0.5;
    }

    /**
     * Identify factors that may influence pain patterns
     */
    private List<String> identifyInfluencingFactors(List<Map<String, Object>> historicalData) {
        List<String> factors = new ArrayList<>();
        Map<String, Integer> triggerCounts = new HashMap<>();
        
        // Analyze trigger patterns
        for (Map<String, Object> data : historicalData) {
            Object triggerObj = data.get("pain_worse_title");
            if (triggerObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> triggers = (List<String>) triggerObj;
                for (String trigger : triggers) {
                    triggerCounts.put(trigger, triggerCounts.getOrDefault(trigger, 0) + 1);
                }
            }
        }
        
        // Identify frequent triggers
        int threshold = Math.max(1, historicalData.size() / 3); // Must appear in at least 1/3 of records
        for (Map.Entry<String, Integer> entry : triggerCounts.entrySet()) {
            if (entry.getValue() >= threshold) {
                factors.add(entry.getKey());
            }
        }
        
        // Add temporal factors
        if (hasWeekendPattern(historicalData)) {
            factors.add("weekend_pattern");
        }
        
        if (hasPeriodicPattern(historicalData)) {
            factors.add("cyclical_pattern");
        }
        
        return factors;
    }

    /**
     * Check for weekend patterns in pain data
     */
    private boolean hasWeekendPattern(List<Map<String, Object>> historicalData) {
        // This would require date information to implement properly
        // For now, return false as placeholder
        return false;
    }

    /**
     * Check for periodic patterns in pain data
     */
    private boolean hasPeriodicPattern(List<Map<String, Object>> historicalData) {
        if (historicalData.size() < 7) return false;
        
        List<Double> painScores = extractPainScores(historicalData);
        if (painScores.size() < 7) return false;
        
        // Simple check for cyclical patterns (peaks and valleys)
        int peaks = 0;
        int valleys = 0;
        
        for (int i = 1; i < painScores.size() - 1; i++) {
            double prev = painScores.get(i - 1);
            double curr = painScores.get(i);
            double next = painScores.get(i + 1);
            
            if (curr > prev && curr > next) peaks++;
            if (curr < prev && curr < next) valleys++;
        }
        
        // Consider cyclical if we have regular peaks and valleys
        int totalFeatures = peaks + valleys;
        double featureRatio = (double) totalFeatures / painScores.size();
        
        return featureRatio > 0.2; // At least 20% of points are peaks/valleys
    }

    /**
     * Create a low-confidence prediction for insufficient data
     */
    private PainPrediction createLowConfidencePrediction() {
        PainPrediction prediction = new PainPrediction();
        prediction.setPredictedPainLevel(0.0);
        prediction.setTrend("stable");
        prediction.setConfidence(0.2);
        prediction.setFactorsInfluencing(Arrays.asList("insufficient_data"));
        prediction.setMethodology("fallback_analysis");
        return prediction;
    }

    /**
     * Analyze pain volatility
     */
    public double analyzePainVolatility(List<Map<String, Object>> historicalData) {
        List<Double> painScores = extractPainScores(historicalData);
        if (painScores.size() < 2) return 0.0;
        
        // Calculate standard deviation as measure of volatility
        double mean = painScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = painScores.stream()
            .mapToDouble(score -> Math.pow(score - mean, 2))
            .average().orElse(0.0);
        
        double standardDeviation = Math.sqrt(variance);
        
        // Normalize to 0-1 scale (assuming max std dev of 5 for pain scores 0-10)
        return Math.min(1.0, standardDeviation / 5.0);
    }

    /**
     * Get pain pattern insights
     */
    public Map<String, Object> getPainPatternInsights(PainPrediction prediction, 
                                                     List<Map<String, Object>> historicalData) {
        Map<String, Object> insights = new HashMap<>();
        
        insights.put("predicted_level", prediction.getPredictedPainLevel());
        insights.put("trend", prediction.getTrend());
        insights.put("confidence", prediction.getConfidence());
        insights.put("volatility", analyzePainVolatility(historicalData));
        
        // Add trend-specific insights
        switch (prediction.getTrend()) {
            case "increasing":
                insights.put("trend_insight", "Pain levels are trending upward");
                insights.put("recommendation", "Consider preventive measures and consult healthcare provider");
                break;
            case "decreasing":
                insights.put("trend_insight", "Pain levels are improving");
                insights.put("recommendation", "Continue current management strategies");
                break;
            case "stable":
                insights.put("trend_insight", "Pain levels are stable");
                insights.put("recommendation", "Maintain current routine and monitor for changes");
                break;
        }
        
        return insights;
    }
}
