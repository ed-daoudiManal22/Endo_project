package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Authentification.Authentification;

public class MainActivity extends AppCompatActivity {

    private TextView appTitle, appName;
    private ImageView logo;
    private View topView2;
    private View topView3;
    private View bottomView2;
    private View bottomView3;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.activity_main);

        appTitle = findViewById(R.id.appTitle);
        appName = findViewById(R.id.appName);


        logo = findViewById(R.id.logo);

        View topView1 = findViewById(R.id.topView1);
        topView2 = findViewById(R.id.topView2);
        topView3 = findViewById(R.id.topView3);

        View bottomView1 = findViewById(R.id.bottomView1);
        bottomView2 = findViewById(R.id.bottomView2);
        bottomView3 = findViewById(R.id.bottomView3);

        Animation logoAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_animation);
        Animation titleAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_animation);
        Animation nameAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_animation);

        Animation topView1Animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.top_views_animation);
        Animation topView2Animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.top_views_animation);
        Animation topView3Animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.top_views_animation);

        Animation bottomView1Animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_views_animation);
        Animation bottomView2Animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_views_animation);
        Animation bottomView3Animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_views_animation);

        topView1.startAnimation(topView1Animation);
        bottomView1.startAnimation(bottomView1Animation);

        topView1Animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                topView2.setVisibility(View.VISIBLE);
                bottomView2.setVisibility(View.VISIBLE);

                topView2.startAnimation(topView2Animation);
                bottomView2.startAnimation(bottomView2Animation);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        topView2Animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                topView3.setVisibility(View.VISIBLE);
                bottomView3.setVisibility(View.VISIBLE);

                topView3.startAnimation(topView3Animation);
                bottomView3.startAnimation(bottomView3Animation);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        topView3Animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                logo.setVisibility(View.VISIBLE);
                logo.startAnimation(logoAnimation);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        logoAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                appTitle.setVisibility(View.VISIBLE);
                appTitle.startAnimation(titleAnimation);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        titleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                appName.setVisibility(View.VISIBLE);
                final String animateTxt = appName.getText().toString();
                appName.setText("");
                count = 0;

                new CountDownTimer(animateTxt.length() * 100L, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        appName.setText(appName.getText().toString()+animateTxt.charAt(count));
                        count++;

                    }

                    @Override
                    public void onFinish() {
                        // All animations are finished, wait for 3 seconds before redirecting
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            Intent intent = new Intent(MainActivity.this, Authentification.class);
                            startActivity(intent);
                            finish(); // Optionally, you can finish the current activity to prevent going back
                        }, 500); // 1000 milliseconds = 1 second
                    }
                }.start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}