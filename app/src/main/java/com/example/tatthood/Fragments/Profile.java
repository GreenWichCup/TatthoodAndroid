package com.example.tatthood.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tatthood.Activity.EditProfileActivity;
import com.example.tatthood.ModelData.Post;
import com.example.tatthood.ModelData.User;
import com.example.tatthood.Modules.GlideApp;
import com.example.tatthood.R;
import com.example.tatthood.adapters.PortFolioTabsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment  {

    private TextView nameTv,user_status, userHood, followersCountTv, followingCountTv, postsCountTv,locationTv;
    private CircleImageView profileImage;
    private Button editProfile;
    private LinearLayout layoutCount;
    String profileId;
    FirebaseUser firebaseUser;


    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null){
            container.removeAllViews();
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("id","none");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        nameTv = view.findViewById(R.id.username);
        userHood = view.findViewById(R.id.user_bio);
        editProfile = view.findViewById(R.id.edit_profile);
        followersCountTv = view.findViewById(R.id.followersCount);
        followingCountTv = view.findViewById(R.id.followingCount);
        user_status = view.findViewById(R.id.user_status);

        layoutCount = view.findViewById(R.id.countLayout);
        postsCountTv = view.findViewById(R.id.postCount);
        locationTv = view.findViewById(R.id.location);
        profileImage = view.findViewById(R.id.image_profile);

        // Portfolio tabLayout

        ViewPager2 viewPager2 = view.findViewById(R.id.vpProfile);
        viewPager2.setAdapter(new PortFolioTabsAdapter(getActivity()));

        TabLayout tabLayout = view.findViewById(R.id.tabLayoutProfile);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setIcon(R.drawable.ic_grid);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_save_post);
                        break;
                    case 2 :
                        tab.setIcon(R.drawable.ic_personal);
                        break;
                    default:
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
        userInfo();
        getFollowers();
        getNrPosts();

        if (profileId.equals(firebaseUser.getUid())){
            editProfile.setText("Edit Profile");
        } else {
            checkFollow();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = editProfile.getText().toString();
                if (btn.equalsIgnoreCase("Edit profile")){
                    startActivity(new Intent(getContext(),EditProfileActivity.class ));
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
                } else if (btn.equals("Unfollow")){
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

        // later will be replaced by tab layout and viewpager2

        return view ;

    }

    private void userInfo(){
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("App_users").child(profileId);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null){ return; }
                User user = snapshot.getValue(User.class);
                GlideApp.with(getContext()).load(user.getimageUrl()).into(profileImage);
                nameTv.setText(user.getUsername());
                user_status.setText(user.getStatus());
                userHood.setText(user.getHood());
                if (!user.getStatus().equals("Hood")){
                    locationTv.setVisibility(View.GONE);
                } else {
                    locationTv.setText(user.getAddress());
                }
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
                   editProfile.setText("Unfollow");
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

        DatabaseReference userFollowing = FirebaseDatabase.getInstance().getReference("Follow").child(profileId).child("following");
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


}