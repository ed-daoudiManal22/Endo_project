package com.example.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {
    private final DotSpan dotSpan;
    private final HashSet<CalendarDay> dates;

    public EventDecorator(Collection<CalendarDay> dates, DotSpan dotSpan) {
        this.dates = new HashSet<>(dates);
        this.dotSpan = dotSpan;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(dotSpan);
    }
}

