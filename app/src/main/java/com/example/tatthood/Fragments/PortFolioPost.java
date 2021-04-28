package com.example.tatthood.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.Activity.TestPagerActivity;
import com.example.tatthood.Interfaces.RecyclerViewClickInterface;
import com.example.tatthood.ModelData.Post;
import com.example.tatthood.R;
import com.example.tatthood.ViewModel.TopSheetViewModel;
import com.example.tatthood.adapters.PhotoGridAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PortFolioPost extends Fragment implements RecyclerViewClickInterface {

    private PhotoGridAdapter photoGridAdapter;
    private RecyclerView recyclerView;
    private List<Post> photoList;
    private String profileId;
    private FirebaseUser firebaseUser;
    TopSheetViewModel mTopSheetViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_port_folio_post, container, false);
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("id","none");

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view_portfolio);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        photoList = new ArrayList<>();
        photoGridAdapter = new PhotoGridAdapter(getContext(),photoList,this);
        recyclerView.setAdapter(photoGridAdapter);
        userPortfolio();
        mTopSheetViewModel = new ViewModelProvider(requireActivity()).get(TopSheetViewModel.class);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTopSheetViewModel.setSheetState(true);
            }
        });
    }

    private void userPortfolio(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                photoList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post userPost = dataSnapshot.getValue(Post.class);
                    if (userPost.getPublisher().equals(profileId)){
                        photoList.add(userPost);
                    }
                }
                Collections.reverse(photoList);
                photoGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), TestPagerActivity.class);
        intent.putExtra("index",position);
        intent.putExtra("photoList", (Serializable) photoList);
        getActivity().startActivity(intent);
    }

    @Override
    public void onLongClick(int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(photoList.get(position).getPostid());
        reference.removeValue();
        photoGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewClick(View viewClicked) {
    }
}