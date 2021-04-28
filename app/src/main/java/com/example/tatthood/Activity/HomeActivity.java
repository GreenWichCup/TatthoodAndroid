package com.example.tatthood.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.tatthood.Fragments.Home;
import com.example.tatthood.Fragments.HoodMap;
import com.example.tatthood.Fragments.MainSearchFragment;
import com.example.tatthood.Fragments.Profile;
import com.example.tatthood.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectFragment = null;
    Toolbar toolbar;

    private ImageView post_photo;
    private ImageView logOut;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        Date date = new Date();
        long dateMilli = date.getTime();
        System.out.println("Time in milli: " + dateMilli);

        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //set home as default fragment

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Home()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        logOut = findViewById(R.id.log_out);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                Intent intent = new Intent(HomeActivity.this, SignIn.class);
                startActivity(intent);
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String fTag = "";
            switch (item.getItemId()) {
                case R.id.bn_home:
                    fTag = "home";
                    toolbar.setVisibility(View.VISIBLE);
                    selectFragment = new Home();
                    break;

                case R.id.bn_search:
                    fTag = "mainsearch";
                    toolbar.setVisibility(View.GONE);
                    selectFragment = new MainSearchFragment();
                  break;

                case R.id.bn_messages:
                    fTag = "hoodmap";
                    toolbar.setVisibility(View.GONE);
                    selectFragment = new HoodMap();
                    break;

                case R.id.bn_profile:
                    fTag = "profile";
                    toolbar.setVisibility(View.GONE);
                    SharedPreferences.Editor editor = getSharedPreferences(getPackageName()+"PREFS_UserProfile", MODE_PRIVATE).edit();
                    editor.putString("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.commit();
                    selectFragment = new Profile();
                    break;

                case R.id.bn_add_photo:
                    selectFragment = null;
                    startActivity(new Intent(HomeActivity.this,PostActivity.class));
                    break;
            }
            if(selectFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectFragment,fTag).commit();
            }

            return true;
        }
    };

    private void logout() {
        mAuth.signOut();
        finish();

    }

}