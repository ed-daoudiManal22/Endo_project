package com.spmenais.paincare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.spmenais.paincare.Models.Quiz_question;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Quiz_Activity extends AppCompatActivity {
    private TextView questions;
    private TextView question;
    private TextView explanationText;
    private AppCompatButton option1, option2, option3, option4;
    private AppCompatButton nextBtn;
    private List<Quiz_question> questionsList;
    private int currentQuestionPosition = 0;
    private String selectedOptionByUser = "";
    private static final String OPTION_COLOR = "#1F6BB8";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        final ImageView backBtn = findViewById(R.id.backBtn);
        final TextView selectedTopicName =findViewById(R.id.topicName);

        questions = findViewById(R.id.questions);
        question = findViewById(R.id.question);

        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        explanationText = findViewById(R.id.explanationText);

        nextBtn = findViewById(R.id.nextBtn);

        final String getSelectedTopicName = getIntent().getStringExtra("selectedTopic");

        selectedTopicName.setText(getSelectedTopicName);

        // Load questions from Firestore for the selected topic
        loadQuestionsFromFirestore(getSelectedTopicName);

        option1.setOnClickListener(view -> {
            if (selectedOptionByUser.isEmpty())
            {
                selectedOptionByUser = option1.getText().toString();

                option1.setBackgroundResource(R.drawable.round_back_red10);
                option1.setTextColor(Color.WHITE);

                revealAnswer();

                questionsList.get(currentQuestionPosition).setUserSelectedAnswer(selectedOptionByUser);
            }
        });

        option2.setOnClickListener(view -> {
            if (selectedOptionByUser.isEmpty())
            {
                selectedOptionByUser = option2.getText().toString();

                option2.setBackgroundResource(R.drawable.round_back_red10);
                option2.setTextColor(Color.WHITE);

                revealAnswer();

                questionsList.get(currentQuestionPosition).setUserSelectedAnswer(selectedOptionByUser);
            }

        });

        option3.setOnClickListener(view -> {
            if (selectedOptionByUser.isEmpty())
            {
                selectedOptionByUser = option3.getText().toString();

                option3.setBackgroundResource(R.drawable.round_back_red10);
                option3.setTextColor(Color.WHITE);

                revealAnswer();

                questionsList.get(currentQuestionPosition).setUserSelectedAnswer(selectedOptionByUser);
            }

        });

        option4.setOnClickListener(view -> {
            if (selectedOptionByUser.isEmpty())
            {
                selectedOptionByUser = option4.getText().toString();

                option4.setBackgroundResource(R.drawable.round_back_red10);
                option4.setTextColor(Color.WHITE);

                revealAnswer();

                questionsList.get(currentQuestionPosition).setUserSelectedAnswer(selectedOptionByUser);
            }

        });

        nextBtn.setOnClickListener(view -> {

            if (selectedOptionByUser.isEmpty())
            {
                Toast.makeText(Quiz_Activity.this, "Please select an option", Toast.LENGTH_SHORT).show();
            }

            else
            {
                changeNextQuestion();
            }
        });

        backBtn.setOnClickListener(view -> {

            startActivity(new Intent(Quiz_Activity.this, Quiz_main.class));
            finish();
        });
    }

    private void changeNextQuestion()
    {
        currentQuestionPosition++;

        if(( currentQuestionPosition+1 ) == questionsList.size())
        {
            nextBtn.setText("Submit Quiz");
        }

        if (currentQuestionPosition < questionsList.size())
        {
            selectedOptionByUser = "";

            option1.setBackgroundResource(R.drawable.round_back_white_stroke2_10);
            option1.setTextColor(Color.parseColor(OPTION_COLOR));

            option2.setBackgroundResource(R.drawable.round_back_white_stroke2_10);
            option2.setTextColor(Color.parseColor(OPTION_COLOR));

            option3.setBackgroundResource(R.drawable.round_back_white_stroke2_10);
            option3.setTextColor(Color.parseColor(OPTION_COLOR));

            option4.setBackgroundResource(R.drawable.round_back_white_stroke2_10);
            option4.setTextColor(Color.parseColor(OPTION_COLOR));

            questions.setText((currentQuestionPosition+1)+"/"+questionsList.size());
            question.setText(getResourceString(questionsList.get(currentQuestionPosition).getQst()));
            option1.setText(getResourceString(questionsList.get(currentQuestionPosition).getOpt1()));
            option2.setText(getResourceString(questionsList.get(currentQuestionPosition).getOpt2()));
            option3.setText(getResourceString(questionsList.get(currentQuestionPosition).getOpt3()));
            option4.setText(getResourceString(questionsList.get(currentQuestionPosition).getOpt4()));

            // Hide the explanationText
            explanationText.setVisibility(View.GONE);
        }

        else
        {
            Intent intent = new Intent(Quiz_Activity.this, QuizResults.class);
            intent.putExtra("correct", getCorrectAnswers());
            intent.putExtra("incorrect", getInCorrectAnswers());
            startActivity(intent);

            finish();
        }
    }
    private int getCorrectAnswers() {
        int correctAnswers = 0;

        if (questionsList != null) {
            for (int i = 0; i < questionsList.size(); i++) {
                final String getUserSelectedAnswer = questionsList.get(i).getUserSelectedAnswer();
                final List<String> getAnswers = questionsList.get(i).getAnswers();

                if (getUserSelectedAnswer != null && checkIfAnswerIsCorrect(getAnswers, getUserSelectedAnswer)) {
                    correctAnswers++;
                }
            }
        }

        return correctAnswers;
    }

    private int getInCorrectAnswers() {
        int incorrectAnswers = 0;

        if (questionsList != null) {
            for (int i = 0; i < questionsList.size(); i++) {
                final String getUserSelectedAnswer = questionsList.get(i).getUserSelectedAnswer();
                final List<String> getAnswers = questionsList.get(i).getAnswers();

                if (getUserSelectedAnswer == null || !checkIfAnswerIsCorrect(getAnswers, getUserSelectedAnswer)) {
                    incorrectAnswers++;
                }
            }
        }

        return incorrectAnswers;
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(Quiz_Activity.this, Quiz_main.class));
        finish();
    }

    private void revealAnswer()
    {
        final List<String> getAnswers = questionsList.get(currentQuestionPosition).getAnswers();
        final String explanation = questionsList.get(currentQuestionPosition).getJustif();

        if (checkIfAnswerIsCorrect(getAnswers, option1.getText().toString()))
        {
            option1.setBackgroundResource(R.drawable.round_back_green10);
            option1.setTextColor(Color.WHITE);
        }
        else if (checkIfAnswerIsCorrect(getAnswers, option2.getText().toString())) {
            option2.setBackgroundResource(R.drawable.round_back_green10);
            option2.setTextColor(Color.WHITE);
        }
        else if (checkIfAnswerIsCorrect(getAnswers, option3.getText().toString())) {
            option3.setBackgroundResource(R.drawable.round_back_green10);
            option3.setTextColor(Color.WHITE);
        }
        else if (checkIfAnswerIsCorrect(getAnswers, option4.getText().toString())) {
            option4.setBackgroundResource(R.drawable.round_back_green10);
            option4.setTextColor(Color.WHITE);
        }
        // Show explanation
        explanationText.setVisibility(View.VISIBLE);
        explanationText.setText(getResourceString(explanation));
    }

    private boolean checkIfAnswerIsCorrect(List<String> correctAnswers, String optionText) {
        // Compare the selected answer with the list of correct answers
        for (String answer : correctAnswers) {
            if (optionText.equals(answer)) {
                return true;
            }
        }
        return false;
    }

    private void loadQuestionsFromFirestore(String selectedTopic) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference topicsCollectionRef = firestore.collection("Quiz");
        topicsCollectionRef.document("Topics").collection(selectedTopic)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        questionsList = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Map the document snapshot to your Quiz_question model class
                            Quiz_question one_question = documentSnapshot.toObject(Quiz_question.class);
                            questionsList.add(one_question);
                        }
                        setInitialQuestion();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occurred while fetching the questions
                });
    }

    private void setInitialQuestion() {
        if (questionsList != null && !questionsList.isEmpty()) {
            questions.setText((currentQuestionPosition + 1) + "/" + questionsList.size());
            question.setText(getResourceString(questionsList.get(currentQuestionPosition).getQst()));
            option1.setText(getResourceString(questionsList.get(currentQuestionPosition).getOpt1()));
            option2.setText(getResourceString(questionsList.get(currentQuestionPosition).getOpt2()));
            option3.setText(getResourceString(questionsList.get(currentQuestionPosition).getOpt3()));
            option4.setText(getResourceString(questionsList.get(currentQuestionPosition).getOpt4()));
        }
    }
    private String getResourceString(String resourceName) {
        int resId = getResources().getIdentifier(resourceName, "string", getPackageName());
        if (resId != 0) {
            return getString(resId);
        } else {
            // Handle the case when the resource is not found
            Log.e("Quiz_Activity", "Resource not found: " + resourceName);
            return "Resource not found";
        }
    }
}
