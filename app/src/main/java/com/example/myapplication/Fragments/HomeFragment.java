package com.example.myapplication.Fragments;

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

import com.example.myapplication.Community.DrawerActivity;
import com.example.myapplication.Diag_start;
import com.example.myapplication.Endo_InfoActivity;
import com.example.myapplication.LineChart_Activity;
import com.example.myapplication.Quiz_main;
import com.example.myapplication.R;
import com.example.myapplication.ReminderActivity;
import com.example.myapplication.User_profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        CardView card1 = view.findViewById(R.id.card1);
        CardView card2 = view.findViewById(R.id.card2);
        CardView card3 = view.findViewById(R.id.card3);
        CardView card4 = view.findViewById(R.id.card4);
        menuIcon = view.findViewById(R.id.menuIcon);
        notificationIcon = view.findViewById(R.id.notificationIcon);

        // Set current user text
        currentUserText.setText(getCurrentUserName());

        // Set click listeners for the cards
        card1.setOnClickListener(v -> startActivity(new Intent(requireContext(), Diag_start.class)));

        card2.setOnClickListener(v -> startActivity(new Intent(requireContext(), DrawerActivity.class)));

        card3.setOnClickListener(v -> startActivity(new Intent(requireContext(), LineChart_Activity.class)));

        card4.setOnClickListener(v -> startActivity(new Intent(requireContext(), Quiz_main.class)));

        // Set click listeners for the menu icon
        menuIcon.setOnClickListener(v -> startActivity(new Intent(requireContext(), User_profile.class)));

        // Set click listeners for the notification icon
        notificationIcon.setOnClickListener(v -> startActivity(new Intent(requireContext(), ReminderActivity.class)));
    }

    private String getCurrentUserName() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            return displayName != null ? displayName : "Guest";
        } else {
            return "Guest";
        }
    }
}
