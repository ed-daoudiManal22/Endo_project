package com.spmenais.paincare.AI.integration;

import android.content.Context;
import android.util.Log;
import com.spmenais.paincare.AI.AIDecisionEngine;
import com.spmenais.paincare.AI.models.AIDecision;
import com.spmenais.paincare.AI.storage.AIDataManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Integration service to connect AI Decision Engine with PainCare app
 */
public class AIIntegrationService {
    private static final String TAG = "AIIntegrationService";
    
    private final AIDecisionEngine aiEngine;
    private final AIDataManager dataManager;
    private final FirebaseFirestore firestore;
    private final FirebaseAuth firebaseAuth;
    private static AIIntegrationService instance;

    private AIIntegrationService(Context context) {
        this.aiEngine = AIDecisionEngine.getInstance();
        this.dataManager = new AIDataManager(context);
        this.firestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        Log.i(TAG, "AI Integration Service initialized");
    }

    public static synchronized AIIntegrationService getInstance(Context context) {
        if (instance == null) {
            instance = new AIIntegrationService(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Analyze current symptoms with AI and provide recommendations
     */
    public CompletableFuture<AIDecision> analyzeCurrentSymptoms(Map<String, Object> symptomData) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.d(TAG, "Starting AI analysis for current symptoms");
                
                // Get current user
                String userId = getCurrentUserId();
                if (userId == null) {
                    throw new RuntimeException("User not authenticated");
                }
                
                // Fetch historical data with fallback
                List<Map<String, Object>> historicalData;
                try {
                    historicalData = fetchHistoricalData(userId).join();
                    Log.d(TAG, "Successfully fetched " + historicalData.size() + " historical records");
                } catch (Exception e) {
                    Log.w(TAG, "Failed to fetch historical data, proceeding with empty history", e);
                    historicalData = new ArrayList<>(); // Use empty list as fallback
                }
                
                // Perform AI analysis
                AIDecision decision = aiEngine.analyzeSymptoms(symptomData, historicalData);
                
                // Store the decision for future reference
                dataManager.storeAIDecision(userId, decision);
                
                // Update user's AI insights with symptom data and wait for completion
                updateUserAIInsights(userId, decision, symptomData).join();
                
                Log.i(TAG, "AI analysis completed successfully");
                return decision;
                
            } catch (Exception e) {
                Log.e(TAG, "Error during AI analysis", e);
                throw new RuntimeException("AI analysis failed", e);
            }
        });
    }

    /**
     * Get AI insights for the current user
     */
    public CompletableFuture<Map<String, Object>> getAIInsights(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> insights = new HashMap<>();
                
                // First try to get latest insights from Firestore
                try {
                    Map<String, Object> firestoreInsights = getLatestInsightsFromFirestore(userId);
                    if (!firestoreInsights.isEmpty()) {
                        insights.putAll(firestoreInsights);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Failed to load from Firestore, falling back to local data", e);
                }
                
                // Get recent AI decisions from local storage
                List<AIDecision> recentDecisions = dataManager.getRecentAIDecisions(userId, 7);
                
                if (!recentDecisions.isEmpty()) {
                    AIDecision latestDecision = recentDecisions.get(0);
                    
                    // Only override if we don't have newer data from Firestore
                    if (!insights.containsKey("latest_risk_score")) {
                        insights.put("latest_risk_score", latestDecision.getRiskScore());
                        insights.put("latest_risk_level", latestDecision.getRiskLevel());
                        insights.put("latest_recommendations", latestDecision.getRecommendations());
                        insights.put("confidence_score", latestDecision.getConfidenceScore());
                    }
                    
                    // Trend analysis
                    if (recentDecisions.size() > 1) {
                        double riskTrend = calculateRiskTrend(recentDecisions);
                        insights.put("risk_trend", riskTrend);
                        insights.put("trend_direction", getTrendDirection(riskTrend));
                    }
                    
                    // Pattern insights
                    Map<String, Object> patterns = analyzePatterns(recentDecisions);
                    insights.putAll(patterns);
                }
                
                // Get model performance metrics
                Map<String, Object> modelInfo = aiEngine.getModelInfo();
                insights.put("model_info", modelInfo);
                
                return insights;
                
            } catch (Exception e) {
                Log.e(TAG, "Error getting AI insights", e);
                return new HashMap<>();
            }
        });
    }

    /**
     * Fetch historical symptom data from Firestore
     */
    private CompletableFuture<List<Map<String, Object>>> fetchHistoricalData(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Map<String, Object>> historicalData = new ArrayList<>();
                
                // Use a simpler query without custom index requirements
                Task<QuerySnapshot> task = firestore.collection("Users")
                    .document(userId)
                    .collection("symptoms")
                    .limit(30)
                    .get();
                
                // Wait for the task to complete
                QuerySnapshot querySnapshot = Tasks.await(task);
                
                for (DocumentSnapshot document : querySnapshot) {
                    Map<String, Object> data = document.getData();
                    if (data != null) {
                        // Add document ID as timestamp info
                        data.put("document_id", document.getId());
                        historicalData.add(data);
                    }
                }
                
                Log.d(TAG, "Fetched " + historicalData.size() + " historical symptom records");
                return historicalData;
                
            } catch (Exception e) {
                Log.e(TAG, "Error fetching historical data", e);
                return new ArrayList<>(); // Return empty list on error
            }
        });
    }

    /**
     * Update user's AI insights in Firestore
     */
    private CompletableFuture<Void> updateUserAIInsights(String userId, AIDecision decision, Map<String, Object> symptomData) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        try {
            Map<String, Object> aiInsights = new HashMap<>();
            aiInsights.put("latest_ai_analysis", new Date());
            aiInsights.put("current_risk_level", decision.getRiskLevel());
            
            // Ensure risk score is stored as Double for consistent Firebase handling
            Double riskScore = Double.valueOf(decision.getRiskScore());
            aiInsights.put("current_risk_score", riskScore);
            
            // Ensure confidence is stored as Double for consistent Firebase handling  
            Double confidence = Double.valueOf(decision.getConfidenceScore());
            aiInsights.put("ai_confidence", confidence);
            
            aiInsights.put("decision_id", decision.getDecisionId());
            
            // Store recommendations in Firebase
            aiInsights.put("current_recommendations", decision.getRecommendations());
            
            // Store the latest symptom/user data for the download icon
            aiInsights.put("latest_user_data", symptomData);
            
            Log.d(TAG, "Updating Firebase with risk score: " + riskScore + 
                  " (type: " + riskScore.getClass().getSimpleName() + ")" +
                  ", risk level: " + decision.getRiskLevel() + 
                  ", confidence: " + confidence + 
                  " (type: " + confidence.getClass().getSimpleName() + ")");
            
            firestore.collection("Users")
                .document(userId)
                .update(aiInsights)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "AI insights updated in Firestore");
                    future.complete(null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update AI insights", e);
                    future.completeExceptionally(e);
                });
                    
        } catch (Exception e) {
            Log.e(TAG, "Error updating user AI insights", e);
            future.completeExceptionally(e);
        }
        
        return future;
    }

    /**
     * Calculate risk trend from recent decisions
     */
    private double calculateRiskTrend(List<AIDecision> decisions) {
        if (decisions.size() < 2) return 0.0;
        
        // Compare latest vs earlier decisions
        double recent = decisions.subList(0, Math.min(3, decisions.size()))
            .stream()
            .mapToDouble(AIDecision::getRiskScore)
            .average()
            .orElse(0.0);
            
        double earlier = decisions.subList(Math.min(3, decisions.size()), decisions.size())
            .stream()
            .mapToDouble(AIDecision::getRiskScore)
            .average()
            .orElse(0.0);
            
        return recent - earlier;
    }

    /**
     * Get trend direction description
     */
    private String getTrendDirection(double trend) {
        if (trend > 0.1) return "INCREASING";
        else if (trend < -0.1) return "DECREASING";
        else return "STABLE";
    }

    /**
     * Analyze patterns in AI decisions
     */
    private Map<String, Object> analyzePatterns(List<AIDecision> decisions) {
        Map<String, Object> patterns = new HashMap<>();
        
        // Risk level distribution
        Map<String, Integer> riskLevelCounts = new HashMap<>();
        for (AIDecision decision : decisions) {
            String riskLevel = decision.getRiskLevel();
            riskLevelCounts.put(riskLevel, riskLevelCounts.getOrDefault(riskLevel, 0) + 1);
        }
        patterns.put("risk_level_distribution", riskLevelCounts);
        
        // Average confidence
        double avgConfidence = decisions.stream()
            .mapToDouble(AIDecision::getConfidenceScore)
            .average()
            .orElse(0.0);
        patterns.put("average_confidence", avgConfidence);
        
        // Most common recommendations
        Map<String, Integer> recommendationCounts = new HashMap<>();
        for (AIDecision decision : decisions) {
            for (String rec : decision.getRecommendations()) {
                recommendationCounts.put(rec, recommendationCounts.getOrDefault(rec, 0) + 1);
            }
        }
        patterns.put("common_recommendations", recommendationCounts);
        
        return patterns;
    }

    /**
     * Get current user ID
     */
    private String getCurrentUserId() {
        if (firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getUid();
        }
        return null;
    }

    /**
     * Generate personalized AI recommendations
     */
    public CompletableFuture<List<String>> getPersonalizedRecommendations(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get user's recent AI decisions
                List<AIDecision> recentDecisions = dataManager.getRecentAIDecisions(userId, 7);
                
                if (recentDecisions.isEmpty()) {
                    return Arrays.asList(
                        "Start tracking your symptoms to get personalized AI insights",
                        "Regular symptom logging helps improve AI recommendations",
                        "Consider setting up daily symptom check-ins"
                    );
                }
                
                AIDecision latestDecision = recentDecisions.get(0);
                List<String> recommendations = new ArrayList<>(latestDecision.getRecommendations());
                
                // Add trend-based recommendations
                if (recentDecisions.size() > 1) {
                    double trend = calculateRiskTrend(recentDecisions);
                    if (trend > 0.1) {
                        recommendations.add("📈 Your risk score is increasing - consider consulting your healthcare provider");
                    } else if (trend < -0.1) {
                        recommendations.add("📉 Great progress! Your risk score is improving");
                    }
                }
                
                return recommendations;
                
            } catch (Exception e) {
                Log.e(TAG, "Error generating personalized recommendations", e);
                return Arrays.asList("Unable to generate recommendations at this time");
            }
        });
    }

    /**
     * Get AI explainability report
     */
    public CompletableFuture<String> getExplainabilityReport(String decisionId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String userId = getCurrentUserId();
                if (userId == null) return "User not authenticated";
                
                AIDecision decision = dataManager.getAIDecision(userId, decisionId);
                if (decision == null) return "Decision not found";
                
                return decision.getExplanation().getFormattedExplanation();
                
            } catch (Exception e) {
                Log.e(TAG, "Error getting explainability report", e);
                return "Unable to generate explanation report";
            }
        });
    }

    /**
     * Check if AI analysis is available for user
     */
    public boolean isAIAnalysisAvailable() {
        String userId = getCurrentUserId();
        if (userId == null) return false;
        
        // Check if user has sufficient data for AI analysis
        try {
            List<Map<String, Object>> historicalData = fetchHistoricalData(userId).join();
            return historicalData.size() >= 3; // Minimum data points for meaningful analysis
        } catch (Exception e) {
            Log.e(TAG, "Error checking AI availability", e);
            return false;
        }
    }

    /**
     * Get AI model transparency information
     */
    public Map<String, Object> getModelTransparency() {
        Map<String, Object> transparency = new HashMap<>();
        transparency.putAll(aiEngine.getModelInfo());
        
        // Add additional transparency info
        transparency.put("data_privacy", "All data is processed locally and securely stored");
        transparency.put("model_type", "Rule-based expert system with machine learning components");
        transparency.put("last_training", "Model uses predefined medical knowledge and patterns");
        transparency.put("accuracy_notes", "Predictions are estimates and should not replace medical advice");
        
        return transparency;
    }
    
    /**
     * Get latest AI insights from Firestore
     */
    private Map<String, Object> getLatestInsightsFromFirestore(String userId) {
        try {
            Map<String, Object> insights = new HashMap<>();
            
            // Force a fresh read from the server (not cache) to get latest data
            Task<DocumentSnapshot> task = firestore.collection("Users")
                .document(userId)
                .get(Source.SERVER);  // Force server read
            
            DocumentSnapshot document = Tasks.await(task);
            
            if (document.exists()) {
                // Get the latest AI insights data
                Object riskScore = document.get("current_risk_score");
                String riskLevel = document.getString("current_risk_level");
                Object confidence = document.get("ai_confidence");
                Object userData = document.get("latest_user_data");
                Object recommendations = document.get("current_recommendations");
                
                if (riskScore != null) {
                    insights.put("latest_risk_score", riskScore);
                    Log.d(TAG, "Retrieved risk score from Firebase: " + riskScore + 
                          " (type: " + riskScore.getClass().getSimpleName() + ")");
                }
                if (riskLevel != null) {
                    insights.put("latest_risk_level", riskLevel);
                    Log.d(TAG, "Retrieved risk level from Firebase: " + riskLevel);
                }
                if (confidence != null) {
                    insights.put("confidence_score", confidence);
                    Log.d(TAG, "Retrieved confidence from Firebase: " + confidence + 
                          " (type: " + confidence.getClass().getSimpleName() + ")");
                }
                if (userData != null) {
                    insights.put("latest_user_data", userData);
                }
                if (recommendations != null) {
                    insights.put("latest_recommendations", recommendations);
                }
                
                Log.d(TAG, "Loaded latest insights from Firestore for user: " + userId);
            }
            
            return insights;
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading latest insights from Firestore", e);
            return new HashMap<>();
        }
    }
}
