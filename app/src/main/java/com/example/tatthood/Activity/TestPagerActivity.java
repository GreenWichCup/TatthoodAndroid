package com.example.tatthood.Activity;

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

import java.util.List;

public class TestPagerActivity extends AppCompatActivity {

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
        swipePost = new TestPagerAdapter(this, listSwipePost);
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
}