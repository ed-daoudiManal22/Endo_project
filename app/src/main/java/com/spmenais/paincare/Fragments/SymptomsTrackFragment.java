package com.spmenais.paincare.Fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spmenais.paincare.Adapters.ItemAdapter;
import com.spmenais.paincare.Models.DataModel;
import com.spmenais.paincare.HomeActivity;
import com.spmenais.paincare.R;
import com.spmenais.paincare.ReminderActivity;
import com.spmenais.paincare.User_profile;
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
import java.util.Objects;

public class SymptomsTrackFragment extends Fragment {
    private Slider painScoreSlider;
    private List<DataModel> mList;
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

        RecyclerView recyclerView = rootView.findViewById(R.id.main_recyclerview);
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

        ItemAdapter adapter = new ItemAdapter(requireContext(), mList);
        recyclerView.setAdapter(adapter);

        TextView currentDateTextView =  rootView.findViewById(R.id.currentDateTextView);
        // Get the user's preferred locale
        Locale currentLocale = getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", currentLocale);
        String currentDate = dateFormat.format(new Date());
        currentDateTextView.setText(currentDate);

        ImageView leftIcon = rootView.findViewById(R.id.leftIcon);
        ImageView notificationIcon = rootView.findViewById(R.id.notificationIcon);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserUid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        painScoreSlider = rootView.findViewById(R.id.painscore);
        painScoreSlider.addOnChangeListener((slider, value, fromUser) -> {
            Pair<String, Integer> painLevelInfo = getPainLevelText(value);
            TextView painLevelTextView = rootView.findViewById(R.id.painLevelTextView);
            painLevelTextView.setText(painLevelInfo.first);

            // Set text color based on the color resource ID
            int color = ContextCompat.getColor(requireContext(), painLevelInfo.second);
            painLevelTextView.setTextColor(color);
        });

        Button submitButton = rootView.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> submitSymptoms());
        leftIcon.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), User_profile.class);
            startActivity(intent);
        });

        notificationIcon.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ReminderActivity.class);
            startActivity(intent);
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
            String optionTitle = getResourceName(dataModel.getTitle());
            List<Integer> selectedPositions = dataModel.getSelectedPositions();
            List<String> selectedOptions = new ArrayList<>();
            for (int position : selectedPositions) {
                String optionValue = dataModel.getOptionsList().get(position);
                selectedOptions.add(getResourceName(optionValue)); // Convert option value to resource name
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
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to submit symptoms.", Toast.LENGTH_SHORT).show());
    }
    private String getResourceName(String resourceValue) {
        // Create a map of resource values to resource names
        Map<String, String> resourceValueToName = new HashMap<>();
        resourceValueToName.put(getString(R.string.pain_locations), "pain_locations");
        resourceValueToName.put(getString(R.string.symptoms), "symptoms");
        resourceValueToName.put(getString(R.string.pain_worse_title), "pain_worse_title");
        resourceValueToName.put(getString(R.string.feelings), "feelings");
        resourceValueToName.put(getString(R.string.abdomen), "abdomen");
        resourceValueToName.put(getString(R.string.back), "back");
        resourceValueToName.put(getString(R.string.chest), "chest");
        resourceValueToName.put(getString(R.string.head), "head");
        resourceValueToName.put(getString(R.string.neck), "neck");
        resourceValueToName.put(getString(R.string.hips), "hips");
        resourceValueToName.put(getString(R.string.cramps), "cramps");
        resourceValueToName.put(getString(R.string.tender_breasts), "tender_breasts");
        resourceValueToName.put(getString(R.string.headache), "headache");
        resourceValueToName.put(getString(R.string.acne), "acne");
        resourceValueToName.put(getString(R.string.fatigue), "fatigue");
        resourceValueToName.put(getString(R.string.bloating), "bloating");
        resourceValueToName.put(getString(R.string.craving), "craving");
        resourceValueToName.put(getString(R.string.lack_of_sleep), "lack_of_sleep");
        resourceValueToName.put(getString(R.string.sitting), "sitting");
        resourceValueToName.put(getString(R.string.standing), "standing");
        resourceValueToName.put(getString(R.string.stress), "stress");
        resourceValueToName.put(getString(R.string.walking), "walking");
        resourceValueToName.put(getString(R.string.exercise), "exercise");
        resourceValueToName.put(getString(R.string.urination), "urination");
        resourceValueToName.put(getString(R.string.anxious), "anxious");
        resourceValueToName.put(getString(R.string.depressed), "depressed");
        resourceValueToName.put(getString(R.string.dizzy), "dizzy");
        resourceValueToName.put(getString(R.string.vomiting), "vomiting");
        resourceValueToName.put(getString(R.string.diarrhea), "diarrhea");
        resourceValueToName.put(getString(R.string.nothing), "nothing");

        String resourceName = resourceValueToName.get(resourceValue);
        if (resourceName != null) {
            return resourceName;
        } else {
            // Handle the case when the resource name is not found
            Log.e("DiagTest_Activity", "Resource name not found for value: " + resourceValue);
            return "Resource name not found";
        }
    }
    private Pair<String, Integer> getPainLevelText(float painScore) {
        Resources resources = getResources();
        int colorResourceId;

        if (painScore == 0) {
            return new Pair<>(resources.getString(R.string.pain_level_non), R.color.blue);
        } else if (painScore >= 1 && painScore <= 3) {
            return new Pair<>(resources.getString(R.string.pain_level_mild), R.color.green);
        } else if (painScore >= 4 && painScore <= 5) {
            return new Pair<>(resources.getString(R.string.pain_level_moderate), R.color.yellow);
        } else if (painScore >= 6 && painScore <= 7) {
            return new Pair<>(resources.getString(R.string.pain_level_severe), R.color.orange1);
        } else if (painScore >= 8 && painScore <= 9) {
            return new Pair<>(resources.getString(R.string.pain_level_very_severe), R.color.calm_red);
        } else {
            return new Pair<>(resources.getString(R.string.pain_level_worst), R.color.red);
        }
    }
}