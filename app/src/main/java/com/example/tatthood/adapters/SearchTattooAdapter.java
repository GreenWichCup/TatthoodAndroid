package com.example.tatthood.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.R;

public class SearchTattooAdapter extends RecyclerView.ViewHolder {

    public TextView categoryName ;
    public ImageView categoryImageUrl;
    public RecyclerView category_rv;
    public RecyclerView.LayoutManager manager;

    public SearchTattooAdapter(@NonNull View itemView) {
        super(itemView);

        categoryName = itemView.findViewById(R.id.category_name);
        manager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        category_rv = itemView.findViewById(R.id.category_rv);
        category_rv.setLayoutManager(manager);
    }
}
