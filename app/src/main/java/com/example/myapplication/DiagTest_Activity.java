package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.myapplication.Models.Test_Questions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagTest_Activity extends AppCompatActivity {
    private TextView questionTextView,inputWeight,inputHeight;
    private RadioGroup optionsRadioGroup;
    private LinearLayout optionsLinearLayout;
    private ProgressBar progressBar;
    private int totalQuestions;
    private List<Test_Questions> questions;
    private Map<String, Object> userAnswers;
    private int currentQuestionIndex = 0;
    private static final int pageWidth = 595;
    private static final int pageHeight = 842;
    private static final int leftMargin = 50;
    private static final float lineSpacing = 12f;
    private final FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference questionsCollection = db.collection("Diagnostic_test");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostic_test);

        questionTextView = findViewById(R.id.questionTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        optionsLinearLayout = findViewById(R.id.optionsLinearLayout);
        inputWeight = findViewById(R.id.inputWeight);
        inputHeight = findViewById(R.id.inputHeight);
        Button nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);
        ImageButton exitButton = findViewById(R.id.exitButton);
        ImageButton backButton = findViewById(R.id.backButton);

        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(DiagTest_Activity.this, HomeActivity.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            // If there is a previous question, show it
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                showQuestion(currentQuestionIndex);
            }
            // Update the progress bar
            updateProgressBar();
        });

        questions = new ArrayList<>();
        userAnswers = new HashMap<>();

        retrieveQuestionsFromFirestore();

        // Set click listener for the Next button
        nextButton.setOnClickListener(v -> {
            // Save user's answer
            saveUserAnswer();

            // Move to the next question
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                showQuestion(currentQuestionIndex);
            } else {
                // All questions answered, generate report
                generateReport();
            }
            // Update the progress bar
            updateProgressBar();
        });
    }
    @Override
    public void onBackPressed() {
        // If there is a previous question, show it
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            showQuestion(currentQuestionIndex);
        } else {
            // If this is the first question, let the default back behavior handle it
            super.onBackPressed();
        }
        // Update the progress bar
        updateProgressBar();
    }

    private void retrieveQuestionsFromFirestore() {
        questionsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                totalQuestions = task.getResult().size();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String text = document.getString("text");
                    String type = document.getString("type");
                    List<String> options = (List<String>) document.get("options");
                    Map<String, Long> optionScores = (Map<String, Long>) document.get("optionScores");

                    Test_Questions question = new Test_Questions(text, options,optionScores,type);
                    questions.add(question);
                }
                // Update the progress bar after retrieving questions
                updateProgressBar();

                // Display the first question
                showQuestion(currentQuestionIndex);
            }  // Handle Firestore retrieval error

        });
    }

    private void showQuestion(int questionIndex) {
        Test_Questions question = questions.get(questionIndex);
        questionTextView.setText(getResourceString(question.getText()));

        optionsRadioGroup.setVisibility(View.GONE);
        optionsLinearLayout.setVisibility(View.GONE);
        inputWeight.setVisibility(View.GONE);
        inputHeight.setVisibility(View.GONE);

        String questionType = question.getType();
        switch (questionType) {
            case "single-answer":
                optionsRadioGroup.setVisibility(View.VISIBLE);
                optionsRadioGroup.removeAllViews();

                for (String option : question.getOptions()) {
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(getResourceString(option));
                    optionsRadioGroup.addView(radioButton);
                }
                break;
            case "input-answer":
                inputWeight.setVisibility(View.VISIBLE);
                inputHeight.setVisibility(View.VISIBLE);
                break;
            case "multi-answer":
                optionsLinearLayout.setVisibility(View.VISIBLE);
                optionsLinearLayout.removeAllViews();

                for (String option : question.getOptions()) {
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(getResourceString(option));
                    optionsLinearLayout.addView(checkBox);
                }
                break;
        }
    }

    private void saveUserAnswer() {
        if (optionsRadioGroup.getVisibility() == View.VISIBLE) {
            int checkedRadioButtonId = optionsRadioGroup.getCheckedRadioButtonId();
            if (checkedRadioButtonId != -1) {
                RadioButton selectedRadioButton = findViewById(checkedRadioButtonId);
                if (selectedRadioButton != null) {
                    String answer = selectedRadioButton.getText().toString();
                    userAnswers.put(questions.get(currentQuestionIndex).getText(), answer);
                } else {
                    // Handle the situation when no radio button is selected
                    userAnswers.put(questions.get(currentQuestionIndex).getText(), "No answer selected");
                }
            }else {
                // No radio button is selected, handle the situation accordingly
                // In this example, you can add a default value, for instance:
                userAnswers.put(questions.get(currentQuestionIndex).getText(), "No answer selected");
            }
        } else if (optionsLinearLayout.getVisibility() == View.VISIBLE) {
            List<String> selectedOptions = new ArrayList<>();

            for (int i = 0; i < optionsLinearLayout.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) optionsLinearLayout.getChildAt(i);
                if (checkBox.isChecked()) {
                    selectedOptions.add(checkBox.getText().toString());
                }
            }
            userAnswers.put(questions.get(currentQuestionIndex).getText(), selectedOptions);

        }else if (inputWeight.getVisibility() == View.VISIBLE && inputHeight.getVisibility() == View.VISIBLE) {
            String weightString = inputWeight.getText().toString().trim();
            String heightString = inputHeight.getText().toString().trim();

            if (!weightString.isEmpty() && !heightString.isEmpty()) {
                double weight = Double.parseDouble(weightString);
                double height = Double.parseDouble(heightString);

                // Calculate BMI
                double bmi = weight / Math.pow((height * 0.01), 2);

                // Assign score based on BMI
                int score;
                if (bmi <= 18.5 ){
                    score = 0 ;// Assign score of 0 for low BMI
                } else {
                    score = 1; // Assign score of 1 for normal BMI
                }
                userAnswers.put("Body mass index : calculate your BMI ", bmi);
                userAnswers.put("BMI Score", score);
            }
        }
    }

    private void generateReport() {
        int totalScore = 0;
        String riskLevel;

        // Generate report with total score and user answers
        StringBuilder reportBuilder = new StringBuilder();
        for (Test_Questions question : questions) {
            Object userAnswer = userAnswers.get(question.getText());
            reportBuilder.append("Question: ").append(question.getText()).append("\n");
            reportBuilder.append(getString(R.string.UserAnswer)).append(userAnswer).append("\n\n");

            // Calculate score based on the answer
            if (userAnswer != null) {
                if (userAnswer instanceof String) {
                    String selectedOption = (String) userAnswer;
                    if (question.getOptionScores().containsKey(selectedOption)) {
                        totalScore += question.getOptionScores().get(selectedOption);
                    }
                } else if (userAnswer instanceof List<?>) {
                    List<String> selectedOptions = (List<String>) userAnswer;
                    for (String option : selectedOptions) {
                        if (option != null && question.getOptionScores().containsKey(option)) {
                            totalScore += question.getOptionScores().get(option);
                        }
                    }
                } else if (userAnswer instanceof Double) {
                    if (question.getText().equals("Body mass index : calculate your BMI ")) {
                        int bmiAnswer = (int) userAnswers.get("BMI Score");
                        Log.d("DiagTest_Activity", "BMI Score: " + bmiAnswer); // Add this line to log the value
                        // Get the score assigned to the BMI answer
                        totalScore += bmiAnswer;
                    }
                }
            }

            reportBuilder.append("\n");
        }
        if (totalScore >= 0 && totalScore <= 12) {
            riskLevel = "Low";
        } else if (totalScore >= 13 && totalScore <= 25) {
            riskLevel ="Medium";
        } else {
            riskLevel = "High";
        }

        //update user's risk level
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getUid();
        updateUserRiskLevelInFirestore(userId, riskLevel);

        String report = reportBuilder.toString();

        // Inflate the layout for the score and report
        View reportLayout = getLayoutInflater().inflate(R.layout.activity_diagnostic_result, null);

        // Find the TextViews within the inflated layout
        TextView scoreTextView = reportLayout.findViewById(R.id.scoreTextView);
        TextView reportTextView = reportLayout.findViewById(R.id.reportTextView);
        Button openReportButton = reportLayout.findViewById(R.id.openReportButton);
        ImageView backBtn = reportLayout.findViewById(R.id.leftIcon);

        // Set the score and report text
        scoreTextView.setText(getString(R.string.RiskLevel)+ riskLevel);
        reportTextView.setText(report);

        // Generate the PDF report
        generatePdfReport(riskLevel,userId);

        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(DiagTest_Activity.this, HomeActivity.class);
            startActivity(intent);
        });
        openReportButton.setOnClickListener(v -> {
            // Request storage permission before opening the PDF
            Dexter.withContext(DiagTest_Activity.this)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            // Open the generated PDF report
                            String filePath = getExternalFilesDir(null) + "/report.pdf";
                            File file = new File(filePath);
                            if (file.exists()) {
                                Uri uri = FileProvider.getUriForFile(
                                        DiagTest_Activity.this,
                                        getPackageName() + ".provider",
                                        file
                                );
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, "application/pdf");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(intent);
                            } else {
                                // Handle the case when the PDF file does not exist
                                Toast.makeText(DiagTest_Activity.this, "PDF file not found!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            // Handle permission denied
                            Toast.makeText(DiagTest_Activity.this, "Storage permission denied!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            // Handle permission rationale
                            token.continuePermissionRequest();
                        }
                    }).check();
        });

        // Display the inflated layout containing the score and report
        setContentView(reportLayout);
    }
    private void updateProgressBar() {
        int progress = (currentQuestionIndex + 1) * 100 / totalQuestions;
        progressBar.setProgress(progress);
    }
    private void generatePdfReport(String riskLevel, String userId) {
        // Create a new PdfDocument instance
        PdfDocument pdfDocument = new PdfDocument();

        // Get a reference to the user's document in the "Users" collection
        DocumentReference userRef = db.collection("Users").document(userId);

        // Retrieve user data from Firestore
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    // User document exists, retrieve data
                    String name = documentSnapshot.getString("name");
                    String email = documentSnapshot.getString("email");
                    String birthday = documentSnapshot.getString("birthday");
                    String painAverage = documentSnapshot.getString("painAverage");

                    String nameLabel = getString(R.string.name_label);
                    String emailLabel = getString(R.string.email_label);
                    String birthdayLabel = getString(R.string.birthday_label);
                    String painAverageLabel = getString(R.string.pain_average_label);

                    // Create a PageInfo for the PDF
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

                    try {
                        // Start a new page
                        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                        Canvas canvas = page.getCanvas();
                        Paint paint = new Paint();
                        Paint titlePaint = new Paint();
                        Paint scorePaint = new Paint();
                        Paint reportPaint = new Paint();

                        // Calculate the center of the page for the title
                        float centerX = canvas.getWidth() / 2;
                        float titleY = 80;
                        float titleWidth = titlePaint.measureText("Diagnostic Test Report");

                        paint.setColor(Color.BLACK);
                        paint.setTextSize(15);
                        paint.setTypeface(Typeface.DEFAULT);

                        titlePaint.setColor(Color.BLACK);
                        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        titlePaint.setTextSize(18);

                        scorePaint.setTextSize(15);
                        scorePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        reportPaint.setTextSize(12f);

                        // Add main title "Diagnostic Test Report"
                        String diagnosticReportTitle = getString(R.string.diagnostic_report_title);
                        canvas.drawText(diagnosticReportTitle, centerX - titleWidth / 2, titleY, titlePaint);
                        // Add user information
                        float userInfoY = titleY + 50;
                        canvas.drawText(nameLabel + " " + name, 50, userInfoY, paint);
                        canvas.drawText(emailLabel + " " + email, 50, userInfoY + 30, paint);
                        canvas.drawText(birthdayLabel + " " + birthday, 50, userInfoY + 60, paint);
                        canvas.drawText(painAverageLabel + " " + painAverage, 50, userInfoY + 90, paint);

                        // Draw a divider line under the user information
                        float dividerY = userInfoY + 120;
                        canvas.drawLine(50, dividerY, page.getInfo().getPageWidth() - 50, dividerY, paint);

                        canvas.drawText("Risk Level : " + riskLevel, 50, dividerY + 30 , scorePaint);

                        canvas.drawText("Test answers : ", 50, dividerY + 60 , paint);

                        // Draw the first 8 questions
                        drawContent(canvas, questions, 0, 7, reportPaint,340);

                        // Finish the first page
                        pdfDocument.finishPage(page);

                        // Create a PageInfo for the second page of the PDF
                        PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create();
                        PdfDocument.Page page2 = pdfDocument.startPage(pageInfo2);
                        Canvas canvas2 = page2.getCanvas();

                        // Draw the rest of the questions (from 9 to the end)
                        drawContent(canvas2, questions, 8, questions.size() - 1, reportPaint,80);

                        // Finish the second page
                        pdfDocument.finishPage(page2);

                        // Define the output file path
                        String filePath = getExternalFilesDir(null) + "/report.pdf";

                        // Create a file output stream
                        FileOutputStream outputStream = new FileOutputStream(filePath);

                        // Write the PDF document to the output stream
                        pdfDocument.writeTo(outputStream);

                        // Close the output stream
                        outputStream.close();

                        // Close the PdfDocument
                        pdfDocument.close();

                        // TODO: Add code to handle the generated PDF report (e.g., send it via email or display a notification)
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle the case when the user document does not exist
                    Log.e("DiagTest_Activity", "User document not found.");
                }
            } else {
                // Handle the case when fetching user information fails
                Log.e("DiagTest_Activity", "Error fetching user data: " + task.getException());
            }
        });
    }

    private void updateUserRiskLevelInFirestore(String userId, String riskLevel) {
        // Update the "riskLevel" field in the user's document in Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("riskLevel", riskLevel);

        db.collection("Users").document(userId)
                .update(userData)
                .addOnSuccessListener(aVoid -> {
                    // Success, risk level updated in the user's document
                })
                .addOnFailureListener(e -> {
                    // Failed to update the risk level
                    // Handle the error here, show a toast message, etc.
                });
    }
    // Add a new method to draw content for a single page
    private void drawContent(Canvas canvas, List<Test_Questions> questions, int startIndex, int endIndex, Paint reportPaint, int height) {
        float reportY = height;
        for (int i = startIndex; i <= endIndex; i++) {
            Test_Questions question = questions.get(i);
            Object userAnswer = userAnswers.get(question.getText());

            // Draw the question
            canvas.drawText("Question: " + getResourceString(question.getText()), leftMargin, reportY, reportPaint);
            reportY += reportPaint.descent() - reportPaint.ascent();

            // Draw the user's answer
            String userAnswerText = "";

            if (userAnswer != null) {
                if (userAnswer instanceof String) {
                    userAnswerText = (String) userAnswer;
                } else if (userAnswer instanceof List<?>) {
                    List<String> selectedOptions = (List<String>) userAnswer;
                    userAnswerText = TextUtils.join(", ", selectedOptions);
                } else if (userAnswer instanceof Double) {
                    userAnswerText = String.valueOf(userAnswer);
                }
            }

            canvas.drawText("User's Answer: " + userAnswerText, leftMargin, reportY, reportPaint);
            reportY += reportPaint.descent() - reportPaint.ascent();

            // Draw a separator line between questions
            reportY += lineSpacing + 10;
        }
    }
    private String getResourceString(String resourceName) {
        int resId = getResources().getIdentifier(resourceName, "string", getPackageName());
        if (resId != 0) {
            return getString(resId);
        } else {
            // Handle the case when the resource is not found
            Log.e("DiagTest_Activity", "Resource not found: " + resourceName);
            return "Resource not found";
        }
    }
}