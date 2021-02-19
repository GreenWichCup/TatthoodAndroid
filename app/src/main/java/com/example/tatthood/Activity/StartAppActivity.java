package com.example.tatthood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tatthood.OnBoarding.OnBoardingActivity;
import com.example.tatthood.OnBoarding.OverviewFragment;
import com.example.tatthood.OnBoarding.SignUpFormFragment;
import com.example.tatthood.OnBoarding.StatusFragment;
import com.example.tatthood.R;

public class StartAppActivity extends AppCompatActivity {

    FragmentManager fm;
    Fragment fragment;
    Button btnNext,btnPrevious;

    private static final String COMMON_TAG = "CombinedLifeCycle";
    private static final String ACTIVITY_NAME = StartAppActivity.class.getSimpleName();
    private static final String TAG = COMMON_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);

        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container_start_app,new StatusFragment(),"StatusFragment").addToBackStack(null).commit();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.i(TAG, "****Backstackentry changed now count equals: "+ fm.getBackStackEntryCount());
                int index = fm.getBackStackEntryCount();
                if (fm.getBackStackEntryCount()<1){
                    startActivity(new Intent(StartAppActivity.this, OnBoardingActivity.class));
                }
                if (fm.getBackStackEntryCount()>1){
                    btnNext.setVisibility(View.GONE);
                }
                if (fm.getBackStackEntryCount()<=1){
                    btnNext.setText("Next");
                    btnNext.setVisibility(View.VISIBLE);
                }
                if (fm.getBackStackEntryCount()>2){
                    btnPrevious.setVisibility(View.GONE);
                }else {
                    btnPrevious.setVisibility(View.VISIBLE);
                }
            }
        });
        Log.i(TAG, "**Initial BackstackentryCount: "+ fm.getBackStackEntryCount());
        btnNext.setVisibility(View.VISIBLE);
        btnPrevious.setVisibility(View.VISIBLE);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment();
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousFragment();
            }
        });
    }

    private void addFragment(){
        String fTag = "";
        switch (fm.getBackStackEntryCount()){
            case 0:
                fTag = "StatusFragment";
                fragment = new StatusFragment(); break;
            case 1:
                fTag = "SignUpFormFragment";
                fragment = new SignUpFormFragment(); break;
            case 2:
                fTag = "OverviewFragment";
                fragment = new OverviewFragment();break;
            default:

                break;
        }

        FragmentTransaction fmTransaction = fm.beginTransaction();
        fmTransaction.replace(R.id.fragment_container_start_app, fragment,fTag).addToBackStack(null)
                .commit();

    }

    private void previousFragment(){
        getSupportFragmentManager().popBackStack();
    }

    public void replaceFragment(){
        OverviewFragment mFrag = new OverviewFragment();
        fm.beginTransaction().replace(R.id.fragment_container_start_app, mFrag,"OverviewFragment").addToBackStack(null).commit();
    }

}