package com.example.tatthood.OnBoarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tatthood.Activity.SignIn;
import com.example.tatthood.Activity.StartAppActivity;
import com.example.tatthood.R;
import com.google.android.material.button.MaterialButton;

public class OnBoardingActivity extends AppCompatActivity {

    private MaterialButton btnSignUp;
    private MaterialButton btn_log_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        btnSignUp = findViewById(R.id.sign_up);
        btn_log_in = findViewById(R.id.log_in);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OnBoardingActivity.this, StartAppActivity.class));
          finish();
            }
        });
        btn_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OnBoardingActivity.this, SignIn.class));
            finish();
            }
        });

    }
}