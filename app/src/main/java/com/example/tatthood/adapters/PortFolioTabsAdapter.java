package com.example.tatthood.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tatthood.Fragments.PortFolioPost;
import com.example.tatthood.Fragments.SavedPost;

public class PortFolioTabsAdapter extends FragmentStateAdapter {
    public PortFolioTabsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PortFolioPost();
            case 1:
                return new SavedPost();
            default:
                return null;
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}