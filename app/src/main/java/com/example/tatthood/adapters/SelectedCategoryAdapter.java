package com.example.tatthood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.R;

import java.util.List;

public class SelectedCategoryAdapter extends RecyclerView.Adapter<SelectedCategoryAdapter.ViewHolder> {
    private Context mContext;
    private List<String> sCategory;

    public SelectedCategoryAdapter(Context mContext, List<String> sCategory) {
        this.mContext = mContext;
        this.sCategory = sCategory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.selected_category_item,parent,false);
        return new SelectedCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String category = sCategory.get(position);
        holder.selectedCategoryName.setText(category);
        holder.removeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sCategory.remove(category);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sCategory.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView selectedCategoryName;
        public ImageView removeIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            selectedCategoryName = itemView.findViewById(R.id.selected_category_name);
            removeIcon = itemView.findViewById(R.id.remove_icon);
        }
    }
}
