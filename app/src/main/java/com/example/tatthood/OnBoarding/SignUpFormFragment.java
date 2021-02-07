package com.example.tatthood.OnBoarding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tatthood.App_Main_Page;
import com.example.tatthood.HomeActivity;
import com.example.tatthood.R;
import com.example.tatthood.SignIn;
import com.example.tatthood.ViewModel.SearchViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SignUpFormFragment extends Fragment implements View.OnKeyListener, View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;
    private EditText editTextUsername;
    private DatabaseReference mDatabase;
    private TextView signInLink;
    private TextView tv_status_choosed;
    LinearLayout linFormLayout;
    ImageView image_profile;
    Button btnSignUp;
    Boolean modeSignUp;

    public SignUpFormFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up_form, container, false);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        tv_status_choosed = view.findViewById(R.id.statusChoosed);

        linFormLayout = view.findViewById(R.id.formLayout);
        image_profile = view.findViewById(R.id.image_profile);

        btnSignUp = view.findViewById(R.id.btnSignUp);
        signInLink= view.findViewById(R.id.log_in_link);
        mAuth = FirebaseAuth.getInstance();

        editTextPassword.setOnKeyListener(this);
        editTextUsername.setOnKeyListener(this);
        editTextEmail.setOnKeyListener(this);
        linFormLayout.setOnClickListener(this);
        image_profile.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        signInLink.setOnClickListener(this);


        return view ;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchViewModel model = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        model.getSelectedStatus().observe(getViewLifecycleOwner(), item -> {
           tv_status_choosed.setText(item);
        });
    }

    private void signUp() {
        String str_username = editTextUsername.getText().toString();
        String str_email = editTextEmail.getText().toString();
        String str_password = editTextPassword.getText().toString();
        String str_status = tv_status_choosed.getText().toString();

        if (str_email.equals("") || str_password.equals("") || str_username.equals("")) {
            Toast.makeText(getContext(), "All fields are required to sign up",
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(str_email, str_password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(getContext(), "Signing up successful.",
                                Toast.LENGTH_SHORT).show();

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("App_users").child(userId);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userId);
                        hashMap.put("username", str_username);
                        hashMap.put("username_uppercase", str_username.toUpperCase());
                        hashMap.put("email", str_email);
                        hashMap.put("imageUrl", "");
                        hashMap.put("bio", "");
                        //Later get value from sign up from  (spinner)
                        hashMap.put("status", str_status);

                        mDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        });

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("signUp log", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            });
        } }



    private void toSignIn(){
        Intent toSignInActivity = new Intent(getActivity(), SignIn.class );
        // organize routing home / social media /etc
        startActivity(toSignInActivity);
    }

    private void transitionToHomeActivity(){
        Intent toSignInActivity = new Intent(getActivity(), App_Main_Page.class );
        // organize routing home / social media /etc
        startActivity(toSignInActivity);
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            signUp(); 
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignUp){
            signUp();
        } else if (v.getId() == R.id.log_in_link) {
            toSignIn();
        } else if(v.getId() == R.id.image_profile || v.getId() == R.id.formLayout) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
        }
    }
}