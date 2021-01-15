package com.example.tatthood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.ModelData.Post;
import com.example.tatthood.Modules.GlideApp;
import com.example.tatthood.R;

import java.util.List;

public class TestPagerAdapter extends RecyclerView.Adapter<TestPagerAdapter.TestPagerViewHolder> {

    public Context mContext;
    private List<Post> mPost;


    public TestPagerAdapter( Context mContext, List<Post> mPost) {

        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public TestPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_swipe_item,parent,false);

        return new TestPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestPagerViewHolder holder, int position) {
        Post post = mPost.get(position);
        GlideApp.with(mContext).load(post.getPostimage()).into(holder.image_post);
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class TestPagerViewHolder extends RecyclerView.ViewHolder {
        ImageView image_post, image_profile;
        public TestPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            image_post = itemView.findViewById(R.id.post_image);
            image_profile = itemView.findViewById(R.id.image_profile);
        }
    }
}
