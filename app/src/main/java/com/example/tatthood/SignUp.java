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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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
        String str_username = editTextUsername.getText().toString();
        String str_email = editTextEmail.getText().toString();
        String str_password = editTextPassword.getText().toString();


        if(str_email.equals("")||str_password.equals("")||str_username.equals("")) {
            Toast.makeText(SignUp.this, "All fields are required to sign up",
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(str_email, str_password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(SignUp.this, "Signing up successful.",
                                Toast.LENGTH_SHORT).show();

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("App_users").child(userId);
                        HashMap<String,Object>hashMap =new HashMap<>();
                        hashMap.put("id",userId);
                        hashMap.put("username",str_username.toLowerCase());
                        hashMap.put("email",str_email);
                        hashMap.put("imageUrl","");
                        hashMap.put("bio","");
                        //Later get value from sign up from  (spinner)
                        hashMap.put("status","");

                        mDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Intent intent = new Intent(SignUp.this,App_Main_Page.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        });

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

}