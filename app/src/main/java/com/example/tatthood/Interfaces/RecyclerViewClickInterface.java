package com.example.tatthood.Interfaces;

import android.view.View;

public interface RecyclerViewClickInterface {
    void onItemClick(int position);
    void onLongClick(int position);
    void onViewClick(View viewClicked);
}
