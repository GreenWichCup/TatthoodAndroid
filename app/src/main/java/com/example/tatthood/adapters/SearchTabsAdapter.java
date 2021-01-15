package com.example.tatthood.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tatthood.Fragments.SearchShop;
import com.example.tatthood.Fragments.SearchTattoo;
import com.example.tatthood.Fragments.SearchUsers;

public class SearchTabsAdapter extends FragmentStateAdapter {
    public SearchTabsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SearchUsers();
            case 1:
                return new SearchTattoo();
            case 2:
                return new SearchShop();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
