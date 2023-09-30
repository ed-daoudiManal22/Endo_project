package com.spmenais.paincare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.spmenais.paincare.Adapters.QuestionAdapter;
import com.spmenais.paincare.Models.Questions;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EndoFAQ_Activity extends AppCompatActivity {
    private List<Questions> QuestionList;
    private QuestionAdapter questionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endo_faq);

        setupNavigationButtons();
        setupRecyclerView();
        loadFAQData();
    }

    private void setupNavigationButtons() {
        ImageView leftIcon = findViewById(R.id.leftIcon);
        ImageView notificationIcon = findViewById(R.id.notificationIcon);

        leftIcon.setOnClickListener(v -> navigateToActivity(Endo_InfoActivity.class));
        notificationIcon.setOnClickListener(v -> navigateToActivity(ReminderActivity.class));
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(EndoFAQ_Activity.this, activityClass);
        startActivity(intent);
        finish();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.endo_Questions);
        recyclerView.setHasFixedSize(true);

        QuestionList = new ArrayList<>();
        questionAdapter = new QuestionAdapter(QuestionList);
        recyclerView.setAdapter(questionAdapter);
    }

    private void loadFAQData() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference endoInfosCollection = firestore.collection("Endo_FAQ");

        endoInfosCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                handleFirestoreError(e);
                return;
            }

            List<Questions> newQuestions = extractQuestionsFromSnapshot(queryDocumentSnapshots);

            updateQuestionList(newQuestions);
        });
    }

    private void handleFirestoreError(Exception e) {
        Log.e("Firestore", "Error fetching data: " + e.getMessage());
    }

    private List<Questions> extractQuestionsFromSnapshot(QuerySnapshot queryDocumentSnapshots) {
        List<Questions> newQuestions = new ArrayList<>();

        if (queryDocumentSnapshots != null) {
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                if (doc.exists()) {
                    String question = doc.getString("question");
                    String answer = doc.getString("answer");

                    if (question != null && answer != null) {
                        newQuestions.add(new Questions(question, answer, false));
                    }
                }
            }
        }

        return newQuestions;
    }

    private void updateQuestionList(List<Questions> newQuestions) {
        QuestionList.clear(); // Clear the existing list before adding new questions
        QuestionList.addAll(newQuestions);
        questionAdapter.notifyDataSetChanged();
    }


}
