package com.example.tatthood;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tatthood.Modules.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Splash_Animation extends AppCompatActivity {

    // initialize variables

    StorageReference gStorageRef;

    ImageView splashInk, splashNeedle, splashWall, drawAnime;
    TextView splashText;
    CharSequence charSequence;
    int index;
    long delay = 200 ;

    // Deprecated
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__animation);

        gStorageRef =  FirebaseStorage.getInstance().getReference();

        //Assign variables
        splashInk = findViewById(R.id.splash_ink);
        splashNeedle = findViewById(R.id.needle);
        splashWall = findViewById(R.id.splash_wall);
        drawAnime = findViewById(R.id.draw_anime);
        splashText = findViewById(R.id.text_splash);

        // deprecated windowmanager.layoutparams.flag_fullscreen deprecated
        getWindow().setFlags(android.R.style.Theme_Light_NoTitleBar_Fullscreen,android.R.style.Theme_Light_NoTitleBar_Fullscreen);

        //initialize top animation

        Animation animationTop = AnimationUtils.loadAnimation(this, R.anim.top_wave );

        //Start Animation
        splashInk.setAnimation(animationTop);

        //initialize object animator
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(splashNeedle,
                PropertyValuesHolder.ofFloat("scaleX",1.2f),
                PropertyValuesHolder.ofFloat("scaleY",1.2f));
        objectAnimator.setDuration(500);

        //set repeat count
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);

        //start animator
        objectAnimator.start();

        // Set Animate text
        animeText("TattHood");

        //Initialize bottom animation
        Animation animationBottom = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);


        splashWall.setAnimation(animationBottom);


        //initialize Handler
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash_Animation.this,SignUp.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            //finish Activity
                finish();
            }
        },4000);

        //other method without glide or picasso library
        StorageReference gifImage = gStorageRef.child("images/giphy_needle.gif");
        GlideApp.with(this).load(gifImage).into(drawAnime);

        /*
        try {
            final File localFile = File.createTempFile("needle","gif");
            gStorageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(Splash_Animation.this,"Picture retrieved",Toast.LENGTH_SHORT).show();
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            ((ImageView)findViewById(R.id.draw_anime)).setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Splash_Animation.this,"You loose ahahahah!!!",Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } */


    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //When runnable
            //Set text
            splashText.setText(charSequence.subSequence(0,index++));
            if(index<charSequence.length()){
                //When charSequence is equal to index
                handler.postDelayed(runnable,delay);
            }
        }
    };

    public void animeText(CharSequence cs){
        //Set text
        charSequence = cs ;
        //Clear index;
        splashText.setText("");
        //remove Call
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,delay);
    }


}