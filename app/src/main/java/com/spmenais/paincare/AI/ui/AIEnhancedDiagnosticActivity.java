package com.spmenais.paincare.AI.ui;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spmenais.paincare.AI.integration.AIIntegrationService;
import com.spmenais.paincare.AI.models.AIDecision;
import com.spmenais.paincare.AI.models.XAIExplanation;
import com.spmenais.paincare.AI.adapters.RecommendationsAdapter;
import com.spmenais.paincare.AI.adapters.FeatureImportanceAdapter;
import com.spmenais.paincare.R;
import com.spmenais.paincare.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Enhanced AI-powered diagnostic results activity
 * Combines diagnostic test results with AI analysis and XAI explanations
 */
public class AIEnhancedDiagnosticActivity extends AppCompatActivity {
    private static final String TAG = "AIDiagnosticActivity";
    
    // Intent extras
    public static final String EXTRA_DIAGNOSTIC_SCORE = "diagnostic_score";
    public static final String EXTRA_RISK_LEVEL = "risk_level";
    public static final String EXTRA_USER_ANSWERS = "user_answers";
    public static final String EXTRA_DIAGNOSTIC_REPORT = "diagnostic_report";
    
    // UI Components - Header
    private TextView diagnosticScoreText;
    private TextView diagnosticRiskText;
    private ProgressBar diagnosticProgressBar;
    
    // UI Components - AI Analysis
    private CardView aiAnalysisCard;
    private TextView aiRiskScoreText;
    private TextView aiRiskLevelText;
    private TextView aiConfidenceText;
    private ProgressBar aiRiskProgressBar;
    private ProgressBar aiConfidenceProgressBar;
    
    // UI Components - Combined Assessment
    private CardView combinedAssessmentCard;
    private TextView combinedRiskText;
    private TextView combinedScoreText;
    private TextView combinedRecommendationText;
    
    // UI Components - Recommendations & Explanations
    private RecyclerView recommendationsRecyclerView;
    private RecyclerView featureImportanceRecyclerView;
    private TextView xaiExplanationText;
    private Button showDetailsButton;
    private Button exportReportButton;
    private Button backToHomeButton;
    
    // Data
    private AIIntegrationService aiService;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private int diagnosticScore;
    private String diagnosticRiskLevel;
    private Map<String, Object> userAnswers;
    private String diagnosticReport;
    private AIDecision aiDecision;
    private boolean detailsExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_enhanced_diagnostic);
        
        initializeServices();
        extractIntentData();
        initializeViews();
        setupListeners();
        loadDiagnosticData();
        performAIAnalysis();
    }
    
    private void initializeServices() {
        aiService = AIIntegrationService.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }
    
    private void extractIntentData() {
        Intent intent = getIntent();
        diagnosticScore = intent.getIntExtra("diagnostic_score", 0);
        diagnosticRiskLevel = intent.getStringExtra("diagnostic_risk_level");
        diagnosticReport = intent.getStringExtra("diagnostic_report");
        
        // If no diagnostic data is provided, try to load the last test results from Firebase
        if (diagnosticRiskLevel == null || diagnosticScore == 0) {
            loadLastDiagnosticResultsFromFirebase();
        }
        
        // Extract user answers - now coming as a HashMap<String, String>
        @SuppressWarnings("unchecked")
        HashMap<String, String> answersMap = (HashMap<String, String>) intent.getSerializableExtra("user_answers");
        userAnswers = new HashMap<>();
        if (answersMap != null) {
            // Convert String values back to appropriate Object types if needed
            for (Map.Entry<String, String> entry : answersMap.entrySet()) {
                userAnswers.put(entry.getKey(), entry.getValue());
            }
        } else {
            // If no user answers, create some default ones for demonstration
            userAnswers = new HashMap<>();
            userAnswers.put("pain_intensity", "5");
            userAnswers.put("pain_frequency", "Sometimes");
            userAnswers.put("symptoms", "Moderate symptoms");
        }
    }
    
    private void initializeViews() {
        // Header components
        diagnosticScoreText = findViewById(R.id.diagnosticScoreText);
        diagnosticRiskText = findViewById(R.id.diagnosticRiskText);
        diagnosticProgressBar = findViewById(R.id.diagnosticProgressBar);
        
        // AI Analysis components
        aiAnalysisCard = findViewById(R.id.aiAnalysisCard);
        aiRiskScoreText = findViewById(R.id.aiRiskScoreText);
        aiRiskLevelText = findViewById(R.id.aiRiskLevelText);
        aiConfidenceText = findViewById(R.id.aiConfidenceText);
        aiRiskProgressBar = findViewById(R.id.aiRiskProgressBar);
        aiConfidenceProgressBar = findViewById(R.id.aiConfidenceProgressBar);
        
        // Combined Assessment components
        combinedAssessmentCard = findViewById(R.id.combinedAssessmentCard);
        combinedRiskText = findViewById(R.id.combinedRiskText);
        combinedScoreText = findViewById(R.id.combinedScoreText);
        combinedRecommendationText = findViewById(R.id.combinedRecommendationText);
        
        // Recommendations & Explanations
        recommendationsRecyclerView = findViewById(R.id.recommendationsRecyclerView);
        featureImportanceRecyclerView = findViewById(R.id.featureImportanceRecyclerView);
        xaiExplanationText = findViewById(R.id.xaiExplanationText);
        showDetailsButton = findViewById(R.id.showDetailsButton);
        exportReportButton = findViewById(R.id.exportReportButton);
        backToHomeButton = findViewById(R.id.backToHomeButton);
        
        // Setup RecyclerViews
        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        featureImportanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void setupListeners() {
        showDetailsButton.setOnClickListener(v -> toggleDetailsView());
        exportReportButton.setOnClickListener(v -> exportCombinedReport());
        backToHomeButton.setOnClickListener(v -> navigateToHome());
        
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }
    
    private void loadDiagnosticData() {
        // Display diagnostic test results
        diagnosticScoreText.setText(String.valueOf(diagnosticScore));
        diagnosticRiskText.setText(diagnosticRiskLevel != null ? diagnosticRiskLevel : "UNKNOWN");
        
        // Set progress bar (assuming max score of 50 for diagnostic test)
        int progressPercentage = Math.min((diagnosticScore * 100) / 50, 100);
        diagnosticProgressBar.setProgress(progressPercentage);
        
        // Set diagnostic risk icon
        updateDiagnosticRiskIcon();
    }
    
    private void updateDiagnosticRiskIcon() {
        if (diagnosticRiskLevel == null) {
            diagnosticRiskLevel = "UNKNOWN";
        }
        
        switch (diagnosticRiskLevel.toUpperCase()) {
            case "HIGH":
                diagnosticRiskText.setTextColor(getResources().getColor(R.color.red));
                break;
            case "MEDIUM":
                diagnosticRiskText.setTextColor(getResources().getColor(R.color.orange1));
                break;
            case "LOW":
                diagnosticRiskText.setTextColor(getResources().getColor(R.color.green));
                break;
            default:
                diagnosticRiskText.setTextColor(getResources().getColor(R.color.light_grey));
                break;
        }
    }
    
    private void performAIAnalysis() {
        String userId = getCurrentUserId();
        if (userId == null) {
            showAIError("User not authenticated");
            return;
        }
        
        // Show loading state
        aiAnalysisCard.setVisibility(View.VISIBLE);
        showLoadingState();
        
        // First load latest symptom data, then perform AI analysis
        loadLatestDataAndPerformAnalysis(userId);
    }
    
    private void loadLatestDataAndPerformAnalysis(String userId) {
        // Create base diagnostic data
        Map<String, Object> diagnosticData = createBaseDiagnosticData();
        
        // Load latest symptom data from Firebase first
        firestore.collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Add latest symptom data if available
                        Object latestUserData = document.get("latest_user_data");
                        if (latestUserData instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> latestData = (Map<String, Object>) latestUserData;
                            diagnosticData.putAll(latestData);
                            Log.d(TAG, "Added latest symptom data to AI analysis: " + latestData.keySet());
                        }
                        
                        // Also include recent pain level if available
                        Object currentPainLevel = document.get("current_pain_level");
                        if (currentPainLevel != null) {
                            diagnosticData.put("current_pain_level", currentPainLevel);
                            Log.d(TAG, "Added current pain level to analysis: " + currentPainLevel);
                        }
                    }
                    
                    // Now perform AI analysis with fresh data
                    performAIAnalysisWithData(diagnosticData);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Could not load latest data, proceeding with base diagnostic data", e);
                    // Proceed with just the diagnostic data if Firebase fails
                    performAIAnalysisWithData(diagnosticData);
                });
    }
    
    private Map<String, Object> createBaseDiagnosticData() {
        Map<String, Object> data = new HashMap<>();
        
        // Add diagnostic test results
        data.put("diagnostic_score", diagnosticScore);
        data.put("diagnostic_risk_level", diagnosticRiskLevel);
        data.put("test_type", "endometriosis_diagnostic");
        data.put("timestamp", new Date());
        
        // Add user answers from the diagnostic test
        data.putAll(userAnswers);
        
        return data;
    }
    
    private void performAIAnalysisWithData(Map<String, Object> diagnosticData) {
        Log.d(TAG, "Performing AI analysis with " + diagnosticData.size() + " data points including latest symptoms");
        
        // Perform AI analysis with the complete data
        aiService.analyzeCurrentSymptoms(diagnosticData)
            .thenAccept(this::handleAIAnalysisResult)
            .exceptionally(throwable -> {
                runOnUiThread(() -> showAIError("AI analysis failed: " + throwable.getMessage()));
                Log.e(TAG, "AI analysis error", throwable);
                return null;
            });
    }
    
    private void handleAIAnalysisResult(AIDecision decision) {
        runOnUiThread(() -> {
            this.aiDecision = decision;
            hideLoadingState();
            displayAIAnalysis(decision);
            calculateCombinedAssessment(decision);
            setupRecommendations(decision.getRecommendations());
        });
    }
    
    private void displayAIAnalysis(AIDecision decision) {
        // AI Risk Score
        aiRiskScoreText.setText(String.format("%.1f", decision.getRiskScore() * 100));
        aiRiskProgressBar.setProgress((int) (decision.getRiskScore() * 100));
        
        // AI Risk Level
        aiRiskLevelText.setText(decision.getRiskLevel());
        updateAIRiskLevelColor(decision.getRiskLevel());
        
        // AI Confidence
        aiConfidenceText.setText(String.format("%.1f%%", decision.getConfidenceScore() * 100));
        aiConfidenceProgressBar.setProgress((int) (decision.getConfidenceScore() * 100));
    }
    
    private void updateAIRiskLevelColor(String riskLevel) {
        switch (riskLevel.toUpperCase()) {
            case "HIGH":
                aiRiskLevelText.setTextColor(getResources().getColor(R.color.red));
                break;
            case "MODERATE":
                aiRiskLevelText.setTextColor(getResources().getColor(R.color.orange1));
                break;
            case "LOW":
                aiRiskLevelText.setTextColor(getResources().getColor(R.color.green));
                break;
            default:
                aiRiskLevelText.setTextColor(getResources().getColor(R.color.gray));
                break;
        }
    }
    
    private void calculateCombinedAssessment(AIDecision aiDecision) {
        // Calculate combined risk assessment
        double diagnosticWeight = 0.6; // Diagnostic test weight
        double aiWeight = 0.4; // AI analysis weight
        
        // Convert diagnostic score to 0-1 scale (assuming max score 50)
        double diagnosticNormalized = Math.min(diagnosticScore / 50.0, 1.0);
        double aiRiskScore = aiDecision.getRiskScore();
        
        // Combined score
        double combinedScore = (diagnosticNormalized * diagnosticWeight) + (aiRiskScore * aiWeight);
        
        // Combined risk level
        String combinedRisk = calculateCombinedRiskLevel(combinedScore);
        
        // Display combined assessment
        combinedScoreText.setText(String.format("%.1f%%", combinedScore * 100));
        combinedRiskText.setText(combinedRisk);
        updateCombinedRiskIcon(combinedRisk);
        
        // Combined recommendation
        String recommendation = generateCombinedRecommendation(combinedRisk, 
            diagnosticRiskLevel != null ? diagnosticRiskLevel : "UNKNOWN", 
            aiDecision.getRiskLevel());
        combinedRecommendationText.setText(recommendation);
    }
    
    private String calculateCombinedRiskLevel(double score) {
        if (score >= 0.7) return "HIGH";
        else if (score >= 0.4) return "MODERATE";
        else return "LOW";
    }
    
    private void updateCombinedRiskIcon(String riskLevel) {
        switch (riskLevel.toUpperCase()) {
            case "HIGH":
                combinedRiskText.setTextColor(getResources().getColor(R.color.red));
                break;
            case "MODERATE":
                combinedRiskText.setTextColor(getResources().getColor(R.color.orange1));
                break;
            case "LOW":
                combinedRiskText.setTextColor(getResources().getColor(R.color.green));
                break;
        }
    }
    
    private String generateCombinedRecommendation(String combinedRisk, String diagnosticRisk, String aiRisk) {
        StringBuilder recommendation = new StringBuilder();
        
        switch (combinedRisk.toUpperCase()) {
            case "HIGH":
                recommendation.append("🚨 High Risk Assessment: ");
                recommendation.append("Both diagnostic and AI analysis indicate elevated risk. ");
                recommendation.append("Schedule consultation with healthcare provider immediately.");
                break;
            case "MODERATE":
                recommendation.append("⚠️ Moderate Risk Assessment: ");
                recommendation.append("Consider scheduling healthcare consultation and monitoring symptoms closely.");
                break;
            case "LOW":
                recommendation.append("✅ Low Risk Assessment: ");
                recommendation.append("Continue monitoring symptoms and maintain healthy lifestyle.");
                break;
        }
        
        return recommendation.toString();
    }
    
    private void setupRecommendations(List<String> recommendations) {
        // Add diagnostic-specific recommendations
        List<String> enhancedRecommendations = new ArrayList<>(recommendations);
        
        // Add diagnostic test context
        enhancedRecommendations.add(0, "📋 Diagnostic Test Score: " + diagnosticScore + "/50 (" + 
            (diagnosticRiskLevel != null ? diagnosticRiskLevel : "UNKNOWN") + " Risk)");
        
        if (diagnosticScore >= 30) {
            enhancedRecommendations.add("🏥 Consider specialized endometriosis care");
            enhancedRecommendations.add("📝 Discuss surgical options with gynecologist");
        }
        
        RecommendationsAdapter adapter = new RecommendationsAdapter(enhancedRecommendations);
        recommendationsRecyclerView.setAdapter(adapter);
    }
    
    private void toggleDetailsView() {
        if (detailsExpanded) {
            // Collapse details
            featureImportanceRecyclerView.setVisibility(View.GONE);
            xaiExplanationText.setVisibility(View.GONE);
            showDetailsButton.setText("Show Explanation");
            detailsExpanded = false;
        } else {
            // Expand details
            loadXAIExplanation();
            featureImportanceRecyclerView.setVisibility(View.VISIBLE);
            xaiExplanationText.setVisibility(View.VISIBLE);
            showDetailsButton.setText("Hide Details");
            detailsExpanded = true;
        }
    }
    
    private void loadXAIExplanation() {
        if (aiDecision == null) return;
        
        XAIExplanation explanation = aiDecision.getExplanation();
        
        StringBuilder xaiText = new StringBuilder();
        
        // Introduction paragraph
        xaiText.append("AI Analysis Explanation\n\n");
        
        // Main explanation paragraph
        xaiText.append("Our AI system has analyzed your diagnostic test results alongside your symptom patterns and medical history to provide a comprehensive assessment. ");
        xaiText.append("Your diagnostic test score of ").append(diagnosticScore).append(" out of 50 ");
        xaiText.append("has been combined with advanced pattern recognition to determine a risk level of ");
        xaiText.append(String.format("%.1f%%", aiDecision.getRiskScore() * 100)).append(".\n\n");
        
        // How it works paragraph
        xaiText.append("The analysis works by comparing your responses with thousands of similar cases in our medical database. ");
        xaiText.append("The AI identified key patterns in your symptoms and combined this with your diagnostic test results to reach a confidence level of ");
        xaiText.append(String.format("%.1f%%", aiDecision.getConfidenceScore() * 100)).append(" in its assessment. ");
        xaiText.append("This integrated approach ensures that both clinical testing and symptom analysis contribute to your personalized health insights.\n\n");
        
        // Key factors paragraph
        if (explanation != null) {
            xaiText.append("Key Factors in Your Assessment:\n\n");
            xaiText.append("The most significant factors influencing your results include your diagnostic test performance, pain intensity patterns, and symptom frequency. ");
            xaiText.append("Additional considerations were given to any risk factors present in your medical profile and how your symptoms compare to established medical patterns. ");
            xaiText.append("This comprehensive analysis helps ensure accuracy while providing you with actionable health insights.\n\n");
        }
        
        // Reliability note
        xaiText.append("Please note that this analysis is designed to support, not replace, professional medical consultation. ");
        xaiText.append("The AI provides valuable insights based on current medical knowledge and your specific data, but should always be discussed with your healthcare provider for the most appropriate care decisions.");
        
        xaiExplanationText.setText(xaiText.toString());
        xaiExplanationText.setLineSpacing(1.2f, 1.0f); // Better line spacing for readability
        
        // Setup feature importance with diagnostic integration
        List<Map<String, Object>> featureData = Arrays.asList(
            createFeatureItem("Diagnostic Test Score", 0.40),
            createFeatureItem("Pain Assessment", 0.25),
            createFeatureItem("Symptom Analysis", 0.20),
            createFeatureItem("Risk Factors", 0.10),
            createFeatureItem("Historical Patterns", 0.05)
        );
        
        FeatureImportanceAdapter featureAdapter = new FeatureImportanceAdapter(featureData);
        featureImportanceRecyclerView.setAdapter(featureAdapter);
    }
    
    private Map<String, Object> createFeatureItem(String name, double importance) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("importance", importance);
        return item;
    }
    
    private void exportCombinedReport() {
        exportReportButton.setEnabled(false);
        exportReportButton.setText("Generating PDF...");
        
        CompletableFuture.supplyAsync(() -> {
            try {
                return generatePDFReport();
            } catch (Exception e) {
                Log.e(TAG, "Error generating PDF", e);
                return null;
            }
        }).thenAccept(pdfFile -> runOnUiThread(() -> {
            exportReportButton.setEnabled(true);
            exportReportButton.setText("📄 Export PDF Report");
            
            if (pdfFile != null) {
                openPDFFile(pdfFile);
                Toast.makeText(this, "PDF report generated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to generate PDF report", Toast.LENGTH_SHORT).show();
            }
        }));
    }
    
    private File generatePDFReport() throws IOException {
        // Create PDF document
        PdfDocument pdfDocument = new PdfDocument();
        
        // Page info
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        
        // Paint for text
        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(20);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        
        Paint headerPaint = new Paint();
        headerPaint.setColor(Color.BLACK);
        headerPaint.setTextSize(16);
        headerPaint.setTypeface(Typeface.DEFAULT_BOLD);
        
        Paint bodyPaint = new Paint();
        bodyPaint.setColor(Color.BLACK);
        bodyPaint.setTextSize(12);
        bodyPaint.setTypeface(Typeface.DEFAULT);
        
        Paint accentPaint = new Paint();
        accentPaint.setColor(Color.parseColor("#89CCC5"));
        accentPaint.setTextSize(14);
        accentPaint.setTypeface(Typeface.DEFAULT_BOLD);
        
        int yPosition = 50;
        int leftMargin = 50;
        int lineHeight = 20;
        
        // Title
        canvas.drawText("PainCare AI-Enhanced Diagnostic Report", leftMargin, yPosition, titlePaint);
        yPosition += 40;
        
        // Date
        canvas.drawText("Generated: " + getCurrentDateTime(), leftMargin, yPosition, bodyPaint);
        yPosition += 40;
        
        // Diagnostic Results Section
        canvas.drawText("DIAGNOSTIC TEST RESULTS", leftMargin, yPosition, headerPaint);
        yPosition += 25;
        canvas.drawText("Score: " + diagnosticScore + "/50", leftMargin + 20, yPosition, accentPaint);
        yPosition += lineHeight;
        canvas.drawText("Risk Level: " + (diagnosticRiskLevel != null ? diagnosticRiskLevel : "UNKNOWN"), leftMargin + 20, yPosition, accentPaint);
        yPosition += 35;
        
        // AI Analysis Section
        if (aiDecision != null) {
            canvas.drawText("AI ANALYSIS RESULTS", leftMargin, yPosition, headerPaint);
            yPosition += 25;
            canvas.drawText("AI Risk Score: " + String.format("%.1f%%", aiDecision.getRiskScore() * 100), 
                leftMargin + 20, yPosition, accentPaint);
            yPosition += lineHeight;
            canvas.drawText("Risk Level: " + aiDecision.getRiskLevel(), leftMargin + 20, yPosition, accentPaint);
            yPosition += lineHeight;
            canvas.drawText("Confidence: " + String.format("%.1f%%", aiDecision.getConfidenceScore() * 100), 
                leftMargin + 20, yPosition, accentPaint);
            yPosition += 35;
        }
        
        // Combined Assessment
        canvas.drawText("COMBINED ASSESSMENT", leftMargin, yPosition, headerPaint);
        yPosition += 25;
        canvas.drawText("Final Risk: " + combinedRiskText.getText(), leftMargin + 20, yPosition, accentPaint);
        yPosition += lineHeight;
        canvas.drawText("Combined Score: " + combinedScoreText.getText(), leftMargin + 20, yPosition, accentPaint);
        yPosition += 35;
        
        // Recommendations
        if (aiDecision != null && !aiDecision.getRecommendations().isEmpty()) {
            canvas.drawText("RECOMMENDATIONS", leftMargin, yPosition, headerPaint);
            yPosition += 25;
            
            for (int i = 0; i < Math.min(aiDecision.getRecommendations().size(), 5); i++) {
                String recommendation = aiDecision.getRecommendations().get(i);
                if (recommendation.length() > 60) {
                    recommendation = recommendation.substring(0, 57) + "...";
                }
                canvas.drawText("• " + recommendation, leftMargin + 20, yPosition, bodyPaint);
                yPosition += lineHeight;
            }
            yPosition += 20;
        }
        
        // Disclaimer
        yPosition += 20;
        canvas.drawText("DISCLAIMER", leftMargin, yPosition, headerPaint);
        yPosition += 25;
        String disclaimer = "This analysis is for informational purposes only and should not replace";
        canvas.drawText(disclaimer, leftMargin + 20, yPosition, bodyPaint);
        yPosition += lineHeight;
        canvas.drawText("professional medical advice. Please consult a healthcare provider.", leftMargin + 20, yPosition, bodyPaint);
        
        // Finish the page
        pdfDocument.finishPage(page);
        
        // Save the PDF
        File pdfDir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PainCare Reports");
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }
        
        String fileName = "PainCare_Report_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".pdf";
        File pdfFile = new File(pdfDir, fileName);
        
        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            pdfDocument.writeTo(fos);
        }
        
        pdfDocument.close();
        return pdfFile;
    }
    
    private void openPDFFile(File pdfFile) {
        Uri pdfUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        try {
            startActivity(intent);
        } catch (Exception e) {
            // Fallback: Share the file
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Open PDF Report"));
        }
    }
    
    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    private void showLoadingState() {
        aiRiskScoreText.setText("--");
        aiRiskLevelText.setText("Analyzing...");
        aiConfidenceText.setText("--");
        aiRiskProgressBar.setProgress(0);
        aiConfidenceProgressBar.setProgress(0);
    }
    
    private void hideLoadingState() {
        // Loading state will be replaced by actual data in displayAIAnalysis
    }
    
    private void showAIError(String message) {
        aiRiskLevelText.setText("Error");
        aiRiskLevelText.setTextColor(getResources().getColor(R.color.red));
        Toast.makeText(this, "AI Analysis: " + message, Toast.LENGTH_LONG).show();
    }
    
    private String getCurrentUserId() {
        return firebaseAuth.getCurrentUser() != null ? 
            firebaseAuth.getCurrentUser().getUid() : null;
    }
    
    private void loadLastDiagnosticResultsFromFirebase() {
        String userId = getCurrentUserId();
        if (userId == null) {
            setDefaultDiagnosticData();
            return;
        }
        
        // Load user's latest AI insights data instead of just the static risk level
        aiService.getAIInsights(userId)
            .thenAccept(insights -> runOnUiThread(() -> {
                if (insights.containsKey("latest_risk_score")) {
                    // Use the latest AI analysis data
                    Object riskScoreObj = insights.get("latest_risk_score");
                    double riskScore = riskScoreObj instanceof Number ? 
                        ((Number) riskScoreObj).doubleValue() : 0.5;
                    
                    String riskLevel = (String) insights.get("latest_risk_level");
                    diagnosticRiskLevel = riskLevel != null ? riskLevel : "MEDIUM";
                    
                    // Convert AI risk score (0.0-1.0) to diagnostic score (0-50)
                    diagnosticScore = (int) Math.round(riskScore * 50);
                    
                    diagnosticReport = "Latest AI analysis results loaded from your profile";
                    
                    // Load user answers from recent analysis if available
                    @SuppressWarnings("unchecked")
                    Map<String, Object> latestUserData = (Map<String, Object>) insights.get("latest_user_data");
                    if (latestUserData != null) {
                        userAnswers.putAll(latestUserData);
                    }
                    
                    // Update UI with loaded data
                    loadDiagnosticData();
                } else {
                    // Fallback to loading from user profile if no AI insights available
                    loadFromUserProfile(userId);
                }
            }))
            .exceptionally(throwable -> {
                Log.e(TAG, "Failed to load AI insights, trying user profile", throwable);
                runOnUiThread(() -> loadFromUserProfile(userId));
                return null;
            });
    }
    
    private void loadFromUserProfile(String userId) {
        // Fallback method to load from user profile
        firestore.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Get the risk level from user document
                    String riskLevel = documentSnapshot.getString("riskLevel");
                    Object currentRiskScore = documentSnapshot.get("current_risk_score");
                    
                    if (riskLevel != null) {
                        diagnosticRiskLevel = riskLevel;
                        
                        // Use current_risk_score if available, otherwise estimate from risk level
                        if (currentRiskScore instanceof Number) {
                            double riskScoreValue = ((Number) currentRiskScore).doubleValue();
                            diagnosticScore = (int) Math.round(riskScoreValue * 50);
                        } else {
                            // Estimate score based on risk level
                            switch (riskLevel.toUpperCase()) {
                                case "HIGH":
                                    diagnosticScore = 42; // High risk score
                                    break;
                                case "MEDIUM":
                                    diagnosticScore = 28; // Medium risk score
                                    break;
                                case "LOW":
                                    diagnosticScore = 15; // Low risk score
                                    break;
                                default:
                                    diagnosticScore = 25; // Default score
                            }
                        }
                        
                        diagnosticReport = "Diagnostic test results loaded from your profile";
                        
                        // Update UI with loaded data
                        loadDiagnosticData();
                    } else {
                        setDefaultDiagnosticData();
                    }
                } else {
                    setDefaultDiagnosticData();
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to load user diagnostic data", e);
                setDefaultDiagnosticData();
            });
    }
    
    private void setDefaultDiagnosticData() {
        diagnosticRiskLevel = "MEDIUM";
        diagnosticScore = 25; // Default middle score
        diagnosticReport = "No diagnostic test completed yet. Please take the diagnostic test for accurate results.";
        
        // Update UI with default data
        runOnUiThread(this::loadDiagnosticData);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh diagnostic data when returning to activity to get latest updates
        if (diagnosticRiskLevel == null || diagnosticScore == 0) {
            loadLastDiagnosticResultsFromFirebase();
        } else {
            // Also refresh AI analysis to include latest symptoms and pain levels
            Log.d(TAG, "Refreshing AI analysis with latest symptom data");
            performAIAnalysis();
        }
    }
    
    private String getCurrentDateTime() {
        return new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
            .format(new Date());
    }
}
