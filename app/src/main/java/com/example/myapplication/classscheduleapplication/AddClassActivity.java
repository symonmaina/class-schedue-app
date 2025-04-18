package com.example.myapplication.classscheduleapplication;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddClassActivity extends AppCompatActivity{
    EditText unitCodeInput, classNameInput, lecturerInput,   locationInput;
    Spinner dayInput;
    TimePicker startTimeInput, endTimeInput;
    Button btnSave;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        // Initialize views
        unitCodeInput = findViewById(R.id.editUnitCode);
        classNameInput = findViewById(R.id.editClassName);
        lecturerInput = findViewById(R.id.editLecturer);
        dayInput = findViewById(R.id.editDay);
        startTimeInput = findViewById(R.id.editStartTime);
        endTimeInput = findViewById(R.id.editEndTime);
        locationInput = findViewById(R.id.editLocation);
        btnSave = findViewById(R.id.btnsave);

        // Init DB
        dbHelper = new DatabaseHelper(this);
// Set up spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.days_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayInput.setAdapter(adapter);

        // Save button click
        btnSave.setOnClickListener(v -> {
            String unitCode = unitCodeInput.getText().toString().trim();
            String className = classNameInput.getText().toString().trim();
            String lecturer = lecturerInput.getText().toString().trim();
            String day = dayInput.getSelectedItem().toString();
            int startHour = startTimeInput.getHour();
            int startMinute = startTimeInput.getMinute();
            String startTime = String.format("%02d:%02d", startHour, startMinute);
            int endHour = endTimeInput.getHour();
            int endMinute = endTimeInput.getMinute();
            String endTime = String.format("%02d:%02d", endHour, endMinute);
            String location = locationInput.getText().toString().trim();

            if (unitCode.isEmpty() || className.isEmpty() || lecturer.isEmpty() || location.isEmpty()) {
                Toast.makeText(AddClassActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                boolean inserted = dbHelper.addClass(unitCode, className, lecturer, day, startTime, endTime, location, "");
                if (inserted) {
                    Toast.makeText(AddClassActivity.this, "Class added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Return to MainActivity
                } else {
                    Toast.makeText(AddClassActivity.this, "Error: Class already exists!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("AddClassActivity", "Error saving class", e);
                Toast.makeText(AddClassActivity.this, "Error saving class: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });


    }
}

