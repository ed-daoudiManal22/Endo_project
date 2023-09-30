package com.spmenais.paincare;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spmenais.paincare.Fragments.CalendarFragment;
import com.spmenais.paincare.Fragments.Community;
import com.spmenais.paincare.Fragments.HomeFragment;
import com.spmenais.paincare.Fragments.SymptomsTrackFragment;

public class HomeActivity extends AppCompatActivity {

    private int selectedTab = 1 ; // 1 because first tab is selected by default
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final LinearLayout homeLayout = findViewById(R.id.home);
        final LinearLayout trackLayout = findViewById(R.id.track);
        final LinearLayout communityLayout = findViewById(R.id.community);
        final LinearLayout calendarLayout = findViewById(R.id.calendar);

        final ImageView homeImage = findViewById(R.id.home_img);
        final ImageView trackImage = findViewById(R.id.track_img);
        final ImageView communityImage = findViewById(R.id.community_img);
        final ImageView calendarImage = findViewById(R.id.calendar_img);

        final TextView hometext = findViewById(R.id.home_txt);
        final TextView tracktext = findViewById(R.id.track_txt);
        final TextView communitytext = findViewById(R.id.community_txt);
        final TextView calendartext = findViewById(R.id.calendar_txt);

        // Set home text visible by default
        hometext.setVisibility(View.VISIBLE);

        //set home fragment by default
        getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainer, HomeFragment.class,null)
                        .commit();

        homeLayout.setOnClickListener(view -> {
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
                calendartext.setVisibility(View.GONE);

                communityImage.setImageResource(R.drawable.community);
                trackImage.setImageResource(R.drawable.tracking);
                calendarImage.setImageResource(R.drawable.calendr);

                communityLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                trackLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                calendarLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

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
        });
        trackLayout.setOnClickListener(view -> {
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
                calendartext.setVisibility(View.GONE);

                communityImage.setImageResource(R.drawable.community);
                homeImage.setImageResource(R.drawable.home);
                calendarImage.setImageResource(R.drawable.calendr);

                communityLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                calendarLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                //select track tab
                tracktext.setVisibility(View.VISIBLE);
                trackImage.setImageResource(R.drawable.tracking);  //selected icon
                trackLayout.setBackgroundResource(R.drawable.menu_round_back);

                //create animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0.0f);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillAfter(true);
                trackLayout.startAnimation(scaleAnimation);

                //set 2nd tab as selected tab
                selectedTab = 2 ;
            }
        });
        communityLayout.setOnClickListener(view -> {
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
                calendartext.setVisibility(View.GONE);

                homeImage.setImageResource(R.drawable.home);
                trackImage.setImageResource(R.drawable.tracking);
                calendarImage.setImageResource(R.drawable.calendr);

                homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                trackLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                calendarLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                //select community tab
                communitytext.setVisibility(View.VISIBLE);
                communityImage.setImageResource(R.drawable.community);  //selected icon
                communityLayout.setBackgroundResource(R.drawable.menu_round_back);

                //create animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0.0f);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillAfter(true);
                communityLayout.startAnimation(scaleAnimation);

                //set 3rd tab as selected tab
                selectedTab = 3 ;
            }
        });
        calendarLayout.setOnClickListener(view -> {
            // Check if the calendar tab is already selected or not
            if(selectedTab != 4){
                // Handle the calendar tab click
                // For example, launch a new activity or fragment to display the calendar
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainer, CalendarFragment.class, null)
                        .commit();

                // Unselect other tabs except the calendar tab
                hometext.setVisibility(View.GONE);
                tracktext.setVisibility(View.GONE);
                communitytext.setVisibility(View.GONE);

                homeImage.setImageResource(R.drawable.home);
                trackImage.setImageResource(R.drawable.tracking);
                communityImage.setImageResource(R.drawable.community);

                homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                trackLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                communityLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                // Select the calendar tab
                calendartext.setVisibility(View.VISIBLE);
                calendarImage.setImageResource(R.drawable.calendr);  // Selected icon
                calendarLayout.setBackgroundResource(R.drawable.menu_round_back);

                // Create animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillAfter(true);
                calendarLayout.startAnimation(scaleAnimation);

                // Set 4th tab as the selected tab
                selectedTab = 4;
            }
        });
        // Check for the fragment to load
        String fragmentToLoad = getIntent().getStringExtra("fragment_to_load");
        if (fragmentToLoad != null && fragmentToLoad.equals("community")) {
            loadCommunityFragment(); // Load the Community fragment
        }
    }
    private void loadCommunityFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new Community()) // Replace with your container view ID
                .commit();
    }
}

