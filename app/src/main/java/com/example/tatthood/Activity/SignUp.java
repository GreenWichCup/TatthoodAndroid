package com.example.tatthood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tatthood.Fragments.TopSheetBehavior;
import com.example.tatthood.Fragments.TopSheetDialog;
import com.example.tatthood.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class SignUp extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;
    private EditText editTextUsername;
    private DatabaseReference mDatabase;
    private TextView signInLink;
    private Spinner statusUser;
    private ImageView imageViewSignUp;
    View sheet;
    LinearLayout linearLayout;
    View rootView;
    TopSheetDialog dialog;
    int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        statusUser = findViewById(R.id.spinner);
        imageViewSignUp = findViewById(R.id.imageViewSignUp);
        sheet = findViewById(R.id.top_sheet);
        linearLayout = findViewById(R.id.formLayout);
        rootView = findViewById(R.id.rootLayout);

        Button btnSignUp = findViewById(R.id.btnSignUp);
        signInLink= findViewById(R.id.log_in_link);
        mAuth = FirebaseAuth.getInstance();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTopSheet();
            }
        });
        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSignIn();
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float height= sheet.getHeight();
                v.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int pointerId = event.getPointerId(0);
                        int pointerIndex = event.findPointerIndex(pointerId);
                        // Get the pointer's current position
                        float x = event.getX(pointerIndex);
                        float y = event.getY(pointerIndex);
                        if (y>height){
                            TopSheetBehavior.from(sheet).setState(TopSheetBehavior.STATE_COLLAPSED);
                            Log.i("onTouchTag", "heightTouched = "+y+ " is higher than " + height);
                        }

                        return false;
                    }
                });
            }
        });
        TopSheetBehavior.from(sheet).setTopSheetCallback(new TopSheetBehavior.TopSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        enableLL(linearLayout);
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        disableLL(linearLayout);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        // to do
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void signUp() {

    }

    private void toSignIn(){
        Intent toSignInActivity = new Intent(this, SignIn.class );
        // organize routing home / social media /etc
        startActivity(toSignInActivity);
    }

    private void transitionToHomeActivity(){
        Intent toSignInActivity = new Intent(this, App_Main_Page.class );
        // organize routing home / social media /etc
        startActivity(toSignInActivity);
    }

    public void openTopSheet() {
        sheet.setBackgroundResource(R.color.black);
        sheet.bringToFront();
        TopSheetBehavior.from(sheet).setState(TopSheetBehavior.STATE_EXPANDED);
    }

    private void disableLL(ViewGroup layout){
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(false);
            if (child instanceof ViewGroup)
                disableLL((ViewGroup) child);
        }
    }

    private void enableLL(ViewGroup layout){
        // can be merged in an one with a boolean
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(true);
            if (child instanceof ViewGroup)
                enableLL((ViewGroup) child);
        }
    }

}