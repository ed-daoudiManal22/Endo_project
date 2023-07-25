package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.myapplication.Adapters.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<DataModel> mList;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        recyclerView = findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mList = new ArrayList<>();

        // Initialize and populate the nested lists using the string arrays
        List<String> painLocationOptions = new ArrayList<>();
        painLocationOptions.add("Nothing");
        painLocationOptions.add("Abdomen");
        painLocationOptions.add("Back");
        painLocationOptions.add("Chest");
        painLocationOptions.add("Head");
        painLocationOptions.add("Neck");
        painLocationOptions.add("Hips");

        List<String> symptomsOptions = new ArrayList<>();
        symptomsOptions.add("Nothing");
        symptomsOptions.add("Cramps");
        symptomsOptions.add("Tender breasts");
        symptomsOptions.add("Headache");
        symptomsOptions.add("Acne");
        symptomsOptions.add("Fatigue");
        symptomsOptions.add("Bloating");
        symptomsOptions.add("Craving");

        List<String> painWorseOptions = new ArrayList<>();
        painWorseOptions.add("Nothing");
        painWorseOptions.add("Lack of sleep");
        painWorseOptions.add("Sitting");
        painWorseOptions.add("Standing");
        painWorseOptions.add("Stress");
        painWorseOptions.add("Walking");
        painWorseOptions.add("Exercise");
        painWorseOptions.add("Urination");

        List<String> feelingOptions = new ArrayList<>();
        feelingOptions.add("Nothing");
        feelingOptions.add("Anxious");
        feelingOptions.add("Depressed");
        feelingOptions.add("Dizzy");
        feelingOptions.add("Vomiting");
        feelingOptions.add("Diarrhea");

        List<String> medsOptions = new ArrayList<>();
        medsOptions.add("Nothing");

        // Add the populated nested lists to mList
        mList.add(new DataModel(painLocationOptions, "pain Location"));
        mList.add(new DataModel(symptomsOptions, "symptoms"));
        mList.add(new DataModel(painWorseOptions, "What Made Your Pain Worse?"));
        mList.add(new DataModel(feelingOptions, "How You Feel Today?"));
        mList.add(new DataModel(medsOptions, "What Medication Did You Try for Your Pain?"));

        adapter = new ItemAdapter(mList);
        recyclerView.setAdapter(adapter);
    }
}