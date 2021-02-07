package com.example.tatthood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.Interfaces.RecyclerViewClickInterface;
import com.example.tatthood.ModelData.Post;
import com.example.tatthood.Modules.GlideApp;
import com.example.tatthood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mContext;
    private List<Post> mPost;
    private FirebaseUser firebaseUser;
    public RecyclerViewClickInterface photoClickInterface;


    public PostAdapter(Context mContext, List<Post> mPost,RecyclerViewClickInterface photoClickInterface) {
        this.mContext = mContext;
        this.mPost = mPost;
        this.photoClickInterface = photoClickInterface;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.stream_item,parent, false  );

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);
        GlideApp.with(mContext).load(post.getPostimage()).into(holder.post_image);

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView post_image;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.post_image);

            itemView.setOnClickListener((View v) -> {
                photoClickInterface.onItemClick(getBindingAdapterPosition());
            });


        }
    }


}
