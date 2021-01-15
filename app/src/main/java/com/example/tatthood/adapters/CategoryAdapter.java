package com.example.tatthood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.Interfaces.RecyclerViewClickInterface;
import com.example.tatthood.ModelData.Category;
import com.example.tatthood.Modules.GlideApp;
import com.example.tatthood.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>   {

    public Context mContext;
    public List<Category> mCategory;
    public RecyclerViewClickInterface recyclerViewClickInterface;

    DatabaseReference mDatatbase;

    public CategoryAdapter(Context mContext, List<Category> mCategory, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.mContext = mContext;
        this.mCategory = mCategory;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.category_item_view,parent,false);
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mDatatbase = FirebaseDatabase.getInstance().getReference("Category");
        final Category category = mCategory.get(position);
        holder.category_name.setText(category.getCategoryName());
        GlideApp.with(mContext).load(category.getImageurl()).into(holder.image_category);

    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView category_name;
        public CircleImageView image_category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            category_name = itemView.findViewById(R.id.category_name);
            image_category = itemView.findViewById(R.id.image_category);

            itemView.setOnClickListener((View v) -> {
                recyclerViewClickInterface.onItemClick(getBindingAdapterPosition());
            });
            itemView.setOnLongClickListener((v -> {
                recyclerViewClickInterface.onLongClick(getBindingAdapterPosition());
                return true;
            }));

        }
    }

}
