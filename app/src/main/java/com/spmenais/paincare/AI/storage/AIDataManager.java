package com.spmenais.paincare.AI.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.spmenais.paincare.AI.models.AIDecision;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Data manager for storing and retrieving AI decisions and insights
 */
public class AIDataManager {
    private static final String TAG = "AIDataManager";
    private static final String PREFS_NAME = "paincare_ai_data";
    private static final String KEY_AI_DECISIONS = "ai_decisions_";
    private static final String KEY_AI_INSIGHTS = "ai_insights_";
    private static final int MAX_STORED_DECISIONS = 50;
    
    private final SharedPreferences preferences;
    private final Gson gson;
    
    public AIDataManager(Context context) {
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
        Log.d(TAG, "AI Data Manager initialized");
    }
    
    /**
     * Store an AI decision for a user
     */
    public void storeAIDecision(String userId, AIDecision decision) {
        try {
            List<AIDecision> userDecisions = getStoredAIDecisions(userId);
            
            // Add new decision at the beginning
            userDecisions.add(0, decision);
            
            // Keep only the most recent decisions
            if (userDecisions.size() > MAX_STORED_DECISIONS) {
                userDecisions = userDecisions.subList(0, MAX_STORED_DECISIONS);
            }
            
            // Store updated list
            String json = gson.toJson(userDecisions);
            preferences.edit()
                .putString(KEY_AI_DECISIONS + userId, json)
                .apply();
                
            Log.d(TAG, "Stored AI decision for user: " + userId);
            
        } catch (Exception e) {
            Log.e(TAG, "Error storing AI decision", e);
        }
    }
    
    /**
     * Get stored AI decisions for a user
     */
    private List<AIDecision> getStoredAIDecisions(String userId) {
        try {
            String json = preferences.getString(KEY_AI_DECISIONS + userId, "[]");
            Type listType = new TypeToken<List<AIDecision>>(){}.getType();
            List<AIDecision> decisions = gson.fromJson(json, listType);
            return decisions != null ? new ArrayList<>(decisions) : new ArrayList<>();
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving stored AI decisions", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get recent AI decisions for a user
     */
    public List<AIDecision> getRecentAIDecisions(String userId, int dayLimit) {
        try {
            List<AIDecision> allDecisions = getStoredAIDecisions(userId);
            
            if (dayLimit <= 0) {
                return allDecisions;
            }
            
            // Filter by date
            Calendar cutoffDate = Calendar.getInstance();
            cutoffDate.add(Calendar.DAY_OF_YEAR, -dayLimit);
            
            return allDecisions.stream()
                .filter(decision -> decision.getTimestamp().after(cutoffDate.getTime()))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            Log.e(TAG, "Error getting recent AI decisions", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get a specific AI decision by ID
     */
    public AIDecision getAIDecision(String userId, String decisionId) {
        try {
            List<AIDecision> decisions = getStoredAIDecisions(userId);
            return decisions.stream()
                .filter(decision -> decision.getDecisionId().equals(decisionId))
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            Log.e(TAG, "Error getting AI decision by ID", e);
            return null;
        }
    }
    
    /**
     * Store AI insights for a user
     */
    public void storeAIInsights(String userId, Map<String, Object> insights) {
        try {
            String json = gson.toJson(insights);
            preferences.edit()
                .putString(KEY_AI_INSIGHTS + userId, json)
                .apply();
                
            Log.d(TAG, "Stored AI insights for user: " + userId);
            
        } catch (Exception e) {
            Log.e(TAG, "Error storing AI insights", e);
        }
    }
    
    /**
     * Get stored AI insights for a user
     */
    public Map<String, Object> getAIInsights(String userId) {
        try {
            String json = preferences.getString(KEY_AI_INSIGHTS + userId, "{}");
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> insights = gson.fromJson(json, mapType);
            return insights != null ? insights : new HashMap<>();
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving AI insights", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Clear all AI data for a user
     */
    public void clearUserAIData(String userId) {
        try {
            preferences.edit()
                .remove(KEY_AI_DECISIONS + userId)
                .remove(KEY_AI_INSIGHTS + userId)
                .apply();
                
            Log.d(TAG, "Cleared AI data for user: " + userId);
            
        } catch (Exception e) {
            Log.e(TAG, "Error clearing user AI data", e);
        }
    }
    
    /**
     * Get AI decision statistics for a user
     */
    public Map<String, Object> getAIStatistics(String userId) {
        try {
            List<AIDecision> decisions = getStoredAIDecisions(userId);
            Map<String, Object> stats = new HashMap<>();
            
            if (decisions.isEmpty()) {
                stats.put("total_decisions", 0);
                return stats;
            }
            
            stats.put("total_decisions", decisions.size());
            
            // Risk level distribution
            Map<String, Long> riskDistribution = decisions.stream()
                .collect(Collectors.groupingBy(
                    AIDecision::getRiskLevel,
                    Collectors.counting()
                ));
            stats.put("risk_distribution", riskDistribution);
            
            // Average risk score
            double avgRiskScore = decisions.stream()
                .mapToDouble(AIDecision::getRiskScore)
                .average()
                .orElse(0.0);
            stats.put("average_risk_score", avgRiskScore);
            
            // Average confidence
            double avgConfidence = decisions.stream()
                .mapToDouble(AIDecision::getConfidenceScore)
                .average()
                .orElse(0.0);
            stats.put("average_confidence", avgConfidence);
            
            // Most recent decision date
            stats.put("last_analysis", decisions.get(0).getTimestamp());
            
            // Oldest decision date
            stats.put("first_analysis", decisions.get(decisions.size() - 1).getTimestamp());
            
            return stats;
            
        } catch (Exception e) {
            Log.e(TAG, "Error calculating AI statistics", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Export AI data for a user
     */
    public String exportUserAIData(String userId) {
        try {
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("decisions", getStoredAIDecisions(userId));
            exportData.put("insights", getAIInsights(userId));
            exportData.put("statistics", getAIStatistics(userId));
            exportData.put("export_date", new Date());
            
            return gson.toJson(exportData);
            
        } catch (Exception e) {
            Log.e(TAG, "Error exporting AI data", e);
            return "{}";
        }
    }
    
    /**
     * Check if user has sufficient AI data
     */
    public boolean hasSufficientData(String userId) {
        try {
            List<AIDecision> decisions = getRecentAIDecisions(userId, 30);
            return decisions.size() >= 3; // Minimum for meaningful analysis
        } catch (Exception e) {
            Log.e(TAG, "Error checking data sufficiency", e);
            return false;
        }
    }
    
    /**
     * Get data usage statistics
     */
    public Map<String, Object> getDataUsageStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Count total stored decisions across all users
            int totalDecisions = 0;
            Map<String, ?> allPrefs = preferences.getAll();
            
            for (String key : allPrefs.keySet()) {
                if (key.startsWith(KEY_AI_DECISIONS)) {
                    String json = (String) allPrefs.get(key);
                    if (json != null) {
                        Type listType = new TypeToken<List<AIDecision>>(){}.getType();
                        List<AIDecision> decisions = gson.fromJson(json, listType);
                        if (decisions != null) {
                            totalDecisions += decisions.size();
                        }
                    }
                }
            }
            
            stats.put("total_stored_decisions", totalDecisions);
            stats.put("total_users_with_ai_data", countUsersWithAIData());
            stats.put("storage_usage_kb", getApproximateStorageUsage());
            
            return stats;
            
        } catch (Exception e) {
            Log.e(TAG, "Error calculating data usage stats", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Count users with AI data
     */
    private int countUsersWithAIData() {
        Set<String> userIds = new HashSet<>();
        Map<String, ?> allPrefs = preferences.getAll();
        
        for (String key : allPrefs.keySet()) {
            if (key.startsWith(KEY_AI_DECISIONS) || key.startsWith(KEY_AI_INSIGHTS)) {
                String userId = key.substring(key.lastIndexOf("_") + 1);
                userIds.add(userId);
            }
        }
        
        return userIds.size();
    }
    
    /**
     * Get approximate storage usage
     */
    private long getApproximateStorageUsage() {
        long totalSize = 0;
        Map<String, ?> allPrefs = preferences.getAll();
        
        for (Object value : allPrefs.values()) {
            if (value instanceof String) {
                totalSize += ((String) value).length();
            }
        }
        
        return totalSize / 1024; // Convert to KB
    }
}
