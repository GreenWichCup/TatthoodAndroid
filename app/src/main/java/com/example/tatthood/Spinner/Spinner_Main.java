package com.example.tatthood.Spinner;

import android.os.Bundle;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tatthood.R;

public class Spinner_Main extends AppCompatActivity {

    private Spinner spinner ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spinner = findViewById(R.id.spinner);
    }
}
