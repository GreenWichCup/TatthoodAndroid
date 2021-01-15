package com.example.tatthood.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tatthood.Fragments.Home;
import com.example.tatthood.Fragments.HoodMap;
import com.example.tatthood.Fragments.Market;
import com.example.tatthood.Fragments.Profile;
import com.example.tatthood.Fragments.SearchUsers;

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
                return new SearchUsers();
            case 2:
                return new Market();
            case 3:
                return new HoodMap();
            case 4:
                return new Profile();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}