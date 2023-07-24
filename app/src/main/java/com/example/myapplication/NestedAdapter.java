package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NestedAdapter extends RecyclerView.Adapter<NestedAdapter.NestedViewHolder> {

    private List<String> mList;
    private List<Integer> checkedPositions = new ArrayList<>();

    public NestedAdapter(List<String> mList)
    {
        this.mList = mList;
    }

    @NonNull
    @Override
    public NestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nested_item, parent, false);
        return new NestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedViewHolder holder, int position) {
        holder.m1.setText(mList.get(position));

        // Check if the current item's position is in the checkedPositions list
        boolean isChecked = checkedPositions.contains(position);
        holder.checkBox.setChecked(isChecked);

        // Set a click listener on the item to handle CheckBox state changes
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    // Toggle the checked state of the CheckBox
                    if (checkedPositions.contains(holder.getAdapterPosition())) {
                        // If the clicked item is already checked, uncheck it
                        checkedPositions.remove((Integer) holder.getAdapterPosition());
                    } else {
                        // If the clicked item is not checked, check it
                        checkedPositions.add(holder.getAdapterPosition());
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
}
