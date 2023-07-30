package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.Models.Quiz_question;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Quiz_Activity extends AppCompatActivity {

    private TextView questions;
    private TextView question;

    private AppCompatButton option1, option2, option3, option4;

    private AppCompatButton nextBtn;

    private Timer quizTimer;

    private int totalTimeInMins = 1;
    private boolean quizCompleted = false;

    private int seconds = 0;

    private List<Quiz_question> questionsList;

    private int currentQuestionPosition = 0;

    private String selectedOptionByUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        final ImageView backBtn = findViewById(R.id.backBtn);
        final TextView timer = findViewById(R.id.timer);
        final TextView selectedTopicName =findViewById(R.id.topicName);

        questions = findViewById(R.id.questions);
        question = findViewById(R.id.question);

        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        nextBtn = findViewById(R.id.nextBtn);

        final String getSelectedTopicName = getIntent().getStringExtra("selectedTopic");

        selectedTopicName.setText(getSelectedTopicName);

        // Load questions from Firestore for the selected topic
        loadQuestionsFromFirestore(getSelectedTopicName);

        startTimer(timer);

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedOptionByUser.isEmpty())
                {
                    selectedOptionByUser = option1.getText().toString();

                    option1.setBackgroundResource(R.drawable.round_back_red10);
                    option1.setTextColor(Color.WHITE);

                    revealAnswer();

                    questionsList.get(currentQuestionPosition).setUserSelectedAnswer(selectedOptionByUser);
                }
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedOptionByUser.isEmpty())
                {
                    selectedOptionByUser = option2.getText().toString();

                    option2.setBackgroundResource(R.drawable.round_back_red10);
                    option2.setTextColor(Color.WHITE);

                    revealAnswer();

                    questionsList.get(currentQuestionPosition).setUserSelectedAnswer(selectedOptionByUser);
                }

            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedOptionByUser.isEmpty())
                {
                    selectedOptionByUser = option3.getText().toString();

                    option3.setBackgroundResource(R.drawable.round_back_red10);
                    option3.setTextColor(Color.WHITE);

                    revealAnswer();

                    questionsList.get(currentQuestionPosition).setUserSelectedAnswer(selectedOptionByUser);
                }

            }
        });

        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedOptionByUser.isEmpty())
                {
                    selectedOptionByUser = option4.getText().toString();

                    option4.setBackgroundResource(R.drawable.round_back_red10);
                    option4.setTextColor(Color.WHITE);

                    revealAnswer();

                    questionsList.get(currentQuestionPosition).setUserSelectedAnswer(selectedOptionByUser);
                }

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedOptionByUser.isEmpty())
                {
                    Toast.makeText(Quiz_Activity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    changeNextQuestion();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quizTimer.purge();
                quizTimer.cancel();

                startActivity(new Intent(Quiz_Activity.this, Quiz_main.class));
                finish();
            }
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
            option1.setTextColor(Color.parseColor("#1F6BB8"));

            option2.setBackgroundResource(R.drawable.round_back_white_stroke2_10);
            option2.setTextColor(Color.parseColor("#1F6BB8"));

            option3.setBackgroundResource(R.drawable.round_back_white_stroke2_10);
            option3.setTextColor(Color.parseColor("#1F6BB8"));

            option4.setBackgroundResource(R.drawable.round_back_white_stroke2_10);
            option4.setTextColor(Color.parseColor("#1F6BB8"));

            questions.setText((currentQuestionPosition+1)+"/"+questionsList.size());
            question.setText(questionsList.get(currentQuestionPosition).getQst());
            option1.setText(questionsList.get(currentQuestionPosition).getOpt1());
            option2.setText(questionsList.get(currentQuestionPosition).getOpt2());
            option3.setText(questionsList.get(currentQuestionPosition).getOpt3());
            option4.setText(questionsList.get(currentQuestionPosition).getOpt4());
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

    private void startTimer(TextView timerTextView)
    {
        quizTimer = new Timer();
        quizTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (seconds == 0 && totalTimeInMins == 0) {
                    // Timer finished, handle the end of the quiz here
                    quizTimer.purge();
                    quizTimer.cancel();
                    quizCompleted = true;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timerTextView.setText("00:00");

                            // Display a toast indicating time's up
                            Toast.makeText(Quiz_Activity.this, "Time Over", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Quiz_Activity.this, QuizResults.class);
                            intent.putExtra("correct", getCorrectAnswers());
                            intent.putExtra("incorrect", getInCorrectAnswers());
                            startActivity(intent);

                            finish();
                        }
                    });
                } else {
                    // Update the timer display
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String finalMinutes = String.valueOf(totalTimeInMins);
                            String finalSeconds = String.valueOf(seconds);

                            if (finalMinutes.length() == 1) {
                                finalMinutes = "0" + finalMinutes;
                            }
                            if (finalSeconds.length() == 1) {
                                finalSeconds = "0" + finalSeconds;
                            }

                            timerTextView.setText(finalMinutes + ":" + finalSeconds);
                        }
                    });

                    // Decrement the timer
                    if (seconds == 0) {
                        totalTimeInMins--;
                        seconds = 59;
                    } else {
                        seconds--;
                    }
                }
            }
        }, 1000, 1000);
    }

    private int getCorrectAnswers()
    {
        int correctAnswers = 0;

        if (questionsList != null) {
            for (int i = 0; i < questionsList.size(); i++) {
                final String getUserSelectedAnswer = questionsList.get(i).getUserSelectedAnswer();
                final String getAnswer = questionsList.get(i).getAnswer();

                if (getUserSelectedAnswer != null && getUserSelectedAnswer.equals(getAnswer)) {
                    correctAnswers++;
                }
            }
        }

        return correctAnswers;
    }

    private int getInCorrectAnswers()
    {
        int incorrectAnswers = 0;
        if (questionsList != null) {
            for (int i = 0; i < questionsList.size(); i++) {
                final String getUserSelectedAnswer = questionsList.get(i).getUserSelectedAnswer();
                final String getAnswer = questionsList.get(i).getAnswer();

                if (getUserSelectedAnswer == null || !getUserSelectedAnswer.equals(getAnswer)) {
                    incorrectAnswers++;
                }
            }
        }

        return incorrectAnswers;
    }

    @Override
    public void onBackPressed()
    {
        quizTimer.purge();
        quizTimer.cancel();

        startActivity(new Intent(Quiz_Activity.this, Quiz_main.class));
        finish();
    }

    private void revealAnswer()
    {
        final String getAnswer = questionsList.get(currentQuestionPosition).getAnswer();

        if (option1.getText().toString().equals(getAnswer))
        {
            option1.setBackgroundResource(R.drawable.round_back_green10);
            option1.setTextColor(Color.WHITE);
        }
        else if (option2.getText().toString().equals(getAnswer)) {
            option2.setBackgroundResource(R.drawable.round_back_green10);
            option2.setTextColor(Color.WHITE);
        }
        else if (option3.getText().toString().equals(getAnswer)) {
            option3.setBackgroundResource(R.drawable.round_back_green10);
            option3.setTextColor(Color.WHITE);
        }
        else if (option4.getText().toString().equals(getAnswer)) {
            option4.setBackgroundResource(R.drawable.round_back_green10);
            option4.setTextColor(Color.WHITE);
        }
    }

    private void loadQuestionsFromFirestore(String selectedTopic) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference topicsCollectionRef = firestore.collection("Quiz");
        topicsCollectionRef.document("Topics").collection(selectedTopic)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            questionsList = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                // Map the document snapshot to your Quiz_question model class
                                Quiz_question question = documentSnapshot.toObject(Quiz_question.class);
                                questionsList.add(question);
                            }
                            // Here, you have the list of questions for the selected topic
                            // Set the initial question and options in the UI
                            setInitialQuestion();
                        } else {
                            // Handle case when there are no questions for the selected topic
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors that occurred while fetching the questions
                    }
                });
    }

    private void setInitialQuestion() {
        if (questionsList != null && !questionsList.isEmpty()) {
            questions.setText((currentQuestionPosition + 1) + "/" + questionsList.size());
            question.setText(questionsList.get(currentQuestionPosition).getQst());
            option1.setText(questionsList.get(currentQuestionPosition).getOpt1());
            option2.setText(questionsList.get(currentQuestionPosition).getOpt2());
            option3.setText(questionsList.get(currentQuestionPosition).getOpt3());
            option4.setText(questionsList.get(currentQuestionPosition).getOpt4());
        }
    }
}
