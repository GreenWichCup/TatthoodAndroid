package com.example.tatthood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tatthood.adapters.ObFragAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class OnBoardingActivity extends AppCompatActivity implements View.OnClickListener {

    ObFragAdapter fragAdapter;
    LinearLayout obLayoutIndicator ;
    MaterialButton btnObAction,btnPrevious;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_on_boarding);


        final ViewPager2 vp2OnboardingItems = findViewById(R.id.vp2onBoarding);

        fragAdapter = new ObFragAdapter(this);

        vp2OnboardingItems.setAdapter(fragAdapter);
        mAuth = FirebaseAuth.getInstance();

        obLayoutIndicator = findViewById(R.id.obIndicator);
         btnObAction = findViewById(R.id.btnObAction);
        btnPrevious = findViewById(R.id.btnPrevious);

        btnObAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vp2OnboardingItems.getCurrentItem() + 1 <  fragAdapter.getItemCount() ) {
                    vp2OnboardingItems.setCurrentItem(vp2OnboardingItems.getCurrentItem()+1);
                    btnPrevious.setVisibility(View.VISIBLE);
                }  else {
                        startActivity(new Intent(OnBoardingActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }

                }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vp2OnboardingItems.getCurrentItem() - 1 == 0 ) {
                    vp2OnboardingItems.setCurrentItem(vp2OnboardingItems.getCurrentItem()-1);
                    btnPrevious.setVisibility(View.GONE);
                } else {
                    vp2OnboardingItems.setCurrentItem(vp2OnboardingItems.getCurrentItem()-1);
                }
            }
        });

    }

    private void setupOnboardingIndicator(){
        ImageView[] indicators = new ImageView[fragAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8,0,8,0);
        for (int i = 0; i<indicators.length; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ob_indicator_inactive));

            indicators[i].setLayoutParams(layoutParams);
            obLayoutIndicator.addView(indicators[i]);
        }
    }
    private void setCurrentOnboardingIndicator(int index){
        int childCount = obLayoutIndicator.getChildCount();
        for(int i=0; i<childCount;i++){
            ImageView imageView = (ImageView)obLayoutIndicator.getChildAt(i);
            if (i == index ) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ob_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ob_indicator_inactive));
            }
        }
        if (index + 1 == fragAdapter.getItemCount() ) {
            btnObAction.setText("Start");
        } else {
            btnObAction.setText("Next");
        }
    }

    @Override
    public void onClick(View v) {

    }
}