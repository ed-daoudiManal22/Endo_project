package com.spmenais.paincare.Fragments;

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

import com.spmenais.paincare.Diag_start;
import com.spmenais.paincare.Endo_InfoActivity;
import com.spmenais.paincare.LineChart_Activity;
import com.spmenais.paincare.Quiz_main;
import com.spmenais.paincare.R;
import com.spmenais.paincare.ReminderActivity;
import com.spmenais.paincare.User_profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private TextView currentUserText;
    private FirebaseAuth firebaseAuth;

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
        ImageView menuIcon = view.findViewById(R.id.menuIcon);
        ImageView notificationIcon = view.findViewById(R.id.notificationIcon);

        getCurrentUserName();
        // Set the risk level in the scoreText TextView
        setRiskLevelForCurrentUser(scoreText, requireContext());

        // Set click listeners for the cards
        card1.setOnClickListener(v -> startActivity(new Intent(requireContext(), Diag_start.class)));

        card2.setOnClickListener(v -> startActivity(new Intent(requireContext(), Endo_InfoActivity.class)));

        card3.setOnClickListener(v -> startActivity(new Intent(requireContext(), LineChart_Activity.class)));

        card4.setOnClickListener(v -> startActivity(new Intent(requireContext(), Quiz_main.class)));

        // Set click listeners for the menu icon
        menuIcon.setOnClickListener(v -> startActivity(new Intent(requireContext(), User_profile.class)));

        // Set click listeners for the notification icon
        notificationIcon.setOnClickListener(v -> startActivity(new Intent(requireContext(), ReminderActivity.class)));
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
                    .addOnFailureListener(e ->
                        // Handle failure
                        currentUserText.setText("Error: " + e.getMessage()));
        } else {
            currentUserText.setText("Guest");
        }
    }

    private void setRiskLevelForCurrentUser(TextView scoreText, Context context) {
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
                                scoreText.setText(getResourceString(context,riskLevel));
                            } else {
                                scoreText.setText(getResourceString(context,"No_Risk"));
                            }
                        } else {
                            scoreText.setText(getResourceString(context,"No_Risk"));
                        }
                    })
                    .addOnFailureListener(e ->
                        // Handle failure
                        scoreText.setText("Error: " + e.getMessage()));
        } else {
            scoreText.setText("No Risk Level (Guest)");
        }
    }
    private String getResourceString(Context context, String resourceName) {
        int resId = context.getResources().getIdentifier(resourceName, "string", context.getPackageName());
        return context.getString(resId);
    }

}
