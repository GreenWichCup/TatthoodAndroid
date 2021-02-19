package com.example.tatthood.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.tatthood.Fragments.Home;
import com.example.tatthood.Fragments.HoodMap;
import com.example.tatthood.Fragments.MainSearchFragment;
import com.example.tatthood.Fragments.Profile;
import com.example.tatthood.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectFragment = null;
    AppBarLayout appBarLayout;


    private ImageView post_photo;
    private ImageView logOut;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        appBarLayout = findViewById(R.id.appBar);

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
            switch (item.getItemId()) {
                case R.id.bn_home:
                    appBarLayout.setVisibility(View.VISIBLE);
                    selectFragment = new Home();
                    break;

                case R.id.bn_search:
                    appBarLayout.setVisibility(View.GONE);
                    selectFragment = new MainSearchFragment();
                  break;

                case R.id.bn_messages:
                    appBarLayout.setVisibility(View.GONE);
                    selectFragment = new HoodMap();
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

    private void logout() {
        mAuth.signOut();
        finish();

    }

}