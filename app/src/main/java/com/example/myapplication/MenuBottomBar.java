package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MenuBottomBar extends AppCompatActivity {

    private MeowBottomNavigation menuBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        menuBottomNavigation = findViewById(R.id.menuBottomNavigation);

        menuBottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.settings));
        menuBottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.home));
        menuBottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.calendr));

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
}
