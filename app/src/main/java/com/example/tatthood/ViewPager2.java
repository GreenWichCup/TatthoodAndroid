package com.example.tatthood;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ViewPager2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}