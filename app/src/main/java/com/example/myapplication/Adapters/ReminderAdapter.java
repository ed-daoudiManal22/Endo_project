package com.example.myapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Reminder;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private List<Reminder> reminders;
    private OnReminderDeleteListener onReminderDeleteListener;
    private OnReminderActiveStatusChangeListener onReminderActiveStatusChangeListener;
    public List<Reminder> getReminders() {
        return reminders;
    }
    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }
    public ReminderAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(itemView);
    }

    public void setOnReminderActiveStatusChangeListener(OnReminderActiveStatusChangeListener listener) {
        this.onReminderActiveStatusChangeListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.titleTextView.setText(reminder.getTitle());
        holder.dateTextView.setText(reminder.getTime());
        holder.activeCheckbox.setChecked(reminder.isActive());

        // Convert boolean array of repeat days to a comma-separated string
        StringBuilder repeatDaysBuilder = new StringBuilder();
        boolean[] repeatDaysArray = reminder.getRepeatDays();
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < repeatDaysArray.length; i++) {
            if (repeatDaysArray[i]) {
                repeatDaysBuilder.append(dayNames[i]);
                repeatDaysBuilder.append(", ");
            }
        }
        // Remove the trailing comma and space if any repeat days were added
        if (repeatDaysBuilder.length() > 0) {
            repeatDaysBuilder.setLength(repeatDaysBuilder.length() - 2);
        }
        holder.repeatDaysTextView.setText(repeatDaysBuilder.toString());

        holder.activeCheckbox.setOnCheckedChangeListener(null); // Remove previous listener to avoid conflicts
        holder.activeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Reminder reminder = reminders.get(adapterPosition);
                    reminder.setActive(isChecked);
                    if (onReminderActiveStatusChangeListener != null) {
                        onReminderActiveStatusChangeListener.onReminderActiveStatusChange(reminder);
                    }
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onReminderDeleteListener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onReminderDeleteListener.onReminderDelete(adapterPosition);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView repeatDaysTextView;
        TextView dateTextView;
        CheckBox activeCheckbox;
        ImageView deleteButton;

        ReminderViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            repeatDaysTextView = itemView.findViewById(R.id.repeatDaysTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            activeCheckbox = itemView.findViewById(R.id.activeCheckbox);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface OnReminderDeleteListener {
        void onReminderDelete(int position);
    }

    public void setOnReminderDeleteListener(OnReminderDeleteListener listener) {
        this.onReminderDeleteListener = listener;
    }

    public interface OnReminderActiveStatusChangeListener {
        void onReminderActiveStatusChange(Reminder reminder);
    }
}
