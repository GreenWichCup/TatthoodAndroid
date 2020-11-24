package com.example.tatthood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;
    EditText editTextUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        Button btnSignIn = findViewById(R.id.btnSignIn);

        mAuth = FirebaseAuth.getInstance();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            //Transition to next Activity
          transitionToHomeActivity();
        }
    }

    private void signUp() {

        if(editTextEmail.getText().toString().equals("")||editTextPassword.getText().toString().equals("")||editTextUsername.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "All fields are required to sign up",
                    Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Signing up" + editTextUsername.getText().toString());
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("signUp log", "createUserWithEmail:success");
                        Toast.makeText(MainActivity.this, "Authentication successful.",
                                Toast.LENGTH_SHORT).show();

//what the meaning of this here => FirebaseUser user = mAuth.getCurrentUser();

                        transitionToHomeActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("signUp log", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void signIn() {
        if(editTextEmail.getText().toString().equals("")||editTextPassword.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "All fields are required to sign up",
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("signing In log", "Sign In:success");
                        Toast.makeText(MainActivity.this, "Welcome user .",
                                Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        transitionToHomeActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("signIn log", "Sign In user:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Welcome failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
   private void transitionToHomeActivity(){
        // organize routing home / social media /etc
        Intent toHomePage = new Intent(this,HomeActivity.class );
        startActivity(toHomePage);
    }
}