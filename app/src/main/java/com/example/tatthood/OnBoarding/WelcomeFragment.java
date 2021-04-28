package com.example.tatthood.OnBoarding;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tatthood.R;

import static android.content.ContentValues.TAG;

// no use anymore
public class WelcomeFragment extends Fragment implements View.OnClickListener {

    Button btnSignUp;
    Button btn_log_in;
    FragmentManager fm;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        btnSignUp = view.findViewById(R.id.sign_up);
        btn_log_in = view.findViewById(R.id.log_in);

        btnSignUp.setOnClickListener(this);
        btn_log_in.setOnClickListener(this);
        // Inflate the layout for this fragment

        return view;
    }

    @Override
    public void onClick(View v) {
        fm = getParentFragmentManager();
        FrameLayout fl = getActivity().findViewById(R.id.fragment_container_start_app);
        if (v == btnSignUp){
            FragmentTransaction fmTransaction = getParentFragmentManager().beginTransaction();
            fmTransaction.replace(R.id.fragment_container_start_app,new StatusFragment(),"StatusFragment").addToBackStack(null).commit();
        }
        if (v == btn_log_in){
            FragmentTransaction fmTransaction = getParentFragmentManager().beginTransaction();
            fmTransaction.replace(R.id.fragment_container_start_app,new SignInFormFragment(),"SignInFormFragment").commit();
        }
        Log.i(TAG, "****Backstackentry from welcome fragment: "+ fm.getBackStackEntryCount());


    }
}