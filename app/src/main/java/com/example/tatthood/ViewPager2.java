package com.example.tatthood;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ViewPager2 extends AppCompatActivity {
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawerLayout = findViewById(R.id.drawerLayout);

        setContentView(R.layout.activity_view_pager2);
        androidx.viewpager2.widget.ViewPager2 viewPager2 = findViewById(R.id.viewPager2);
        viewPager2.setAdapter(new TabAdapter(this));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: {
                        tab.setText("Message");
                        tab.setIcon(R.drawable.message);
                        BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.Accent));
                        badgeDrawable.setVisible(true);
                        badgeDrawable.setNumber(1000);
                        badgeDrawable.setMaxCharacterCount(3);
                        break;
                    }
                    case 1: {
                        tab.setText("Picture");
                        tab.setIcon(R.drawable.pictures);
                        break;
                    }
                    default: {
                        tab.setText("Profile");
                        tab.setIcon(R.drawable.profile);
                        BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.Accent));
                        badgeDrawable.setVisible(true);
                        badgeDrawable.setNumber(1000);
                        badgeDrawable.setMaxCharacterCount(3);
                        break;
                    }
                }
            }
        });
        tabLayoutMediator.attach();

    //end of "Oncreate"
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