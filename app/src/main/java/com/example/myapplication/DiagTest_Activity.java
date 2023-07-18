package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Models.Test_Questions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagTest_Activity extends AppCompatActivity {
    private TextView questionTextView,inputWeight,inputHeight;
    private RadioGroup optionsRadioGroup;
    private LinearLayout optionsLinearLayout;
    private Button nextButton;
    private ProgressBar progressBar;
    private int totalQuestions;
    private List<Test_Questions> questions;
    private Map<String, Object> userAnswers;
    private int currentQuestionIndex = 0;

    // Initialize Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference questionsCollection = db.collection("Diagnostic_test");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostic_test);

        questionTextView = findViewById(R.id.questionTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        optionsLinearLayout = findViewById(R.id.optionsLinearLayout);
        inputWeight = findViewById(R.id.inputWeight);
        inputHeight = findViewById(R.id.inputHeight);
        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);


        questions = new ArrayList<>();
        userAnswers = new HashMap<>();

        retrieveQuestionsFromFirestore();

        // Set click listener for the Next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
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
            } else {
                // Handle Firestore retrieval error
            }
        });
    }

    private void showQuestion(int questionIndex) {
        Test_Questions question = questions.get(questionIndex);
        questionTextView.setText(question.getText());

        optionsRadioGroup.setVisibility(View.GONE);
        optionsLinearLayout.setVisibility(View.GONE);
        inputWeight.setVisibility(View.GONE);
        inputHeight.setVisibility(View.GONE);

        String questionType = question.getType();
        if (questionType.equals("single-answer")) {
            optionsRadioGroup.setVisibility(View.VISIBLE);
            optionsRadioGroup.removeAllViews();

            for (String option : question.getOptions()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(option);
                optionsRadioGroup.addView(radioButton);
            }
        } else if (questionType.equals("input-answer")) {
            inputWeight.setVisibility(View.VISIBLE);
            inputHeight.setVisibility(View.VISIBLE);
        } else if (questionType.equals("multi-answer")) {
            optionsLinearLayout.setVisibility(View.VISIBLE);
            optionsLinearLayout.removeAllViews();

            for (String option : question.getOptions()) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(option);
                optionsLinearLayout.addView(checkBox);
            }
        }
    }

    private void saveUserAnswer() {
        if (optionsRadioGroup.getVisibility() == View.VISIBLE) {
            int checkedRadioButtonId = optionsRadioGroup.getCheckedRadioButtonId();
            if (checkedRadioButtonId != -1) {
                RadioButton selectedRadioButton = findViewById(checkedRadioButtonId);
                String answer = selectedRadioButton.getText().toString();
                userAnswers.put(questions.get(currentQuestionIndex).getText(), answer);
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

        // Generate report with total score and user answers
        StringBuilder reportBuilder = new StringBuilder();
        for (Test_Questions question : questions) {
            Object userAnswer = userAnswers.get(question.getText());
            reportBuilder.append("Question: ").append(question.getText()).append("\n");
            reportBuilder.append("User's Answer: ").append(userAnswer).append("\n\n");

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
                        if (question.getOptionScores().containsKey(option)) {
                            totalScore += question.getOptionScores().get(option);
                        }
                    }
                }else if (userAnswer instanceof Double) {
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
        String report = reportBuilder.toString();

        // Inflate the layout for the score and report
        View reportLayout = getLayoutInflater().inflate(R.layout.activity_diagnostic_result, null);

        // Find the TextViews within the inflated layout
        TextView scoreTextView = reportLayout.findViewById(R.id.scoreTextView);
        TextView reportTextView = reportLayout.findViewById(R.id.reportTextView);

        // Set the score and report text
        scoreTextView.setText("Score: " + totalScore);
        reportTextView.setText(report);

        // Display the inflated layout containing the score and report
        setContentView(reportLayout);
        // Display the report or save it to Firestore
        // You can implement the desired behavior here
        // For example, you can display the total score in a TextView
        //TextView scoreTextView = findViewById(R.id.scoreTextView);
        //scoreTextView.setText("Total Score: " + totalScore);

        // You can also save the report and score to Firestore if needed
        // For that, you can create a new document in the Firestore collection and set the report and score fields.
    }
    private void updateProgressBar() {
        int progress = (currentQuestionIndex + 1) * 100 / totalQuestions;
        progressBar.setProgress(progress);
    }
}
