package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SymptomsTrack_Activity extends AppCompatActivity {

    private SeekBar painScoreSeekBar;
    private RecyclerView recyclerView;
    private List<DataModel> mList;
    private ItemAdapter adapter;
    private ImageView leftIcon;
    private Button submitButton, cancelButton;
    private String[] painLocationOptions, symptomsOptions, painWorseOptions,feelingOptions, medsOptions ;
    private String currentUserUid;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptoms_tracking);

        recyclerView = findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mList = new ArrayList<>();

        // Retrieve the arrays from arrays.xml
        List<String> painLocationOptionsList = Arrays.asList(painLocationOptions);
        List<String> symptomsOptionsList = Arrays.asList(symptomsOptions);
        List<String> painWorseOptionsList = Arrays.asList(painWorseOptions);
        List<String> feelingOptionsList = Arrays.asList(feelingOptions);
        List<String> medsOptionsList = Arrays.asList(medsOptions);

        // Add the populated nested lists to mList

        mList.add(new DataModel(painLocationOptionsList, "pain Location"));
        mList.add(new DataModel(symptomsOptionsList, "symptoms"));
        mList.add(new DataModel(painWorseOptionsList, "What Made Your Pain Worse?"));
        mList.add(new DataModel(feelingOptionsList, "How You Feel Today?"));
        mList.add(new DataModel(medsOptionsList, "What Medication Did You Try for Your Pain?"));

        adapter = new ItemAdapter(mList);
        recyclerView.setAdapter(adapter);

        TextView currentDateTextView = findViewById(R.id.currentDateTextView);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        currentDateTextView.setText(currentDate);

        leftIcon = findViewById(R.id.leftIcon);


        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserUid = firebaseAuth.getCurrentUser().getUid();

        painScoreSeekBar = findViewById(R.id.painscore);
        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSymptoms();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SymptomsTrack_Activity.this, UserPage_Activity.class);
                startActivity(intent);
            }
        });
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event and redirect to UserPage_Activity
                Intent intent = new Intent(SymptomsTrack_Activity.this, UserPage_Activity.class);
                startActivity(intent);
            }
        });
    }

    private void submitSymptoms() {
        int painScore = painScoreSeekBar.getProgress();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // Create a new symptom document
        Map<String, Object> symptomData = new HashMap<>();
        symptomData.put("painScore", painScore);

        DocumentReference userSymptomRef = firestore
                .collection("Users")
                .document(currentUserUid)
                .collection("symptoms")
                .document(currentDate);

        userSymptomRef.set(symptomData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(SymptomsTrack_Activity.this, "Symptoms submitted successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SymptomsTrack_Activity.this, UserPage_Activity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SymptomsTrack_Activity.this, "Failed to submit symptoms.", Toast.LENGTH_SHORT).show();
                });
    }
}
