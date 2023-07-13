package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private ImageView leftIcon, rightIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endo_faq);

        leftIcon = findViewById(R.id.leftIcon);
        leftIcon.setOnClickListener(v -> startActivity(new Intent(EndoFAQ_Activity.this, UserPage_Activity.class)));

        // Right Icon
        rightIcon = findViewById(R.id.rightIcon);
        //rightIcon.setOnClickListener(v -> showHelpDialog());

        recyclerView = findViewById(R.id.endo_Questions);
        firestore = FirebaseFirestore.getInstance();

        QuestionList = new ArrayList<>();
        questionAdapter = new QuestionAdapter(QuestionList);

        recyclerView.setAdapter(questionAdapter);
        recyclerView.setHasFixedSize(true);

        fetchDataFromFirestore();
    }


    private void fetchDataFromFirestore() {
        firestore.collection("Endo_infos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String answer = document.getString("answer");

                            Questions question = new Questions(title, answer);
                            QuestionList.add(question);
                        }

                        questionAdapter.notifyDataSetChanged();
                    } else {
                        Exception exception = task.getException();
                        if (exception != null){
                            Log.e("FirestoreError", "Error fetching data: " + exception.getMessage());
                            Toast.makeText(EndoFAQ_Activity.this, "Error fetching data", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
