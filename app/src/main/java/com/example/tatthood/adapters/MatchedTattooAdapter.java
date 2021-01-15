package com.example.tatthood.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.R;

public class MatchedTattooAdapter extends RecyclerView.ViewHolder {

    public TextView title;
    public ImageView matched_image;

    public MatchedTattooAdapter(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title_matched_tattoo);
        matched_image = itemView.findViewById(R.id.image_matched_tattoo);
    }
}
