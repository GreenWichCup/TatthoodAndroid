package com.example.tatthood.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tatthood.Fragments.Gallery;
import com.example.tatthood.Fragments.Home;
import com.example.tatthood.Fragments.Market;
import com.example.tatthood.Fragments.Notifications;
import com.example.tatthood.Fragments.Profile;
import com.example.tatthood.Fragments.Search;

public class TabAdapter extends FragmentStateAdapter {
    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Home();
            case 1:
                return new Search();
            case 2:
                return new Market();
            case 3:
                return new Gallery();
            case 4:
                return new Notifications();
            default:
                return new Profile ();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}