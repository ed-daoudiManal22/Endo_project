package com.spmenais.paincare.AI.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.spmenais.paincare.R;

import java.util.List;

/**
 * Adapter for displaying AI recommendations
 */
public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.RecommendationViewHolder> {
    
    private final List<String> recommendations;

    public RecommendationsAdapter(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_recommendation, parent, false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        String recommendation = recommendations.get(position);
        holder.bind(recommendation);
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    static class RecommendationViewHolder extends RecyclerView.ViewHolder {
        private final TextView recommendationText;
        private final ImageView recommendationIcon;

        public RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            recommendationText = itemView.findViewById(R.id.recommendationText);
            recommendationIcon = itemView.findViewById(R.id.recommendationIcon);
        }

        public void bind(String recommendation) {
            recommendationText.setText(recommendation);
            
            // Set icon based on recommendation type
            if (recommendation.contains("🚨") || recommendation.contains("High")) {
                recommendationIcon.setImageResource(R.drawable.ic_warning_red);
            } else if (recommendation.contains("⚠️") || recommendation.contains("Moderate")) {
                recommendationIcon.setImageResource(R.drawable.ic_warning_orange);
            } else if (recommendation.contains("✅") || recommendation.contains("good")) {
                recommendationIcon.setImageResource(R.drawable.ic_check_green);
            } else {
                recommendationIcon.setImageResource(R.drawable.ic_info_gray);
            }
        }
    }
}
