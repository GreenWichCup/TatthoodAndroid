package com.example.tatthood.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.Interfaces.RecyclerViewClickInterface;
import com.example.tatthood.ModelData.Post;
import com.example.tatthood.R;
import com.example.tatthood.adapters.PhotoGridAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Gallery extends Fragment implements RecyclerViewClickInterface {

    PhotoGridAdapter personalTattooGridAdapter;
    RecyclerView recyclerView_personal_tattoo;
    private List<Post> mPersonal;
    List<Post> personalPostList;
    String profileId;
    FirebaseUser firebaseUser;

    public Gallery() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_gallery, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("id","none");

        recyclerView_personal_tattoo = view.findViewById(R.id.recycler_view_personal_tattoo);
        recyclerView_personal_tattoo.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerTwo = new GridLayoutManager(getContext(),3);
        recyclerView_personal_tattoo.setLayoutManager(linearLayoutManagerTwo);
        personalPostList = new ArrayList<>();
        personalTattooGridAdapter = new PhotoGridAdapter(getContext(),personalPostList, this);
        recyclerView_personal_tattoo.setAdapter(personalTattooGridAdapter);
        // Inflate the layout for this fragment

        mPersonalTattooPost();

        return view ;
    }

    private void mPersonalTattooPost(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                personalPostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId) && dataSnapshot.child("personal_tattoo").exists()){
                        Log.d(TAG, "data found for condition:");
                        personalPostList.add(post);
                    }
                }
                Collections.reverse(personalPostList);
                personalTattooGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPersonalTattooPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                personalPostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post savedPost = dataSnapshot.getValue(Post.class);
                    for (Post id : mPersonal ){
                        if (savedPost.getPostid().equals(id)){
                            personalPostList.add(savedPost);
                        }
                    }
                }
                Collections.reverse(personalPostList);
                personalTattooGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onLongClick(int position) {

    }
}