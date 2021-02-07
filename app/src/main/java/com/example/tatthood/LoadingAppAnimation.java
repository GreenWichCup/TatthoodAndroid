package com.example.tatthood;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingAppAnimation extends AppCompatActivity {

    ProgressBar progressBar;
    TextView tv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_app);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.progress_bar);
        tv = findViewById(R.id.loading_counter);

        progressBar.setMax(100);
        progressBar.setScaleY(3f);

        progressBarAnimation();

    }

    public void progressBarAnimation(){
        ProgressBarAnimation anim = new ProgressBarAnimation(this, progressBar,tv,0f,100f );
        anim.setDuration(8000);
        progressBar.setAnimation(anim);

    }
}