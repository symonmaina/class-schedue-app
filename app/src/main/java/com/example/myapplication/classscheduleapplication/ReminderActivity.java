package com.example.myapplication.classscheduleapplication;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Switch switchRepeatWeekly;
    private Button btnSetReminder, btnCancelReminder, btnAutoNotify;
    private TextView textReminderTitle, textReminderPreview;
    private String classTitle, classDescription, classStartTime, classDay;

    private static final String CHANNEL_ID = "class_reminder_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        timePicker = findViewById(R.id.timePicker);
        switchRepeatWeekly = findViewById(R.id.switchRepeatWeekly);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        btnCancelReminder = findViewById(R.id.btnCancelReminder);
        btnAutoNotify = findViewById(R.id.btnAutoNotify); // Button for class start notification
        textReminderTitle = findViewById(R.id.textReminderTitle);
        textReminderPreview = findViewById(R.id.textReminderPreview);
        timePicker.setIs24HourView(true);

        // Retrieve data passed from previous activity
        classTitle = getIntent().getStringExtra("classTitle");
        classDescription = getIntent().getStringExtra("classDescription");
        classStartTime = getIntent().getStringExtra("classStartTime");
        classDay = getIntent().getStringExtra("classDay"); // e.g., "Monday"

        textReminderTitle.setText("Set reminder for " + classTitle);

        createNotificationChannel();

        // Set up button click listeners
        btnSetReminder.setOnClickListener(v -> setReminder());
        btnCancelReminder.setOnClickListener(v -> cancelReminder());
        btnAutoNotify.setOnClickListener(v -> scheduleClassStartNotification()); // New feature

        updatePreview();
        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> updatePreview());
    }

    private void updatePreview() {
        int reminderHour = timePicker.getHour();
        int reminderMinute = timePicker.getMinute();

        // Calculate next occurrence of the chosen reminder time on the class day
        Calendar reminderCal = getNextClassDayCalendar(classDay, reminderHour, reminderMinute);

        // Parse the class start time
        String[] startParts = classStartTime.split(":");
        int classHour = Integer.parseInt(startParts[0]);
        int classMinute = Integer.parseInt(startParts[1]);

        // Clone the reminder time and set it to the class start time
        Calendar classCal = (Calendar) reminderCal.clone();
        classCal.set(Calendar.HOUR_OF_DAY, classHour);
        classCal.set(Calendar.MINUTE, classMinute);
        classCal.set(Calendar.SECOND, 0);

        long millisDiff = classCal.getTimeInMillis() - reminderCal.getTimeInMillis();
        long hours = millisDiff / (1000 * 60 * 60);
        long minutes = (millisDiff % (1000 * 60 * 60)) / (1000 * 60);

        String remaining = String.format("Class starts in %d hours and %d minutes after the reminder.", hours, minutes);
        textReminderPreview.setText("Reminder set for " + classDay + " at " + reminderHour + ":"
                + String.format("%02d", reminderMinute) + "\n" + remaining);
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setReminder() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Get the next occurrence of the class day at the chosen reminder time.
        Calendar reminderCalendar = getNextClassDayCalendar(classDay, hour, minute);

        // Parse class start time and create a calendar instance for it.
        String[] startParts = classStartTime.split(":");
        int classHour = Integer.parseInt(startParts[0]);
        int classMinute = Integer.parseInt(startParts[1]);
        Calendar classCalendar = (Calendar) reminderCalendar.clone();
        classCalendar.set(Calendar.HOUR_OF_DAY, classHour);
        classCalendar.set(Calendar.MINUTE, classMinute);
        classCalendar.set(Calendar.SECOND, 0);

        // Calculate the time difference between the reminder time and class start time.
        long diffMillis = classCalendar.getTimeInMillis() - reminderCalendar.getTimeInMillis();
        long diffHours = diffMillis / (1000 * 60 * 60);
        long diffMinutes = (diffMillis / (1000 * 60)) % 60;
        String timeRemaining = "Class starts in " + diffHours + " hours and " + diffMinutes + " minutes.";

        // Prepare the notification intent, including the remaining time in the description.
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("title",  classTitle);
        intent.putExtra("description", classDescription + "\n" + timeRemaining);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, classTitle.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set the alarm based on repeat setting.
        if (switchRepeatWeekly.isChecked()) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    reminderCalendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
            );
            Toast.makeText(this, "Weekly reminder set\n" + timeRemaining, Toast.LENGTH_SHORT).show();
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderCalendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "One-time reminder set\n" + timeRemaining, Toast.LENGTH_LONG).show();
        }

        finish();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleClassStartNotification() {
        // Use the same logic to determine the next occurrence of the class start time
        String[] startTimeParts = classStartTime.split(":");
        int classHour = Integer.parseInt(startTimeParts[0]);
        int classMinute = Integer.parseInt(startTimeParts[1]);
        Calendar classStartCal = getNextClassDayCalendar(classDay, classHour, classMinute);

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("title", "Class Starting: " + classTitle);
        intent.putExtra("description", "Your class \"" + classTitle + "\" starts now.");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, (classTitle + "_start").hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, classStartCal.getTimeInMillis(), pendingIntent);

        Toast.makeText(this, "Class start notification scheduled!", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method calculates the next occurrence of the given class day and time.
     * If the class is scheduled for today and the specified time is still in the future,
     * it will return today's date with that time; otherwise, it shifts the event to the next week.
     */
    private Calendar getNextClassDayCalendar(String day, int hour, int minute) {
        int targetDay = dayToCalendarDay(day);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        int today = calendar.get(Calendar.DAY_OF_WEEK);
        int daysUntil = (targetDay - today + 7) % 7;
        // If the event is scheduled for today but its time has already passed, shift it to next week.
        if (daysUntil == 0 && calendar.getTimeInMillis() < System.currentTimeMillis()) {
            daysUntil = 7;
        }
        calendar.add(Calendar.DAY_OF_YEAR, daysUntil);
        return calendar;
    }

    private int dayToCalendarDay(String day) {
        if (day == null || day.isEmpty()) {
            throw new IllegalArgumentException("Class day cannot be null or empty");
        }

        switch (day.toLowerCase()) {
            case "sunday": return Calendar.SUNDAY;
            case "monday": return Calendar.MONDAY;
            case "tuesday": return Calendar.TUESDAY;
            case "wednesday": return Calendar.WEDNESDAY;
            case "thursday": return Calendar.THURSDAY;
            case "friday": return Calendar.FRIDAY;
            case "saturday": return Calendar.SATURDAY;
            default: return Calendar.MONDAY;
        }
    }

    private void cancelReminder() {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, classTitle.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Reminder canceled", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Class Reminders";
            String description = "Channel for class schedule reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}