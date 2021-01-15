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

import com.example.tatthood.ModelData.OnBoardingItems;
import com.example.tatthood.adapters.OnboardingAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    OnboardingAdapter onboardingAdapter;
    LinearLayout obLayoutIndicator ;
    MaterialButton btnObAction;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_on_boarding);
        setupOnboardingItems();
        final ViewPager2 vp2OnboardingItems = findViewById(R.id.vp2onBoarding);
        vp2OnboardingItems.setAdapter(onboardingAdapter);


        mAuth = FirebaseAuth.getInstance();

        obLayoutIndicator = findViewById(R.id.obIndicator);
        btnObAction = findViewById(R.id.btnOnboardingAction);

        setupOnboardingIndicator();
        setCurrentOnboardingIndicator(0);

        vp2OnboardingItems.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });

        btnObAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vp2OnboardingItems.getCurrentItem()+1 <  onboardingAdapter.getItemCount() ) {
                    vp2OnboardingItems.setCurrentItem(vp2OnboardingItems.getCurrentItem()+1);
                } else {
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if(currentUser != null) {
                        //Transition to next Activity
                        startActivity(new Intent(OnBoardingActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        startActivity(new Intent(OnBoardingActivity.this, SignIn.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }            //finish Activity
                    finish();
                }
            }
        });
    }

    private void setupOnboardingItems(){
        List<OnBoardingItems> onBoardingItems = new ArrayList<>();

        OnBoardingItems statusItems =  new OnBoardingItems();
        statusItems.setTitle("List of user user status");
        statusItems.setDescription("There are various type of account in our tattoo application");
        statusItems.setImage(R.drawable.ob_one);

        OnBoardingItems signUpForm =  new OnBoardingItems();
        signUpForm.setTitle("Sign up form");
        signUpForm.setDescription("Fill the form to create your account");
        signUpForm.setImage(R.drawable.ob_two);

        OnBoardingItems appTutorial =  new OnBoardingItems();
        appTutorial.setTitle("application tutorial");
        appTutorial.setDescription("Follow this tutorial to lear how to use the application");
        appTutorial.setImage(R.drawable.ob_tree);

        onBoardingItems.add(statusItems);
        onBoardingItems.add(signUpForm);
        onBoardingItems.add(appTutorial);

        onboardingAdapter = new OnboardingAdapter(onBoardingItems);
    }

    private void setupOnboardingIndicator(){
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
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
        if (index == onboardingAdapter.getItemCount() - 1) {
            btnObAction.setText("Start");
        } else {
            btnObAction.setText("Next");
        }
    }
}