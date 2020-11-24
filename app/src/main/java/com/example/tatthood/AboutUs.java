package com.example.tatthood;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class AboutUs extends AppCompatActivity {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        drawerLayout = findViewById(R.id.drawerLayout);

    }

    public void ClickMenu(View view){
        NavigationDrawerLayout.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view){
        //Close drawer
        NavigationDrawerLayout.closeDrawer(drawerLayout);
    }
    public void ClickHome(View view){
        NavigationDrawerLayout.redirectActivity(this,NavigationDrawerLayout.class);
    }
    public void ClickDashboard(View view){
        NavigationDrawerLayout.redirectActivity(this,Dashboard.class);
    }
    public void ClickAboutUs(View view){
        recreate();
    }
    public void ClickLogout(View view){
        NavigationDrawerLayout.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NavigationDrawerLayout.closeDrawer(drawerLayout);
    }


}