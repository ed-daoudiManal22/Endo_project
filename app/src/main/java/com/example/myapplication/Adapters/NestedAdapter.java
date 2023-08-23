package com.example.myapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class NestedAdapter extends RecyclerView.Adapter<NestedAdapter.NestedViewHolder> {

    private final List<String> mList;
    private List<Integer> selectedPositions;

    public NestedAdapter(List<String> mList, List<Integer> selectedPositions) {
        this.mList = mList;
        this.selectedPositions = selectedPositions;
    }

    @NonNull
    @Override
    public NestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nested_item, parent, false);
        return new NestedViewHolder(view);
    }
    public void setSelectedPositions(List<Integer> selectedPositions) {
        this.selectedPositions = selectedPositions;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull NestedViewHolder holder, int position) {
        holder.m1.setText(mList.get(position));

        // Check if the current item's position is in the selected positions list of the NestedAdapter
        boolean isChecked = selectedPositions.contains(position);
        holder.checkBox.setChecked(isChecked);

        holder.itemView.setOnClickListener(view -> {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                int selectedPosition = holder.getAdapterPosition();
                if (selectedPositions.contains(selectedPosition)) {
                    selectedPositions.remove(Integer.valueOf(selectedPosition));
                } else {
                    selectedPositions.add(selectedPosition);
                }
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class NestedViewHolder extends RecyclerView.ViewHolder {
        private final TextView m1;
        private final CheckBox checkBox;

        public NestedViewHolder(@NonNull View itemView) {
            super(itemView);
            m1 = itemView.findViewById(R.id.nestedItem1);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public List<String> getSelectedOptions() {
        List<String> selectedOptions = new ArrayList<>();
        for (int position : selectedPositions) {
            selectedOptions.add(mList.get(position));
        }
        return selectedOptions;
    }
}
