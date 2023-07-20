package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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

        // list n

        List<String> nestedList1 = new ArrayList<>();
        nestedList1.add("M and M");
        nestedList1.add("M and M");
        nestedList1.add("M and M");
        nestedList1.add("M and M");
        nestedList1.add("M and M");
        nestedList1.add("M and M");
        nestedList1.add("M and M");
        nestedList1.add("M and M");

        List<String> nestedList2 = new ArrayList<>();
        nestedList2.add("M and M");
        nestedList2.add("M and M");
        nestedList2.add("M and M");
        nestedList2.add("M and M");
        nestedList2.add("M and M");
        nestedList2.add("M and M");
        nestedList2.add("M and M");
        nestedList2.add("M and M");

        List<String> nestedList3 = new ArrayList<>();
        nestedList3.add("M and M");
        nestedList3.add("M and M");
        nestedList3.add("M and M");
        nestedList3.add("M and M");
        nestedList3.add("M and M");
        nestedList3.add("M and M");
        nestedList3.add("M and M");
        nestedList3.add("M and M");

        List<String> nestedList4 = new ArrayList<>();
        nestedList4.add("M and M");
        nestedList4.add("M and M");
        nestedList4.add("M and M");
        nestedList4.add("M and M");
        nestedList4.add("M and M");
        nestedList4.add("M and M");
        nestedList4.add("M and M");
        nestedList4.add("M and M");

        List<String> nestedList5 = new ArrayList<>();
        nestedList5.add("M and M");
        nestedList5.add("M and M");
        nestedList5.add("M and M");
        nestedList5.add("M and M");
        nestedList5.add("M and M");
        nestedList5.add("M and M");
        nestedList5.add("M and M");
        nestedList5.add("M and M");

        List<String> nestedList6 = new ArrayList<>();
        nestedList6.add("M and M");
        nestedList6.add("M and M");
        nestedList6.add("M and M");
        nestedList6.add("M and M");
        nestedList6.add("M and M");
        nestedList6.add("M and M");
        nestedList6.add("M and M");
        nestedList6.add("M and M");

        List<String> nestedList7 = new ArrayList<>();
        nestedList7.add("M and M");
        nestedList7.add("M and M");
        nestedList7.add("M and M");
        nestedList7.add("M and M");
        nestedList7.add("M and M");
        nestedList7.add("M and M");
        nestedList7.add("M and M");
        nestedList7.add("M and M");

        mList.add(new DataModel(nestedList1, "Context1"));
        mList.add(new DataModel(nestedList2, "Context2"));
        mList.add(new DataModel(nestedList3, "Context3"));
        mList.add(new DataModel(nestedList4, "Context4"));
        mList.add(new DataModel(nestedList5, "Context5"));
        mList.add(new DataModel(nestedList6, "Context6"));
        mList.add(new DataModel(nestedList7, "Context7"));

        adapter = new ItemAdapter(mList);
        recyclerView.setAdapter(adapter);
    }
}