package com.spmenais.paincare.AI.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.spmenais.paincare.R;

import java.util.List;
import java.util.Map;

/**
 * Adapter for displaying feature importance in AI explanations
 */
public class FeatureImportanceAdapter extends RecyclerView.Adapter<FeatureImportanceAdapter.FeatureViewHolder> {
    
    private final List<Map<String, Object>> features;

    public FeatureImportanceAdapter(List<Map<String, Object>> features) {
        this.features = features;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_feature_importance, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        Map<String, Object> feature = features.get(position);
        holder.bind(feature);
    }

    @Override
    public int getItemCount() {
        return features.size();
    }

    static class FeatureViewHolder extends RecyclerView.ViewHolder {
        private final TextView featureNameText;
        private final TextView featureValueText;
        private final ProgressBar featureProgressBar;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            featureNameText = itemView.findViewById(R.id.featureNameText);
            featureValueText = itemView.findViewById(R.id.featureValueText);
            featureProgressBar = itemView.findViewById(R.id.featureProgressBar);
        }

        public void bind(Map<String, Object> feature) {
            String name = (String) feature.get("name");
            Object importanceObj = feature.get("importance");
            double importance = importanceObj instanceof Number ? 
                ((Number) importanceObj).doubleValue() : 0.0;
            
            featureNameText.setText(name);
            featureValueText.setText(String.format("%.0f%%", importance * 100));
            featureProgressBar.setProgress((int) (importance * 100));
        }
    }
}
