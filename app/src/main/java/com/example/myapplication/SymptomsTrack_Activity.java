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

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SymptomsTrack_Activity extends AppCompatActivity {

    private SeekBar painScoreSeekBar;
    private Spinner painLocationSpinner;
    private Spinner feelingSpinner;
    private Spinner symptomsSpinner;
    private Spinner painWorseSpinner;
    private Spinner medicationSpinner;
    private ImageView leftIcon;
    private Button submitButton, cancelButton;
    private String currentUserUid;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptoms_tracking);

        TextView currentDateTextView = findViewById(R.id.currentDateTextView);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        currentDateTextView.setText(currentDate);

        leftIcon = findViewById(R.id.leftIcon);


        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserUid = firebaseAuth.getCurrentUser().getUid();

        painScoreSeekBar = findViewById(R.id.painscore);
        painLocationSpinner = findViewById(R.id.painlocationSpinner);
        feelingSpinner = findViewById(R.id.feelingSpinner);
        symptomsSpinner = findViewById(R.id.symptomsSpinner);
        painWorseSpinner = findViewById(R.id.painworseSpinner);
        medicationSpinner = findViewById(R.id.medicationSpinner);
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
        String painScore = String.valueOf(painScoreSeekBar.getProgress());
        String painLocation = painLocationSpinner.getSelectedItem().toString();
        String feeling = feelingSpinner.getSelectedItem().toString();
        String symptoms = symptomsSpinner.getSelectedItem().toString();
        String painWorse = painWorseSpinner.getSelectedItem().toString();
        String medication = medicationSpinner.getSelectedItem().toString();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // Create a new symptom document
        Map<String, Object> symptomData = new HashMap<>();
        symptomData.put("painScore", painScore);
        symptomData.put("painLocation", painLocation);
        symptomData.put("feeling", feeling);
        symptomData.put("symptoms", symptoms);
        symptomData.put("painWorse", painWorse);
        symptomData.put("medication", medication);

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
