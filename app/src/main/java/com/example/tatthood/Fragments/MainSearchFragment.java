package com.example.tatthood.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tatthood.R;
import com.example.tatthood.ViewModel.SearchViewModel;
import com.example.tatthood.adapters.SearchTabsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MainSearchFragment extends Fragment {

    EditText searchInput;
    SearchViewModel model;
    ImageView cancelSearch;
    Toolbar toolbar;


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
        ViewPager2 viewPager2 = view.findViewById(R.id.vp2Search);
        viewPager2.setAdapter(new SearchTabsAdapter(getActivity()));
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutSearch);
        cancelSearch = view.findViewById(R.id.cancel);
        toolbar = view.findViewById(R.id.app_bar);
        searchInput = view.findViewById(R.id.search_bar_edt);


        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setText("");
                cancelSearch.setVisibility(View.GONE);
                hideSoftKeyboard();
                searchInput.clearFocus();
            }
        });

        searchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    cancelSearch.setVisibility(View.VISIBLE);
                }
            }
        });

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    searchInput.clearFocus();

                }
                return false;
            }
        });

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
        return view ;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);


        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                model.selectStatus(s.toString());
            }
        });
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

    }

}