package com.example.tatthood;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class Dashboard extends AppCompatActivity {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

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
        recreate();
    }
    public void ClickAboutUs(View view){
        NavigationDrawerLayout.redirectActivity(this,AboutUs.class);
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