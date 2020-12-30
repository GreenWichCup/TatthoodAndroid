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

public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoGridAdapter.ViewHolder> {

    public Context mContext ;
    public List<Post> userPostedPhoto;

    public PhotoGridAdapter(Context mContext, List<Post> userPostedPhoto) {
        this.mContext = mContext;
        this.userPostedPhoto = userPostedPhoto;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item,parent, false  );

        return new PhotoGridAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post userPhoto = userPostedPhoto.get(position);
        GlideApp.with(mContext).load(userPhoto.getPostimage()).into(holder.postedImage);


    }

    @Override
    public int getItemCount() {
        return userPostedPhoto.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView postedImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postedImage = itemView.findViewById(R.id.posted_image);
        }
    }
}
