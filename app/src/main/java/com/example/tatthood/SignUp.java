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

import com.example.tatthood.UsersData.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;
    private EditText editTextUsername;
    private DatabaseReference mDatabase;
    private TextView signInLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        signInLink= findViewById(R.id.log_in_link);
        mAuth = FirebaseAuth.getInstance();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSignIn();
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
            Toast.makeText(SignUp.this, "All fields are required to sign up",
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("signUp log", "createUserWithEmail:success");
                        Toast.makeText(SignUp.this, "Authentication successful.",
                                Toast.LENGTH_SHORT).show();

//what the meaning of this here => FirebaseUser user = mAuth.getCurrentUser();
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        writeNewUser(task.getResult().getUser().getUid(),editTextEmail.getText().toString(),editTextUsername.getText().toString());
                        transitionToHomeActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("signUp log", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUp.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void writeNewUser(String userId, String username, String email) {
        User user = new User(userId, username, email);
        mDatabase.child("TattHood_users").child(userId).setValue(user);
    }

    private void transitionToHomeActivity(){
        Intent toHomePage = new Intent(this, App_Main_Page.class );
        // organize routing home / social media /etc
        startActivity(toHomePage);
    }
    private void toSignIn(){
        Intent toSignInActivity = new Intent(this, SignIn.class );
        // organize routing home / social media /etc
        startActivity(toSignInActivity);
    }

}