package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.ItemAdapter;
import com.example.myapplication.Models.DataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
                Intent intent = new Intent(SymptomsTrack_Activity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SymptomsTrack_Activity.this, HomeActivity.class);
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

        // Store the selected options in Firebase
        for (DataModel dataModel : mList) {
            String optionTitle = dataModel.getTitle();
            List<Integer> selectedPositions = dataModel.getSelectedPositions();
            List<String> selectedOptions = new ArrayList<>();
            for (int position : selectedPositions) {
                selectedOptions.add(dataModel.getOptionsList().get(position));
            }
            symptomData.put(optionTitle, selectedOptions);
        }

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
