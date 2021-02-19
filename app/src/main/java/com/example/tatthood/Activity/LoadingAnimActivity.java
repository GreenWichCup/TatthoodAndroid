package com.example.tatthood.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tatthood.Animation.ProgressBarAnimation;
import com.example.tatthood.R;

public class LoadingAnimActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView tv ;
    VideoView video ;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_app);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.progress_bar);
        tv = findViewById(R.id.loading_counter);
        video = findViewById(R.id.videoView);

        uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_sample);
        video.setVideoURI(uri);
        video.start();

    }

    public void progressBarAnimation(){
        ProgressBarAnimation anim = new ProgressBarAnimation(this, progressBar,tv,0f,100f );
        anim.setDuration(5500);
        progressBar.setAnimation(anim);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setMax(100);
        progressBar.setScaleY(10f);
        progressBarAnimation();
    }

}