package com.example.myapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DataModel;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class NestedAdapter extends RecyclerView.Adapter<NestedAdapter.NestedViewHolder> {

    private List<String> mList;
    private List<Integer> selectedPositions = new ArrayList<>();
    private DataModel dataModel;

    public NestedAdapter(List<String> mList, List<Integer> selectedPositions, DataModel dataModel) {
        this.mList = mList;
        this.selectedPositions = selectedPositions;
        this.dataModel = dataModel;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    int selectedPosition = holder.getAdapterPosition();
                    if (selectedPositions.contains(selectedPosition)) {
                        selectedPositions.remove(Integer.valueOf(selectedPosition));
                    } else {
                        selectedPositions.add(selectedPosition);
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class NestedViewHolder extends RecyclerView.ViewHolder {
        private TextView m1;
        private CheckBox checkBox;

        public NestedViewHolder(@NonNull View itemView) {
            super(itemView);
            m1 = itemView.findViewById(R.id.nestedItem1);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public List<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    public List<String> getSelectedOptions() {
        List<String> selectedOptions = new ArrayList<>();
        for (int position : selectedPositions) {
            selectedOptions.add(mList.get(position));
        }
        return selectedOptions;
    }
}
