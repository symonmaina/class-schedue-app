package com.example.myapplication.classscheduleapplication;

import android.widget.Spinner;
import android.widget.TimePicker;

public class Helper {
    public static void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public static void setTimePicker(TimePicker picker, String time) {
        String[] parts = time.split(":");
        picker.setHour(Integer.parseInt(parts[0]));
        picker.setMinute(Integer.parseInt(parts[1]));
    }

    public static String getTimeFromPicker(TimePicker picker) {
        return String.format("%02d:%02d", picker.getHour(), picker.getMinute());
    }
}
