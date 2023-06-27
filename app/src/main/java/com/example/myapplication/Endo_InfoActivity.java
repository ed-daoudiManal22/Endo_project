package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.QuestionAdapter;
import com.example.myapplication.Models.Questions;

import java.util.ArrayList;
import java.util.List;

public class Endo_InfoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Questions> QuestionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endo_infos);

        recyclerView = findViewById(R.id.endo_Questions);

        initData();
        setRecyclerView();
    }

    private void initData() {
        QuestionList = new ArrayList<>();
        QuestionList.add(new Questions("title 1","Answer 1"));
        QuestionList.add(new Questions("title 2","Answer 2"));
        QuestionList.add(new Questions("title 3","Answer 3"));
        QuestionList.add(new Questions("title 4","Answer 4"));
        QuestionList.add(new Questions("title 5","Answer 5"));
        QuestionList.add(new Questions("title 6","Answer 6"));
        QuestionList.add(new Questions("title 7","Answer 7"));
    }

    private void setRecyclerView() {
        QuestionAdapter questionAdapter =new QuestionAdapter(QuestionList);
        recyclerView.setAdapter(questionAdapter);
        recyclerView.setHasFixedSize(true);
    }
}
