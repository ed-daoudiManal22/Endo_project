package com.example.myapplication.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Diag_start;
import com.example.myapplication.Endo_InfoActivity;
import com.example.myapplication.LineChart_Activity;
import com.example.myapplication.NotifiactionSettings_Activity;
import com.example.myapplication.Quiz_main;
import com.example.myapplication.R;
import com.example.myapplication.ReminderActivity;
import com.example.myapplication.User_profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private TextView currentUserText;
    private FirebaseAuth firebaseAuth;
    private ImageView menuIcon, notificationIcon;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        currentUserText = view.findViewById(R.id.userName);
        TextView scoreText = view.findViewById(R.id.scoreText);
        CardView card1 = view.findViewById(R.id.card1);
        CardView card2 = view.findViewById(R.id.card2);
        CardView card3 = view.findViewById(R.id.card3);
        CardView card4 = view.findViewById(R.id.card4);
        menuIcon = view.findViewById(R.id.menuIcon);
        notificationIcon = view.findViewById(R.id.notificationIcon);

        getCurrentUserName();
        // Set the risk level in the scoreText TextView
        setRiskLevelForCurrentUser(scoreText);

        // Set click listeners for the cards
        card1.setOnClickListener(v -> startActivity(new Intent(requireContext(), Diag_start.class)));

        card2.setOnClickListener(v -> startActivity(new Intent(requireContext(), Endo_InfoActivity.class)));

        card3.setOnClickListener(v -> startActivity(new Intent(requireContext(), LineChart_Activity.class)));

        card4.setOnClickListener(v -> startActivity(new Intent(requireContext(), Quiz_main.class)));

        // Set click listeners for the menu icon
        menuIcon.setOnClickListener(v -> startActivity(new Intent(requireContext(), User_profile.class)));

        // Set click listeners for the notification icon
        notificationIcon.setOnClickListener(v -> startActivity(new Intent(requireContext(), NotifiactionSettings_Activity.class)));
    }

    private void getCurrentUserName() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            if (name != null && !name.isEmpty()) {
                                // Use the retrieved name from Firestore
                                currentUserText.setText("Hi "+name);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        currentUserText.setText("Error: " + e.getMessage());
                    });
        } else {
            currentUserText.setText("Guest");
        }
    }

    private void setRiskLevelForCurrentUser(TextView scoreText) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String riskLevel = documentSnapshot.getString("riskLevel");
                            if (riskLevel != null) {
                                scoreText.setText(getResourceString(requireContext(),riskLevel));
                            } else {
                                scoreText.setText(getResourceString(requireContext(),"Ready"));
                            }
                        } else {
                            scoreText.setText(getResourceString(requireContext(),"Ready"));
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        scoreText.setText("Error: " + e.getMessage());
                    });
        } else {
            scoreText.setText("No Risk Level (Guest)");
        }
    }
    private String getResourceString(Context context, String resourceName) {
        int resId = context.getResources().getIdentifier(resourceName, "string", context.getPackageName());
        return context.getString(resId);
    }

}
