package com.example.tatthood.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tatthood.OnBoarding.OverviewFragment;
import com.example.tatthood.OnBoarding.SignUpFormFragment;
import com.example.tatthood.OnBoarding.StatusFragment;
import com.example.tatthood.OnBoarding.WelcomeFragment;

public class ObFragAdapter extends FragmentStateAdapter {


    String status;
    public ObFragAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WelcomeFragment();
            case 1:
                return new StatusFragment();
            case 2:
                return new SignUpFormFragment();
            case 3:
                return new OverviewFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
