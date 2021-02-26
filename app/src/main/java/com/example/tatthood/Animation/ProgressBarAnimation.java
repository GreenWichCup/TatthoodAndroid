package com.example.tatthood.Animation;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tatthood.Activity.HomeActivity;
import com.example.tatthood.OnBoarding.OnBoardingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProgressBarAnimation extends Animation {

    private Context context;
    private ProgressBar progressBar ;
    private TextView tvLoadingCounter;
    private float from, to ;
    private FirebaseAuth mAuth;



    public ProgressBarAnimation(Context context, ProgressBar progressBar, TextView tvLoadingCounter, float from, float to) {
        this.context = context;
        this.progressBar = progressBar;
        this.tvLoadingCounter = tvLoadingCounter;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime ;
        mAuth = FirebaseAuth.getInstance();
        progressBar.setProgress((int)value);
        tvLoadingCounter.setText((int) value  + "%");
        if ( value == to ) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null) {
                //Transition to next Activity
                context.startActivity(new Intent(context, HomeActivity.class));
            }else{
                context.startActivity(new Intent(context, OnBoardingActivity.class));
            }
        }
    }

}
