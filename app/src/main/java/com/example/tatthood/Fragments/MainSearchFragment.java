package com.example.tatthood.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tatthood.R;
import com.example.tatthood.adapters.SearchTabsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainSearchFragment extends Fragment {

    EditText searchInput ;

    public MainSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_main_search, container, false);
        searchInput = view.findViewById(R.id.search_bar_edt);

        ViewPager2 viewPager2 = view.findViewById(R.id.vp2Search);

        viewPager2.setAdapter(new SearchTabsAdapter(getActivity()));

        TabLayout tabLayout = view.findViewById(R.id.tabLayoutSearch);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 0: {
                    tab.setIcon(R.drawable.ic_account24);
                    break;
                }
                case 1: {
                    tab.setIcon(R.drawable.ic_tattoo_search);
                    break;
                }
                case 2: {
                    tab.setIcon(R.drawable.store);
                    break;
                }
                default:
                    break;
            }
        });
        tabLayoutMediator.attach();


        // Inflate the layout for this fragment
return view ;    }
}