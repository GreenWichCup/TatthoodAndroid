package com.example.tatthood.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tatthood.ModelData.Post;
import com.example.tatthood.R;
import com.example.tatthood.adapters.TestPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class PostDetails extends Fragment  {

    private ViewPager2 vpHorizontal;
    TestPagerAdapter swipePost;
    List<Post> listSwipePost;
    TextView bundleTv;

    public PostDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_post_details, container, false);
        bundleTv = view.findViewById(R.id.toolbar_name);
        vpHorizontal = view.findViewById(R.id.vp2_hz);
        swipePost = new TestPagerAdapter(getContext(),listSwipePost);
        vpHorizontal.setClipToPadding(false);
        vpHorizontal.setClipChildren(false);
        vpHorizontal.setOffscreenPageLimit(3);
        vpHorizontal.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        vpHorizontal.setAdapter(swipePost);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(8));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float v = 1 - Math.abs(position);
                page.setScaleY(0.8f + v*0.2f);
            }
        });

       // readPost();

        return view ;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Log.d("my bundle", "userId: " + bundle);
        }
    }

    public void readPost(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listSwipePost.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                            listSwipePost.add(post);
                            Log.i("post", post.getPublisher().toLowerCase());

                }
                swipePost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
