package com.example.myapplication.Models;

public class PeriodDay {
    private final String date;
    private final boolean isPeriodDay;
    private boolean isOvulationDay;

    public PeriodDay(String date, boolean isPeriodDay) {
        this.date = date;
        this.isPeriodDay = isPeriodDay;
    }

    public String getDate() {
        return date;
    }

    public boolean isPeriodDay() {
        return isPeriodDay;
    }

    public boolean isOvulationDay() {
        return isOvulationDay;
    }

    public void setOvulationDay(boolean ovulationDay) {
        isOvulationDay = ovulationDay;
    }
}
