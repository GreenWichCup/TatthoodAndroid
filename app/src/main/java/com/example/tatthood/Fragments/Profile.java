package com.example.tatthood.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.ModelData.Post;
import com.example.tatthood.ModelData.User;
import com.example.tatthood.Modules.GlideApp;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment {

    private TextView nameTv,user_status ,toolbarNameTv, userBioTv, followersCountTv, followingCountTv, postsCountTv, editProfile;
    private CircleImageView profileImage;
    private Button btnFollow;
    private LinearLayout layoutCount;
    String profileId;
    ImageButton gridPortfolio, gridSavedPost;
    FirebaseUser firebaseUser;

    PhotoGridAdapter photoGridAdapter;
    RecyclerView recyclerView;
    List<Post> photoList;

    private  List<String> mSaved;
    PhotoGridAdapter savedPostGridAdapter;
    RecyclerView saved_post_recyclerView;
    List<Post> savedPostList;


    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("id","none");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        nameTv = view.findViewById(R.id.username);
        userBioTv = view.findViewById(R.id.user_bio);
        editProfile = view.findViewById(R.id.edit_profile);
        followersCountTv = view.findViewById(R.id.followersCount);
        followingCountTv = view.findViewById(R.id.followingCount);
        user_status = view.findViewById(R.id.user_status);

        layoutCount = view.findViewById(R.id.countLayout);
        postsCountTv = view.findViewById(R.id.postCount);
        toolbarNameTv = view.findViewById(R.id.toolbar_name);
        profileImage = view.findViewById(R.id.image_profile);
        btnFollow = view.findViewById(R.id.btn_follow);

        // portfolio recyclerview

        gridPortfolio = view.findViewById(R.id.grid_portfolio);
        gridSavedPost = view.findViewById(R.id.grid_saved_post);

        recyclerView = view.findViewById(R.id.recycler_view_portfolio);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        photoList = new ArrayList<>();
        photoGridAdapter = new PhotoGridAdapter(getContext(),photoList);
        recyclerView.setAdapter(photoGridAdapter);

        // saved_post recyclerview

        saved_post_recyclerView = view.findViewById(R.id.recycler_view_saved_post);
        saved_post_recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerTwo = new GridLayoutManager(getContext(),3);
        saved_post_recyclerView.setLayoutManager(linearLayoutManagerTwo);
        savedPostList = new ArrayList<>();
        savedPostGridAdapter = new PhotoGridAdapter(getContext(),savedPostList);
        saved_post_recyclerView.setAdapter(savedPostGridAdapter);



        userInfo();
        getFollowers();
        getNrPosts();
        userPortfolio();
        mSavedPost();

        if (profileId.equals(firebaseUser.getUid())){
            editProfile.setText("Edit Profile");
            btnFollow.setVisibility(View.GONE);
        } else {
            checkFollow();
            gridSavedPost.setVisibility(View.GONE);
        }


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = editProfile.getText().toString();
                if (btn.equals("Edit profile")){
                    //go to edit profile
                } else if (btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(firebaseUser.getUid())
                            .child("following")
                            .child(profileId).setValue(true);

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(profileId)
                            .child("followers")
                            .child(firebaseUser.getUid()).setValue(true);
                } else if (btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(firebaseUser.getUid())
                            .child("following")
                            .child(profileId).removeValue();

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(profileId)
                            .child("followers")
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        gridPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerView.setVisibility(View.VISIBLE);
                saved_post_recyclerView.setVisibility(View.GONE);
            }
        });

        gridSavedPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                saved_post_recyclerView.setVisibility(View.VISIBLE);

            }
        });

        return view ;

    }

    private void userInfo(){
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("App_users").child(profileId);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null){
                    return;
                }
                User user = snapshot.getValue(User.class);
                GlideApp.with(getContext()).load(user.getimageUrl()).into(profileImage);
                nameTv.setText(user.getUsername());
                user_status.setText(user.getStatus());
                userBioTv.setText(user.getBio());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void checkFollow(){
        DatabaseReference followingReference = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("following");
        followingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.child(profileId).exists()) {
                   editProfile.setVisibility(View.GONE);
                   btnFollow.setVisibility(View.GONE);
                   layoutCount.setVisibility(View.VISIBLE);


               } else {
                   editProfile.setText("follow");
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getFollowers(){
        DatabaseReference userFollowers = FirebaseDatabase.getInstance().getReference("Follow").child(profileId).child("followers");
        userFollowers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    followersCountTv.setText(""+ snapshot.getChildrenCount() );
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference userFollowing = FirebaseDatabase.getInstance().getReference("Follow").child(profileId).child("followers");
        userFollowing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingCountTv.setText(""+ snapshot.getChildrenCount() );
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void getNrPosts(){
        DatabaseReference userPost = FirebaseDatabase.getInstance().getReference("Posts");
        userPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0 ;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post posts = dataSnapshot.getValue(Post.class);
                    if (posts.getPublisher().equals(profileId)){
                        i++;
                    }
                }
                postsCountTv.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    private void mSavedPost(){
        mSaved = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saved")
                .child(firebaseUser.getUid());
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
                savedPostGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}