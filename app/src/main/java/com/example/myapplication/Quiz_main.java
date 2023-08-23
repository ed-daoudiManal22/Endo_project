package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Quiz_main extends AppCompatActivity {
    private String selectedTopicName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);

        final LinearLayout Endometriosis = findViewById(R.id.Endometriosis);
        final LinearLayout Fertility_Contraception = findViewById(R.id.Fertility_and_Contraception);
        final LinearLayout Intimate_Health = findViewById(R.id.Intimate_Health);
        final LinearLayout Menstrual_Cycle = findViewById(R.id.Menstrual_Cycle);

        final Button startBtn = findViewById(R.id.startQuizBtn);

        Endometriosis.setOnClickListener(view -> {

            selectedTopicName = "Endometriosis";

            Endometriosis.setBackgroundResource(R.drawable.round_back_white_stroke10);

            Fertility_Contraception.setBackgroundResource(R.drawable.round_back_white10);
            Intimate_Health.setBackgroundResource(R.drawable.round_back_white10);
            Menstrual_Cycle.setBackgroundResource(R.drawable.round_back_white10);

        });

        Fertility_Contraception.setOnClickListener(view -> {

            selectedTopicName = "Fertility and Contraception";

            Fertility_Contraception.setBackgroundResource(R.drawable.round_back_white_stroke10);

            Endometriosis.setBackgroundResource(R.drawable.round_back_white10);
            Intimate_Health.setBackgroundResource(R.drawable.round_back_white10);
            Menstrual_Cycle.setBackgroundResource(R.drawable.round_back_white10);

        });

        Intimate_Health.setOnClickListener(view -> {

            selectedTopicName = "Intimate Health";

            Intimate_Health.setBackgroundResource(R.drawable.round_back_white_stroke10);

            Endometriosis.setBackgroundResource(R.drawable.round_back_white10);
            Fertility_Contraception.setBackgroundResource(R.drawable.round_back_white10);
            Menstrual_Cycle.setBackgroundResource(R.drawable.round_back_white10);

        });

        Menstrual_Cycle.setOnClickListener(view -> {

            selectedTopicName = "Menstrual Cycle";

            Menstrual_Cycle.setBackgroundResource(R.drawable.round_back_white_stroke10);

            Endometriosis.setBackgroundResource(R.drawable.round_back_white10);
            Fertility_Contraception.setBackgroundResource(R.drawable.round_back_white10);
            Intimate_Health.setBackgroundResource(R.drawable.round_back_white10);

        });

        startBtn.setOnClickListener(view -> {

            if (selectedTopicName.isEmpty())
            {
                Toast.makeText(Quiz_main.this, " Please select a topic ", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent = new Intent(Quiz_main.this, Quiz_Activity.class);
                intent.putExtra("selectedTopic", selectedTopicName);
                startActivity(intent);
            }
        });


    }
}
