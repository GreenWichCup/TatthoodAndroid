package com.example.tatthood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;
    private Button btnSignIn;
    private TextView createAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        mAuth = FirebaseAuth.getInstance();
        btnSignIn = findViewById(R.id.btnSignIn);
        createAccount = findViewById(R.id.sign_up_link);
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
    }
    private void signIn() {
        if(editTextEmail.getText().toString().equals("")||editTextPassword.getText().toString().equals("")) {
            Toast.makeText(SignIn.this, "All fields are required to sign up",
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("signing In log", "Sign In:success");
                        Toast.makeText(SignIn.this, "Welcome user .",
                                Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        transitionToHomeActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("signIn log", "Sign In user:failure", task.getException());
                        Toast.makeText(SignIn.this, "Welcome failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void transitionToHomeActivity(){
        // organize routing home / social media /etc
        Intent toHomePage = new Intent(this,App_Main_Page.class );
        startActivity(toHomePage);
    }
    private void toCreateAccount(){
        Intent toSignUpActivity = new Intent(this, SignUp.class);
        // organize routing home / social media /etc
        startActivity(toSignUpActivity);
    }
}