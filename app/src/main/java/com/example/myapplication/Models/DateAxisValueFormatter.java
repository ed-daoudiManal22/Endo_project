package com.example.myapplication.Models;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateAxisValueFormatter extends ValueFormatter {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.US);

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        // Convert the float value to a Date object
        Date date = new Date((long) value);

        // Format the date as a string
        return dateFormat.format(date);
    }
}
