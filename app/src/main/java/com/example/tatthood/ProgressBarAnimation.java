package com.example.tatthood;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tatthood.OnBoarding.OnBoardingActivity;

public class ProgressBarAnimation extends Animation {

    private Context context;
    private ProgressBar progressBar ;
    private TextView tvLoadingCounter;
    private float from, to ;

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
        progressBar.setProgress((int)value);
        tvLoadingCounter.setText((int) value  + "%");
        if ( value == to ) {
            context.startActivity(new Intent(context, OnBoardingActivity.class));
        }
    }
}
