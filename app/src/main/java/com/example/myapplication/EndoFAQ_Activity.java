package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.QuestionAdapter;
import com.example.myapplication.Models.Questions;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EndoFAQ_Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Questions> QuestionList;
    private QuestionAdapter questionAdapter;
    private FirebaseFirestore firestore;
    private ImageView leftIcon, notificationIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endo_faq);

        leftIcon = findViewById(R.id.leftIcon);
        notificationIcon = findViewById(R.id.notificationIcon);
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event, navigate to HelloActivity
                Intent intent = new Intent(EndoFAQ_Activity.this, Endo_InfoActivity.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity after navigating
            }
        });
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event, navigate to HelloActivity
                Intent intent = new Intent(EndoFAQ_Activity.this, ReminderActivity.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity after navigating
            }
        });

        recyclerView = findViewById(R.id.endo_Questions);
        firestore = FirebaseFirestore.getInstance();

        QuestionList = new ArrayList<>();
        questionAdapter = new QuestionAdapter(QuestionList);

        recyclerView.setAdapter(questionAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView = findViewById(R.id.endo_Questions);

        QuestionList = new ArrayList<>();
        // Add resource IDs for questions from the strings.xml file to the QuestionList
        QuestionList.add(new Questions(R.string.Endometriosis, R.string.Endometriosis_answer, this));
        QuestionList.add(new Questions(R.string.Endo_Symptoms, R.string.Endo_Symptoms_answer, this));
        QuestionList.add(new Questions(R.string.Endo_riskFactors, R.string.Endo_riskFactors_answer, this));
        QuestionList.add(new Questions(R.string.Endo_reduces, R.string.Endo_reducess_answer, this));
        QuestionList.add(new Questions(R.string.Endo_importance, R.string.Endo_health_prob_answer, this));
        QuestionList.add(new Questions(R.string.Endo_importance, R.string.Endo_importance_answer, this));

        questionAdapter = new QuestionAdapter(QuestionList);

        recyclerView.setAdapter(questionAdapter);
        recyclerView.setHasFixedSize(true);
    }
}
