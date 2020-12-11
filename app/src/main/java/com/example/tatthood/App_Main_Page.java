package com.example.tatthood;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tatthood.adapters.TabAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class App_Main_Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app__main__page);

        ViewPager2 viewPager2 = findViewById(R.id.viewPager2);
        viewPager2.setAdapter(new TabAdapter(this));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: {
                        tab.setIcon(R.drawable.home);
                        break;
                    }
                    case 1: {
                        tab.setIcon(R.drawable.search);
                        break;
                    }
                    case 2: {
                        tab.setIcon(R.drawable.store);
                        break;
                    }
                    case 3: {
                        tab.setIcon(R.drawable.notifications);
                        break;
                    }
                    case 4: {
                        tab.setIcon(R.drawable.profile);
                        break;
                    }
                    default:
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
    }

}