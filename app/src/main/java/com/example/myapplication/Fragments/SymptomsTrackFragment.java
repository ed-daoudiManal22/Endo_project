package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapters.ItemAdapter;
import com.example.myapplication.Models.DataModel;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.R;
import com.example.myapplication.ReminderActivity;
import com.example.myapplication.User_profile;
import com.google.android.material.slider.Slider;
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
public class SymptomsTrackFragment extends Fragment {
    private Slider painScoreSlider;
    private RecyclerView recyclerView;
    private List<DataModel> mList;
    private ItemAdapter adapter;
    private ImageView leftIcon,notificationIcon ;
    private Button submitButton;
    private String currentUserUid;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    public SymptomsTrackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_symptoms_track, container, false);

        recyclerView = rootView.findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        mList = new ArrayList<>();

        // Initialize and populate the nested lists using the string arrays
        List<String> painLocationOptions = new ArrayList<>();
        painLocationOptions.add(getString(R.string.abdomen));
        painLocationOptions.add(getString(R.string.back));
        painLocationOptions.add(getString(R.string.chest));
        painLocationOptions.add(getString(R.string.head));
        painLocationOptions.add(getString(R.string.neck));
        painLocationOptions.add(getString(R.string.hips));

        List<String> symptomsOptions = new ArrayList<>();
        symptomsOptions.add(getString(R.string.cramps));
        symptomsOptions.add(getString(R.string.tender_breasts));
        symptomsOptions.add(getString(R.string.headache));
        symptomsOptions.add(getString(R.string.acne));
        symptomsOptions.add(getString(R.string.fatigue));
        symptomsOptions.add(getString(R.string.bloating));
        symptomsOptions.add(getString(R.string.craving));

        List<String> painWorseOptions = new ArrayList<>();
        painWorseOptions.add(getString(R.string.lack_of_sleep));
        painWorseOptions.add(getString(R.string.sitting));
        painWorseOptions.add(getString(R.string.standing));
        painWorseOptions.add(getString(R.string.stress));
        painWorseOptions.add(getString(R.string.walking));
        painWorseOptions.add(getString(R.string.exercise));
        painWorseOptions.add(getString(R.string.urination));

        List<String> feelingOptions = new ArrayList<>();
        feelingOptions.add(getString(R.string.anxious));
        feelingOptions.add(getString(R.string.depressed));
        feelingOptions.add(getString(R.string.dizzy));
        feelingOptions.add(getString(R.string.vomiting));
        feelingOptions.add(getString(R.string.diarrhea));

        // Add the populated nested lists to mList
        mList.add(new DataModel(painLocationOptions, getString(R.string.pain_locations)));
        mList.add(new DataModel(symptomsOptions, getString(R.string.symptoms)));
        mList.add(new DataModel(painWorseOptions, getString(R.string.pain_worse_title)));
        mList.add(new DataModel(feelingOptions, getString(R.string.feelings)));

        adapter = new ItemAdapter(requireContext(),mList);
        recyclerView.setAdapter(adapter);

        TextView currentDateTextView =  rootView.findViewById(R.id.currentDateTextView);
        // Get the user's preferred locale
        Locale currentLocale = getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", currentLocale);
        String currentDate = dateFormat.format(new Date());
        currentDateTextView.setText(currentDate);

        leftIcon =  rootView.findViewById(R.id.leftIcon);
        notificationIcon = rootView.findViewById(R.id.notificationIcon);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserUid = firebaseAuth.getCurrentUser().getUid();

        painScoreSlider =  rootView.findViewById(R.id.painscore);
        submitButton =  rootView.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSymptoms();
            }
        });
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), User_profile.class);
                startActivity(intent);
            }
        });

        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ReminderActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
    private void submitSymptoms() {
        float painScore = painScoreSlider.getValue();

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
                    Toast.makeText(requireContext(), "Symptoms submitted successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), HomeActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to submit symptoms.", Toast.LENGTH_SHORT).show();
                });
    }
}