package com.example.myapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.PeriodDay;
import com.example.myapplication.R;

import java.util.List;

public class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.PeriodViewHolder> {
    private List<PeriodDay> periodDayList;

    public PeriodAdapter(List<PeriodDay> periodDayList) {
        this.periodDayList = periodDayList;
    }

    @NonNull
    @Override
    public PeriodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.period_item_layout, parent, false);
        return new PeriodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeriodViewHolder holder, int position) {
        PeriodDay periodDay = periodDayList.get(position);

        holder.dateTextView.setText(periodDay.getDate());
        holder.periodIndicatorView.setVisibility(periodDay.isPeriodDay() ? View.VISIBLE : View.GONE);
        holder.ovulationIndicatorView.setVisibility(periodDay.isOvulationDay() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return periodDayList.size();
    }

    public static class PeriodViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public View periodIndicatorView;
        public View ovulationIndicatorView;

        public PeriodViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            periodIndicatorView = itemView.findViewById(R.id.periodIndicatorView);
            ovulationIndicatorView = itemView.findViewById(R.id.ovulationIndicatorView);
        }
    }
}