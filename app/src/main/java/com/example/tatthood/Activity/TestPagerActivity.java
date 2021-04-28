package com.example.tatthood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tatthood.ModelData.Post;
import com.example.tatthood.R;
import com.example.tatthood.adapters.TestPagerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TestPagerActivity extends AppCompatActivity implements TestPagerAdapter.IpagerAdapter {
    ViewPager2 vpHorizontal;
    TestPagerAdapter swipePost;
    List<Post> listSwipePost;
    int currentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pager);
        listSwipePost= (List<Post>) getIntent().getSerializableExtra("photoList");
        currentPost = (int) getIntent().getSerializableExtra("index") ;
        vpHorizontal = findViewById(R.id.vp2_hz);
        swipePost = new TestPagerAdapter(this, listSwipePost,this);
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
        vpHorizontal.setPageTransformer(transformer);
        vpHorizontal.setCurrentItem(currentPost,false);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void goBackToProfile(String post_id,String post_image,int position) {
        beginDelete(post_id,post_image,position);
    }

    @Override
    public void editPost(String post_id, String post_url, String description
    ) {
        Intent intent = new Intent(TestPagerActivity.this, PostActivity.class);
        intent.putExtra("key","editPost");
        intent.putExtra("editPostId",post_id);
        intent.putExtra("post_url", post_url );
        intent.putExtra("postDescription", description);
        startActivity(intent);
        ;
       ;
    }

    private void beginDelete(String post_id,String post_image,int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(post_id);
        reference.removeValue();
        swipePost.removeItem(position);
        swipePost.notifyDataSetChanged();
        swipePost.notifyItemRemoved(position);

    }
    public void startPostEdition(String post_id, String post_url, String description){

    }

}