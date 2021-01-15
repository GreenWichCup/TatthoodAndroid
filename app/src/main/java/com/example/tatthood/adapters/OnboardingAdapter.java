package com.example.tatthood.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.ModelData.OnBoardingItems;
import com.example.tatthood.R;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private List<OnBoardingItems> obListItems;

    public OnboardingAdapter(List<OnBoardingItems> obListItems) {
        this.obListItems = obListItems;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.onboarding_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
    holder.setOnboardingData(obListItems.get(position));
    }

    @Override
    public int getItemCount() {
        return obListItems.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTitle, txtDescription;
        ImageView obImage;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            obImage = itemView.findViewById(R.id.ob_image);
        }

        public void setOnboardingData(OnBoardingItems onboardingItems) {
            txtTitle.setText(onboardingItems.getTitle());
            txtDescription.setText(onboardingItems.getDescription());
            obImage.setImageResource(onboardingItems.getImage());
        }
    }
}
