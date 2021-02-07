package com.example.tatthood.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.ModelData.User;
import com.example.tatthood.R;
import com.example.tatthood.ViewModel.SearchViewModel;
import com.example.tatthood.adapters.UserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchUsers extends Fragment {

    RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_search_users, container, false);

        usersRecyclerView = view.findViewById(R.id.search_recycler_view);
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(),mUsers);


        readUsers();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchViewModel model = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        model.getSelectedStatus().observe(getViewLifecycleOwner(), item -> {
            searchUsers(item.toUpperCase());
        });

        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersRecyclerView.setAdapter(userAdapter);
    }

    private void searchUsers(String s){
        Query query = FirebaseDatabase.getInstance().getReference("App_users")
                .orderByChild("username_uppercase")
                .startAt(s.toUpperCase())
                            .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers (){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("App_users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUsers.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        User user = dataSnapshot.getValue(User.class);
                        mUsers.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
