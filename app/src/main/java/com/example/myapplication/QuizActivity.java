package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class QuizActivity extends Activity {

    private String selectedTopicName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

        final LinearLayout topic1 = findViewById(R.id.topic1Layout);
        final LinearLayout topic2 = findViewById(R.id.topic2Layout);
        final LinearLayout topic3 = findViewById(R.id.topic3Layout);
        final LinearLayout topic4 = findViewById(R.id.topic4Layout);

        final Button startBtn = findViewById(R.id.startQuizBtn);

        topic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedTopicName = "topic1";

                topic1.setBackgroundResource(R.drawable.round_back_white_stroke10);

                topic2.setBackgroundResource(R.drawable.round_back_white10);
                topic3.setBackgroundResource(R.drawable.round_back_white10);
                topic4.setBackgroundResource(R.drawable.round_back_white10);

            }
        });

        topic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedTopicName = "topic2";

                topic2.setBackgroundResource(R.drawable.round_back_white_stroke10);

                topic1.setBackgroundResource(R.drawable.round_back_white10);
                topic3.setBackgroundResource(R.drawable.round_back_white10);
                topic4.setBackgroundResource(R.drawable.round_back_white10);

            }
        });

        topic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedTopicName = "topic3";

                topic3.setBackgroundResource(R.drawable.round_back_white_stroke10);

                topic1.setBackgroundResource(R.drawable.round_back_white10);
                topic2.setBackgroundResource(R.drawable.round_back_white10);
                topic4.setBackgroundResource(R.drawable.round_back_white10);

            }
        });

        topic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedTopicName = "topic4";

                topic4.setBackgroundResource(R.drawable.round_back_white_stroke10);

                topic1.setBackgroundResource(R.drawable.round_back_white10);
                topic2.setBackgroundResource(R.drawable.round_back_white10);
                topic3.setBackgroundResource(R.drawable.round_back_white10);

            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedTopicName.isEmpty())
                {
                    Toast.makeText(QuizActivity.this, " Please select a topic ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(QuizActivity.this, StartQuizActivity.class);
                    intent.putExtra("selectedTopic", selectedTopicName);
                    startActivity(intent);
                }
            }
        });


    }
}
