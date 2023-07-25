package com.example.myapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DataModel;
import com.example.myapplication.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<DataModel> mList;

    public ItemAdapter(List<DataModel> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_item_in_recycler_view, parent, false);
        return new ItemViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        DataModel model = mList.get(position);
        holder.mTextView.setText(model.getTitle());

        boolean isExpandable = model.isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

        if (isExpandable) {
            holder.mArrowImage.setImageResource(R.drawable.arrow_up);
        } else {
            holder.mArrowImage.setImageResource(R.drawable.arrow_down);
        }

        NestedAdapter adapter = new NestedAdapter(model.getOptionsList(), model.getSelectedPositions(), model);
        holder.setNestedAdapter(adapter);
        holder.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.nestedRecyclerView.setAdapter(adapter);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setExpandable(!model.isExpandable());
                updateDataModelSelections(model);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void updateDataModelSelections(DataModel dataModel) {
        NestedAdapter nestedAdapter = dataModel.getNestedAdapter();
        if (nestedAdapter != null) {
            // Update the selected positions in the NestedAdapter when expanding/unexpanding
            nestedAdapter.setSelectedPositions(dataModel.getSelectedPositions());
            List<String> selectedOptions = nestedAdapter.getSelectedOptions();
            dataModel.setSelectedOptions(selectedOptions);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private RelativeLayout expandableLayout;
        private TextView mTextView;
        private ImageView mArrowImage;
        private RecyclerView nestedRecyclerView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            mTextView = itemView.findViewById(R.id.item1);
            mArrowImage = itemView.findViewById(R.id.arrow_imageview);
            nestedRecyclerView = itemView.findViewById(R.id.child_rv);
        }

        public void setNestedAdapter(NestedAdapter nestedAdapter) {
            nestedRecyclerView.setAdapter(nestedAdapter);

            // Get the DataModel object from the mList using the current adapter position
            DataModel dataModel = mList.get(getAdapterPosition());
            dataModel.setNestedAdapter(nestedAdapter);
        }
    }
}