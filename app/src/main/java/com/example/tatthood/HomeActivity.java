package com.example.tatthood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tatthood.Fragments.Home;
import com.example.tatthood.Fragments.Messages;
import com.example.tatthood.Fragments.Profile;
import com.example.tatthood.Fragments.Search;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //set home as default fragment

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Home()).commit();


        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bn_home:
                    selectFragment = new Home();
                    break;

                case R.id.bn_search:
                    selectFragment = new Search();
                  break;

                case R.id.bn_messages:
                    selectFragment = new Messages();
                    break;

                case R.id.bn_profile:
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectFragment = new Profile();
                    break;

                case R.id.bn_add_photo:
                    selectFragment = null;
                    startActivity(new Intent(HomeActivity.this,PostActivity.class));
                    break;
            }
            if(selectFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectFragment).commit();
            }

            return true;
        }
    };


}