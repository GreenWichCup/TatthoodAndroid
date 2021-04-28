package com.example.tatthood.OnBoarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.tatthood.Activity.HomeActivity;
import com.example.tatthood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInFormFragment extends Fragment  {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;
    private Button btnSignIn;
    private TextView createAccount;

    public SignInFormFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in_form, container, false);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        mAuth = FirebaseAuth.getInstance();
        btnSignIn = view.findViewById(R.id.btnSignIn);
        createAccount = view.findViewById(R.id.sign_up_link);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCreateAccount();
            }
        });





        return view;
    }

    private void signIn() {
        if(editTextEmail.getText().toString().equals("")||editTextPassword.getText().toString().equals("")) {
            Toast.makeText(getContext(), "All fields are required to sign up",
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(getContext(), "Welcome user .",
                                Toast.LENGTH_SHORT).show();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("App_users")
                                .child(mAuth.getCurrentUser().getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Intent intent = new Intent(getContext(), HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getContext(), "Welcome failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void toCreateAccount(){
        FragmentTransaction fmTransaction = getParentFragmentManager().beginTransaction();
        fmTransaction.replace(R.id.fragment_container_start_app,new StatusFragment()).addToBackStack(null).commit();
    }

}