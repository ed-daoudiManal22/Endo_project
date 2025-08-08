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
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Activity to display AI analysis results and explanations
 */
public class AIInsightsActivity extends AppCompatActivity {
    private static final String TAG = "AIInsightsActivity";
    
    // UI Components
    private TextView riskScoreText;
    private TextView riskLevelText;
    private TextView confidenceText;
    private TextView analysisDateText;
    private TextView primaryReasonText;
    private TextView explanationText;
    private ProgressBar riskProgressBar;
    private ProgressBar confidenceProgressBar;
    private RecyclerView recommendationsRecyclerView;
    private RecyclerView featureImportanceRecyclerView;
    private CardView explanationCard;
    private Button showDetailsButton;
    private Button exportReportButton;
    private ImageView riskLevelIcon;
    private LinearLayout loadingLayout;
    private LinearLayout contentLayout;
    
    // Services
    private AIIntegrationService aiService;
    private FirebaseAuth firebaseAuth;
    private AIDecision currentAIDecision;
    
    // Data
    private AIDecision currentDecision;
    private boolean detailsExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_insights);
        
        initializeComponents();
        setupUI();
        loadAIInsights();
    }

    private void initializeComponents() {
        // Initialize services
        aiService = AIIntegrationService.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        
        // Initialize UI components
        riskScoreText = findViewById(R.id.riskScoreText);
        riskLevelText = findViewById(R.id.riskLevelText);
        confidenceText = findViewById(R.id.confidenceText);
        analysisDateText = findViewById(R.id.analysisDateText);
        primaryReasonText = findViewById(R.id.primaryReasonText);
        explanationText = findViewById(R.id.explanationText);
        riskProgressBar = findViewById(R.id.riskProgressBar);
        confidenceProgressBar = findViewById(R.id.confidenceProgressBar);
        recommendationsRecyclerView = findViewById(R.id.recommendationsRecyclerView);
        featureImportanceRecyclerView = findViewById(R.id.featureImportanceRecyclerView);
        explanationCard = findViewById(R.id.explanationCard);
        showDetailsButton = findViewById(R.id.showDetailsButton);
        exportReportButton = findViewById(R.id.exportReportButton);
        riskLevelIcon = findViewById(R.id.riskLevelIcon);
        loadingLayout = findViewById(R.id.loadingLayout);
        contentLayout = findViewById(R.id.contentLayout);
        
        Log.d(TAG, "Components initialized");
    }

    private void setupUI() {
        // Setup RecyclerViews
        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        featureImportanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Setup button listeners
        showDetailsButton.setOnClickListener(v -> toggleExplanationDetails());
        exportReportButton.setOnClickListener(v -> exportAIReport());
        
        // Setup back button
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
        
        // Initially hide content and show loading
        contentLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    private void loadAIInsights() {
        String userId = getCurrentUserId();
        if (userId == null) {
            showError("User not authenticated");
            return;
        }
        
        // Check if we have recent AI data
        aiService.getAIInsights(userId)
            .thenAccept(insights -> runOnUiThread(() -> {
                if (insights.containsKey("latest_risk_score")) {
                    displayAIInsights(insights);
                } else {
                    showNoDataMessage();
                }
            }))
            .exceptionally(throwable -> {
                runOnUiThread(() -> showError("Failed to load AI insights"));
                Log.e(TAG, "Error loading AI insights", throwable);
                return null;
            });
    }

    private void displayAIInsights(Map<String, Object> insights) {
        try {
            Log.d(TAG, "Displaying AI insights. Available keys: " + insights.keySet());
            
            // Risk Score
            Object riskScoreObj = insights.get("latest_risk_score");
            double riskScore = riskScoreObj instanceof Number ? 
                ((Number) riskScoreObj).doubleValue() : 0.0;
            
            // Validate risk score is in expected range (0.0 to 1.0)
            if (riskScore < 0.0 || riskScore > 1.0) {
                Log.w(TAG, "Invalid risk score received: " + riskScore + ". Using fallback value.");
                riskScore = 0.0;
            }
            
            Log.d(TAG, "Risk score from insights: " + riskScoreObj + " (converted to: " + riskScore + ")");
            
            riskScoreText.setText(String.format("%.1f", riskScore * 100));
            riskProgressBar.setProgress((int) (riskScore * 100));
            
            // Risk Level
            String riskLevel = (String) insights.get("latest_risk_level");
            Log.d(TAG, "Risk level from insights: " + riskLevel);
            riskLevelText.setText(riskLevel != null ? riskLevel : "UNKNOWN");
            updateRiskLevelIcon(riskLevel);
            
            // Confidence Score
            Object confidenceObj = insights.get("confidence_score");
            double confidence = confidenceObj instanceof Number ? 
                ((Number) confidenceObj).doubleValue() : 0.0;
            
            Log.d(TAG, "Confidence score from insights: " + confidenceObj + " (converted to: " + confidence + ")");
            
            confidenceText.setText(String.format("%.0f%%", confidence * 100));
            confidenceProgressBar.setProgress((int) (confidence * 100));
            
            // Analysis Date
            analysisDateText.setText("Last updated: " + getCurrentDateTime());
            
            // Recommendations
            @SuppressWarnings("unchecked")
            List<String> recommendations = (List<String>) insights.get("latest_recommendations");
            if (recommendations != null && !recommendations.isEmpty()) {
                setupRecommendations(recommendations);
            } else {
                // Provide default recommendations if none are available
                List<String> defaultRecommendations = Arrays.asList(
                    "📊 Track your symptoms regularly to identify patterns",
                    "💤 Maintain a consistent sleep schedule (7-9 hours)",
                    "🧘 Practice stress reduction techniques like meditation",
                    "🚶 Engage in gentle, regular physical activity",
                    "👩‍⚕️ Schedule regular check-ups with your healthcare provider"
                );
                setupRecommendations(defaultRecommendations);
            }
            
            // Primary Reason (placeholder - would come from XAI explanation)
            primaryReasonText.setText("Analysis based on symptom patterns, pain levels, and historical trends");
            
            // Show content
            loadingLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
            
        } catch (Exception e) {
            Log.e(TAG, "Error displaying AI insights", e);
            showError("Error displaying insights");
        }
    }

    private void updateRiskLevelIcon(String riskLevel) {
        if (riskLevel == null) return;
        
        switch (riskLevel.toUpperCase()) {
            case "HIGH":
                riskLevelIcon.setImageResource(R.drawable.ic_warning_red);
                riskLevelText.setTextColor(getResources().getColor(R.color.red));
                break;
            case "MODERATE":
                riskLevelIcon.setImageResource(R.drawable.ic_warning_orange);
                riskLevelText.setTextColor(getResources().getColor(R.color.orange1));
                break;
            case "LOW":
                riskLevelIcon.setImageResource(R.drawable.ic_check_green);
                riskLevelText.setTextColor(getResources().getColor(R.color.green));
                break;
            case "MINIMAL":
                riskLevelIcon.setImageResource(R.drawable.ic_check_blue);
                riskLevelText.setTextColor(getResources().getColor(R.color.blue));
                break;
            default:
                riskLevelIcon.setImageResource(R.drawable.ic_info_gray);
                break;
        }
    }

    private void setupRecommendations(List<String> recommendations) {
        Log.d(TAG, "Setting up recommendations: " + recommendations.size() + " items");
        for (int i = 0; i < recommendations.size(); i++) {
            Log.d(TAG, "Recommendation " + i + ": " + recommendations.get(i));
        }
        RecommendationsAdapter adapter = new RecommendationsAdapter(recommendations);
        recommendationsRecyclerView.setAdapter(adapter);
    }

    private void toggleExplanationDetails() {
        if (detailsExpanded) {
            // Collapse details
            featureImportanceRecyclerView.setVisibility(View.GONE);
            explanationText.setVisibility(View.GONE);
            showDetailsButton.setText("Show Details");
            detailsExpanded = false;
        } else {
            // Expand details
            loadExplanationDetails();
            featureImportanceRecyclerView.setVisibility(View.VISIBLE);
            explanationText.setVisibility(View.VISIBLE);
            showDetailsButton.setText("Hide Details");
            detailsExpanded = true;
        }
    }

    private void loadExplanationDetails() {
        // Load detailed explanation
        explanationText.setText("💡 How Our AI Analysis Works:\n\n" +
            "Our intelligent system carefully examines your health data to provide personalized insights. Here's what we analyze:\n\n" +
            
            "📊 Data Analysis Process:\n" +
            "• Pain Patterns: We track how your pain levels change over time, identifying recurring patterns and trends that might not be immediately obvious.\n\n" +
            "• Symptom Correlation: The AI looks for connections between different symptoms you experience, helping identify potential trigger relationships.\n\n" +
            "• Historical Context: Your past data helps us understand what's normal for you and detect meaningful changes in your condition.\n\n" +
            "• Risk Factors: We evaluate various elements that might influence your pain, including lifestyle factors, environmental triggers, and symptom severity.\n\n" +
            
            "🎯 Personalized Insights:\n" +
            "The AI doesn't just look at individual data points - it considers the complete picture of your health journey. By analyzing patterns across weeks and months, it can identify:\n" +
            "• Early warning signs of flare-ups\n" +
            "• Effective management strategies based on your history\n" +
            "• Potential triggers you might want to monitor\n" +
            "• Optimal timing for medical consultations\n\n" +
            
            "⚠️ Important Note:\n" +
            "These AI insights are designed to complement your healthcare team's expertise, not replace it. Always consult with your healthcare providers for medical decisions. Our AI serves as an additional tool to help you better understand and manage your condition.");
        
        // Setup feature importance (mock data for demonstration)
        List<Map<String, Object>> featureData = Arrays.asList(
            createFeatureItem("Pain Level Trends", 0.35),
            createFeatureItem("Symptom Severity", 0.25),
            createFeatureItem("Pattern Recognition", 0.20),
            createFeatureItem("Trigger Analysis", 0.15),
            createFeatureItem("Historical Context", 0.05)
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

    private void exportAIReport() {
        String userId = getCurrentUserId();
        if (userId == null) return;
        
        // Show loading
        exportReportButton.setEnabled(false);
        exportReportButton.setText("Exporting...");
        
        // Generate and share report
        generateAIReport(userId);
    }

    private void generateAIReport(String userId) {
        aiService.getAIInsights(userId)
            .thenAccept(insights -> {
                StringBuilder report = new StringBuilder();
                report.append("PainCare AI Analysis Report\n");
                report.append("============================\n\n");
                report.append("Generated: ").append(getCurrentDateTime()).append("\n\n");
                
                // Risk Assessment
                Object riskScore = insights.get("latest_risk_score");
                String riskLevel = (String) insights.get("latest_risk_level");
                report.append("Risk Assessment:\n");
                report.append("- Risk Score: ").append(String.format("%.1f%%", 
                    ((Number) riskScore).doubleValue() * 100)).append("\n");
                report.append("- Risk Level: ").append(riskLevel).append("\n\n");
                
                // Recommendations
                @SuppressWarnings("unchecked")
                List<String> recommendations = (List<String>) insights.get("latest_recommendations");
                if (recommendations != null) {
                    report.append("AI Recommendations:\n");
                    for (int i = 0; i < recommendations.size(); i++) {
                        report.append((i + 1)).append(". ").append(recommendations.get(i)).append("\n");
                    }
                    report.append("\n");
                }
                
                // Disclaimer
                report.append("Disclaimer:\n");
                report.append("This AI analysis is for informational purposes only and should not ");
                report.append("replace professional medical advice, diagnosis, or treatment.");
                
                runOnUiThread(() -> {
                    generatePDFReport(insights);
                    exportReportButton.setEnabled(true);
                    exportReportButton.setText("Export Report");
                });
            })
            .exceptionally(throwable -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to generate report", Toast.LENGTH_SHORT).show();
                    exportReportButton.setEnabled(true);
                    exportReportButton.setText("Export Report");
                });
                return null;
            });
    }

    private void generatePDFReport(Map<String, Object> insights) {
        try {
            // Create PDF document
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            
            // Set up paint objects
            Paint titlePaint = new Paint();
            titlePaint.setColor(Color.rgb(33, 150, 243));
            titlePaint.setTextSize(24);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            
            Paint headingPaint = new Paint();
            headingPaint.setColor(Color.rgb(63, 81, 181));
            headingPaint.setTextSize(18);
            headingPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            
            Paint bodyPaint = new Paint();
            bodyPaint.setColor(Color.BLACK);
            bodyPaint.setTextSize(14);
            
            Paint smallPaint = new Paint();
            smallPaint.setColor(Color.GRAY);
            smallPaint.setTextSize(12);
            
            // Draw header
            int yPosition = 80;
            canvas.drawText("PainCare AI Analysis Report", 50, yPosition, titlePaint);
            yPosition += 30;
            canvas.drawText("Generated: " + getCurrentDateTime(), 50, yPosition, smallPaint);
            yPosition += 60;
            
            // Draw AI Analysis section
            canvas.drawText("AI Analysis Results", 50, yPosition, headingPaint);
            yPosition += 40;
            
            // Risk Assessment
            Object riskScore = insights.get("latest_risk_score");
            String riskLevel = (String) insights.get("latest_risk_level");
            if (riskScore != null && riskLevel != null) {
                canvas.drawText("Risk Assessment:", 70, yPosition, bodyPaint);
                yPosition += 25;
                canvas.drawText("• Risk Score: " + String.format("%.1f%%", 
                    ((Number) riskScore).doubleValue() * 100), 90, yPosition, bodyPaint);
                yPosition += 25;
                canvas.drawText("• Risk Level: " + riskLevel, 90, yPosition, bodyPaint);
                yPosition += 40;
            }
            
            // AI Explanation
            String explanation = (String) insights.get("latest_explanation");
            if (explanation != null) {
                canvas.drawText("AI Explanation:", 70, yPosition, bodyPaint);
                yPosition += 25;
                
                // Break explanation into multiple lines
                String[] explanationLines = wrapText(explanation, 65); // approximately 65 chars per line
                for (String line : explanationLines) {
                    canvas.drawText("• " + line, 90, yPosition, bodyPaint);
                    yPosition += 20;
                }
                yPosition += 20;
            }
            
            // Recommendations
            @SuppressWarnings("unchecked")
            List<String> recommendations = (List<String>) insights.get("latest_recommendations");
            if (recommendations != null && !recommendations.isEmpty()) {
                canvas.drawText("AI Recommendations:", 70, yPosition, bodyPaint);
                yPosition += 25;
                
                for (int i = 0; i < recommendations.size() && i < 5; i++) {
                    String[] recLines = wrapText(recommendations.get(i), 60);
                    canvas.drawText((i + 1) + ". " + recLines[0], 90, yPosition, bodyPaint);
                    yPosition += 20;
                    
                    for (int j = 1; j < recLines.length; j++) {
                        canvas.drawText("   " + recLines[j], 90, yPosition, bodyPaint);
                        yPosition += 20;
                    }
                    yPosition += 5;
                }
                yPosition += 20;
            }
            
            // Disclaimer
            yPosition += 40;
            canvas.drawText("Important Disclaimer:", 50, yPosition, headingPaint);
            yPosition += 30;
            
            String disclaimer = "This AI analysis is for informational purposes only and should not replace professional medical advice, diagnosis, or treatment. Always consult with qualified healthcare providers for medical concerns.";
            String[] disclaimerLines = wrapText(disclaimer, 70);
            for (String line : disclaimerLines) {
                canvas.drawText(line, 70, yPosition, smallPaint);
                yPosition += 18;
            }
            
            document.finishPage(page);
            
            // Save PDF to file
            String fileName = "AI_Analysis_Report_" + System.currentTimeMillis() + ".pdf";
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
            
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            
            // Open PDF file
            openPDFFile(file);
            
        } catch (IOException e) {
            Log.e(TAG, "Error generating PDF", e);
            Toast.makeText(this, "Error generating PDF report", Toast.LENGTH_SHORT).show();
        }
    }
    
    private String[] wrapText(String text, int maxCharsPerLine) {
        if (text == null || text.isEmpty()) return new String[]{""};
        
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 <= maxCharsPerLine) {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    lines.add(word);
                }
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.toArray(new String[0]);
    }
    
    private void openPDFFile(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(this, 
                "com.spmenais.paincare.provider", file);
            
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                Toast.makeText(this, "PDF report generated successfully", Toast.LENGTH_LONG).show();
            } else {
                shareFile(uri);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening PDF file", e);
            Toast.makeText(this, "PDF generated but couldn't open. Check Downloads folder.", Toast.LENGTH_LONG).show();
        }
    }
    
    private void shareFile(Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share AI Report"));
    }

    private void showNoDataMessage() {
        loadingLayout.setVisibility(View.GONE);
        
        TextView noDataText = new TextView(this);
        noDataText.setText("No AI analysis available yet.\n\nStart tracking your symptoms to get personalized AI insights!");
        noDataText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        noDataText.setPadding(32, 32, 32, 32);
        
        contentLayout.removeAllViews();
        contentLayout.addView(noDataText);
        contentLayout.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        loadingLayout.setVisibility(View.GONE);
        
        TextView errorText = new TextView(this);
        errorText.setText("Error: " + message);
        errorText.setTextColor(getResources().getColor(R.color.red));
        errorText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        errorText.setPadding(32, 32, 32, 32);
        
        contentLayout.removeAllViews();
        contentLayout.addView(errorText);
        contentLayout.setVisibility(View.VISIBLE);
    }

    private String getCurrentUserId() {
        return firebaseAuth.getCurrentUser() != null ? 
            firebaseAuth.getCurrentUser().getUid() : null;
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to activity
        if (contentLayout.getVisibility() == View.VISIBLE) {
            loadAIInsights();
        }
    }
}
