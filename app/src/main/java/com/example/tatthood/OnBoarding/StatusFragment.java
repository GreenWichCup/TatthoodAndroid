package com.example.tatthood.OnBoarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tatthood.R;
import com.example.tatthood.ViewModel.SearchViewModel;

public class StatusFragment extends Fragment {

    String choiceItem;
    ListView status_lv;
    ListAdapter statusListAdpt;
    SearchViewModel model;

    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        String [] statusChoices = {"Hood","Artist","Tattoued","Virgin skin","Seller"};
        statusListAdpt = new ArrayAdapter<>(getActivity(),R.layout.tv_status_selector_item,R.id.btn_status,statusChoices);
        status_lv = view.findViewById(R.id.list_view_status);
        status_lv.setAdapter(statusListAdpt);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        status_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                choiceItem = parent.getItemAtPosition(position).toString();
                v.setSelected(true);
                model.selectStatus(choiceItem);


            }
        });
}}