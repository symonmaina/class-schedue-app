package com.example.myapplication.classscheduleapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ClassDetailsActivity extends AppCompatActivity {

    private TextView textReminderDetails;
    private TextView textReminderDate;
    private TextView textUnitCode, textClassName, textLecturer, textDay, textTime, textLocation;
    private DatabaseHelper dbHelper;
    private String unitCode;

    private Button btnEditClass, btnDelete, btnSetReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        textReminderDetails = findViewById(R.id.textReminderDetails);
        textReminderDate = findViewById(R.id.textReminderDate);
        textUnitCode = findViewById(R.id.textUnitCode);
        textClassName = findViewById(R.id.textClassName);
        textLecturer = findViewById(R.id.textLecturer);
        textDay = findViewById(R.id.textDay);
        textTime = findViewById(R.id.textTime);
        textLocation = findViewById(R.id.textLocation);
        btnDelete = findViewById(R.id.btnDelete);
        btnEditClass = findViewById(R.id.btnEditClass);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        dbHelper = new DatabaseHelper(this);

        unitCode = getIntent().getStringExtra("unitCode");
        if (unitCode != null) {
            showReminderForClass(unitCode);
        } else {
            Toast.makeText(this, "No class selected", Toast.LENGTH_LONG).show();
            finish();
        }

        if (btnEditClass != null) {
            btnEditClass.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditClassActivity.class);
                intent.putExtra("unitCode", unitCode);
                startActivity(intent);
            });
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                dbHelper.deleteClass(unitCode);
                Toast.makeText(this, "Class deleted", Toast.LENGTH_LONG).show();
                finish();
            });
        }

        if (btnSetReminder != null) {
            btnSetReminder.setOnClickListener(v -> {
                Intent intent = new Intent(this, ReminderActivity.class);

                intent.putExtra("classStartTime", textTime.getText().toString().split(" - ")[0]);
                intent.putExtra("classTitle", textClassName.getText().toString());
                intent.putExtra("classDay", textDay.getText().toString());
                startActivity(intent);
            });
        }
    }

    private void showReminderForClass(String unitCode) {
        Cursor cursor = dbHelper.getClassCursorByUnitCode(unitCode);

        if (cursor != null && cursor.moveToFirst()) {
            String className = cursor.getString(cursor.getColumnIndexOrThrow("class_name"));
            String lecturer = cursor.getString(cursor.getColumnIndexOrThrow("lecturer"));
            String day = cursor.getString(cursor.getColumnIndexOrThrow("day"));
            String startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
            String endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));

            textUnitCode.setText(unitCode);
            textClassName.setText(className);
            textLecturer.setText(lecturer);
            textDay.setText(day);
            textTime.setText(startTime + " - " + endTime);
            textLocation.setText(location);

            Calendar classCal = Calendar.getInstance();
            int dayOfWeek = getDayOfWeekIndex(day);
            classCal.set(Calendar.DAY_OF_WEEK, dayOfWeek);

            // Allow same-day scheduling; shift only if the class has already happened today
            Calendar now = Calendar.getInstance();
            if (classCal.get(Calendar.DAY_OF_WEEK) == now.get(Calendar.DAY_OF_WEEK)) {
                String[] timeParts = startTime.split(":");
                int classHour = Integer.parseInt(timeParts[0]);
                int classMinute = Integer.parseInt(timeParts[1]);

                classCal.set(Calendar.HOUR_OF_DAY, classHour);
                classCal.set(Calendar.MINUTE, classMinute);
                classCal.set(Calendar.SECOND, 0);

                if (classCal.getTimeInMillis() < now.getTimeInMillis()) {
                    classCal.add(Calendar.DAY_OF_YEAR, 7); // Shift to next week only if today's class is already over
                }
            } else if (classCal.getTimeInMillis() < now.getTimeInMillis()) {
                classCal.add(Calendar.DAY_OF_YEAR, 7);
            }

            showReminderDetails(classCal.getTimeInMillis());
            cursor.close();
        }
    }

    private int getDayOfWeekIndex(String day) {
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

    private void showReminderDetails(long triggerTime) {
        long currentTime = System.currentTimeMillis();
        long timeDiff = triggerTime - currentTime;

        long days = TimeUnit.MILLISECONDS.toDays(timeDiff);
        long hours = TimeUnit.MILLISECONDS.toHours(timeDiff) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff) % 60;

        textReminderDetails.setText("Time Remaining: " + days + "d " + hours + "h " + minutes + "m");
        textReminderDetails.setVisibility(View.VISIBLE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MMM dd", Locale.getDefault());
        String formattedDate = dateFormat.format(triggerTime);
        textReminderDate.setText("Class Date: " + formattedDate);
        textReminderDate.setVisibility(View.VISIBLE);
    }
}