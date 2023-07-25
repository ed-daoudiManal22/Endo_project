package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeActivity extends AppCompatActivity {
    private TextView currentUserText;
    private FirebaseAuth firebaseAuth;
    private MeowBottomNavigation menuBottomNavigation;
    private ImageView menuIcon,notificationIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserText = findViewById(R.id.userName);
        currentUserText.setText(getCurrentUserName());

        menuBottomNavigation = findViewById(R.id.menuBottomNavigation);

        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);
        CardView card3 = findViewById(R.id.card3);
        CardView card4 = findViewById(R.id.card4);
        menuIcon = findViewById(R.id.menuIcon);
        notificationIcon = findViewById(R.id.notificationIcon);
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Activity1
                Intent intent = new Intent(HomeActivity.this, Diag_start.class);
                startActivity(intent);
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Activity2
                Intent intent = new Intent(HomeActivity.this, Endo_InfoActivity.class);
                startActivity(intent);
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Activity3
                Intent intent = new Intent(HomeActivity.this, LineChart_Activity.class);
                startActivity(intent);
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Activity4
                Intent intent = new Intent(HomeActivity.this, Quiz_main.class);
                startActivity(intent);
            }
        });

        // Set click listeners for the menu icon
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the activity for the menu icon
                Intent intent = new Intent(HomeActivity.this, User_profile.class);
                startActivity(intent);
            }
        });

        // Set click listeners for the notification icon
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the activity for the notification icon
                Intent intent = new Intent(HomeActivity.this, ReminderActivity.class);
                startActivity(intent);
            }
        });

        menuBottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.calendr)); //track_symptom
        menuBottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.home));
        menuBottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.settings));  //comumnity

        menuBottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 1:

                        break;


                }
                return null;
            }
        });

        menuBottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 1:

                        break;


                }

                return null;
            }
        });

        menuBottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 2:

                        break;


                }

                return null;
            }
        });

        menuBottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                // YOUR CODES

                switch (model.getId()){

                    case 3:

                        break;


                }

                return null;
            }
        });

    }

    private String getCurrentUserName() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName != null) {
                return displayName;
            } else {
                return "Guest"; // Or any other default message you prefer
            }
        } else {
            return "Guest"; // User is not logged in, so show a default message
        }
    }
}

