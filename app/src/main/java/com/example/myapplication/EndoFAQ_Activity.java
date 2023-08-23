package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.QuestionAdapter;
import com.example.myapplication.Models.Questions;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EndoFAQ_Activity extends AppCompatActivity {
    private List<Questions> QuestionList;
    private QuestionAdapter questionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endo_faq);

        ImageView leftIcon = findViewById(R.id.leftIcon);
        ImageView notificationIcon = findViewById(R.id.notificationIcon);
        leftIcon.setOnClickListener(v -> {
            // Handle the click event, navigate to HelloActivity
            Intent intent = new Intent(EndoFAQ_Activity.this, Endo_InfoActivity.class);
            startActivity(intent);
            finish(); // Optional: Close the current activity after navigating
        });
        notificationIcon.setOnClickListener(v -> {
            // Handle the click event, navigate to HelloActivity
            Intent intent = new Intent(EndoFAQ_Activity.this, ReminderActivity.class);
            startActivity(intent);
            finish(); // Optional: Close the current activity after navigating
        });

        RecyclerView recyclerView = findViewById(R.id.endo_Questions);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        QuestionList = new ArrayList<>();
        questionAdapter = new QuestionAdapter(QuestionList);

        recyclerView.setAdapter(questionAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView = findViewById(R.id.endo_Questions);

        QuestionList = new ArrayList<>();

        // Reference to the Firestore collection
        CollectionReference endoInfosCollection = firestore.collection("Endo_FAQ");
        // Fetch the data from Firestore
        endoInfosCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e("Firestore", "Error fetching data: " + e.getMessage());
                return;
            }

            QuestionList.clear(); // Clear the existing list before adding new questions

            // Iterate through the documents and extract question and answer data
            if (queryDocumentSnapshots != null) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.exists()) {
                        String question = doc.getString("question");
                        String answer = doc.getString("answer");

                        // Add the question to the QuestionList
                        if (question != null && answer != null) {
                            QuestionList.add(new Questions(question, answer,false));
                        }
                    }
                }
            }

            // Notify the adapter about the data change
            questionAdapter.notifyDataSetChanged();
        });
        questionAdapter = new QuestionAdapter(QuestionList);

        recyclerView.setAdapter(questionAdapter);
        recyclerView.setHasFixedSize(true);
    }
}
