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
import com.google.firebase.auth.FirebaseAuth;
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


public class SavedPost extends Fragment implements RecyclerViewClickInterface {

    PhotoGridAdapter savedPostGridAdapter;
    RecyclerView saved_post_recyclerView;
    private List<String> mSaved;
    List<Post> savedPostList;
    String profileId;
    FirebaseUser firebaseUser;
    TopSheetViewModel mTopSheetViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_post, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("id","none");

        saved_post_recyclerView = view.findViewById(R.id.recycler_view_saved_post);
        saved_post_recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerTwo = new GridLayoutManager(getContext(),3);
        saved_post_recyclerView.setLayoutManager(linearLayoutManagerTwo);
        savedPostList = new ArrayList<>();
        savedPostGridAdapter = new PhotoGridAdapter(getContext(),savedPostList, this);
        saved_post_recyclerView.setAdapter(savedPostGridAdapter);

        return view ;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTopSheetViewModel = new ViewModelProvider(requireActivity()).get(TopSheetViewModel.class);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTopSheetViewModel.setSheetState(true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSavedPost();
    }

    private void mSavedPost(){
        mSaved = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saved")
                .child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    mSaved.add(dataSnapshot.getKey());
                }
                readSavedPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void readSavedPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                savedPostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post savedPost = dataSnapshot.getValue(Post.class);
                    for (String id : mSaved ){
                        if (savedPost.getPostid().equals(id)){
                            savedPostList.add(savedPost);
                        }
                    }
                }
                Collections.reverse(savedPostList);
                savedPostGridAdapter.notifyDataSetChanged();
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
        intent.putExtra("photoList", (Serializable) savedPostList);
        getActivity().startActivity(intent);

    }

    @Override
    public void onLongClick(int position) {

    }

    @Override
    public void onViewClick(View viewClicked) {

    }
}