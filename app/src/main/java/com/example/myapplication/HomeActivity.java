package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Fragments.Community;
import com.example.myapplication.Fragments.HomeFragment;
import com.example.myapplication.Fragments.SymptomsTrackFragment;

public class HomeActivity extends AppCompatActivity {

    private int selectedTab = 1 ; // 1 because first tab is selected by default
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final LinearLayout homeLayout = findViewById(R.id.home);
        final LinearLayout trackLayout = findViewById(R.id.track);
        final LinearLayout communityLayout = findViewById(R.id.community);

        final ImageView homeImage = findViewById(R.id.home_img);
        final ImageView trackImage = findViewById(R.id.track_img);
        final ImageView communityImage = findViewById(R.id.community_img);

        final TextView hometext = findViewById(R.id.home_txt);
        final TextView tracktext = findViewById(R.id.track_txt);
        final TextView communitytext = findViewById(R.id.community_txt);

        // Set home text visible by default
        hometext.setVisibility(View.VISIBLE);

        //set home fragment by default
        getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainer, HomeFragment.class,null)
                        .commit();

        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if home is already selected or not
                if(selectedTab != 1){
                    //set home fragment
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, HomeFragment.class,null)
                            .commit();

                    //unselect other tabs expect home tab
                    communitytext.setVisibility(View.GONE);
                    tracktext.setVisibility(View.GONE);

                    communityImage.setImageResource(R.drawable.settings);
                    trackImage.setImageResource(R.drawable.calendr);

                    communityLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    trackLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select home tab
                    hometext.setVisibility(View.VISIBLE);
                    homeImage.setImageResource(R.drawable.home);  //selected icon
                    homeLayout.setBackgroundResource(R.drawable.menu_round_back);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    homeLayout.startAnimation(scaleAnimation);

                    //set 1st tab as selected tab
                    selectedTab = 1 ;
                }
            }
        });
        trackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if track is already selected or not
                if(selectedTab != 2){
                    //set track fragment
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, SymptomsTrackFragment.class,null)
                            .commit();

                    //unselect other tabs expect home tab
                    communitytext.setVisibility(View.GONE);
                    hometext.setVisibility(View.GONE);

                    communityImage.setImageResource(R.drawable.settings);
                    homeImage.setImageResource(R.drawable.home);

                    communityLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select track tab
                    tracktext.setVisibility(View.VISIBLE);
                    trackImage.setImageResource(R.drawable.calendr);  //selected icon
                    trackLayout.setBackgroundResource(R.drawable.menu_round_back);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    trackLayout.startAnimation(scaleAnimation);

                    //set 2nd tab as selected tab
                    selectedTab = 2 ;
                }
            }
        });
        communityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if community is already selected or not
                if(selectedTab != 3){
                    //set community fragment
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, Community.class,null)
                            .commit();

                    //unselect other tabs expect home tab
                    hometext.setVisibility(View.GONE);
                    tracktext.setVisibility(View.GONE);

                    homeImage.setImageResource(R.drawable.home);
                    trackImage.setImageResource(R.drawable.calendr);

                    homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    trackLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    //select community tab
                    communitytext.setVisibility(View.VISIBLE);
                    communityImage.setImageResource(R.drawable.settings);  //selected icon
                    communityLayout.setBackgroundResource(R.drawable.menu_round_back);

                    //create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    communityLayout.startAnimation(scaleAnimation);

                    //set 3rd tab as selected tab
                    selectedTab = 3 ;
                }
            }
        });
    }
}

