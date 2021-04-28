package com.example.tatthood.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tatthood.OnBoarding.SignUpFormFragment;
import com.example.tatthood.OnBoarding.StatusFragment;
import com.example.tatthood.OnBoarding.WelcomeFragment;
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
        fm.beginTransaction().add(R.id.fragment_container_start_app,new WelcomeFragment(),"WelcomeFragment").addToBackStack(null).commit();
        Log.i(TAG, "****Backstackentry begins with: "+ fm.getBackStackEntryCount());

        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.i(TAG, "****Backstackentry changed now count equals: "+ fm.getBackStackEntryCount());
                switch (fm.getBackStackEntryCount()){
                    case 1:
                        btnPrevious.setVisibility(View.GONE);
                        btnNext.setVisibility(View.GONE);
                        break;
                    case 2:
                       btnPrevious.setVisibility(View.VISIBLE);
                       btnNext.setVisibility(View.VISIBLE);
                       break;
                    case 3:
                        btnPrevious.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.GONE);

                        break;

                }
            }
        });
        Log.i(TAG, "**Initial BackstackentryCount: "+ fm.getBackStackEntryCount());

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
            case 1:
                fTag = "StatusFragment";
                fragment = new StatusFragment(); break;

            case 2:
                fTag = "SignUpFormFragment";
                fragment = new SignUpFormFragment(); break;

            case 0:
                fTag = "WelcomeFragment";
                fragment = new WelcomeFragment();
                break;

        }

        FragmentTransaction fmTransaction = fm.beginTransaction();
        fmTransaction.replace(R.id.fragment_container_start_app, fragment,fTag).addToBackStack(null)
                .commit();

    }




    private void previousFragment(){
        getSupportFragmentManager().popBackStack();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fm.getBackStackEntryCount() == 0){
            finishAffinity();
        } else {

        }
    }
}