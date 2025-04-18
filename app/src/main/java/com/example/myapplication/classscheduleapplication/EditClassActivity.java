package com.example.myapplication.classscheduleapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditClassActivity extends AppCompatActivity {

    EditText editClassName, editLecturer, editLocation;
    Spinner editDay;
    TimePicker editStartTime, editEndTime;
    Button btnUpdate, btnDelete;
    DatabaseHelper dbHelper;
    String unitCode; // Primary key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        // Get unit code from intent
        unitCode = getIntent().getStringExtra("unitCode");

        // Initialize views
        editClassName = findViewById(R.id.editClassName);
        editLecturer = findViewById(R.id.editLecturer);
        editDay = findViewById(R.id.editDay);
        editStartTime = findViewById(R.id.editStartTime);
        editEndTime = findViewById(R.id.editEndTime);
        editLocation = findViewById(R.id.editLocation);
        btnUpdate = findViewById(R.id.btnUpdateClass);
        btnDelete = findViewById(R.id.btnDelete);

        dbHelper = new DatabaseHelper(this);

        // Spinner adapter for days
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.days_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editDay.setAdapter(adapter);

        // Load class details from database
        Cursor cursor = dbHelper.getClassCursorByUnitCode(unitCode);

        if (cursor != null && cursor.moveToFirst()) {
            ClassModel classData = new ClassModel();
            classData.setUnitCode(cursor.getString(cursor.getColumnIndexOrThrow("unit_code")));
            classData.setClassName(cursor.getString(cursor.getColumnIndexOrThrow("class_name")));
            classData.setLecturer(cursor.getString(cursor.getColumnIndexOrThrow("lecturer")));
            classData.setDay(cursor.getString(cursor.getColumnIndexOrThrow("day")));
            classData.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow("start_time")));
            classData.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow("end_time")));
            classData.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
            cursor.close();

            // Populate UI fields
            editClassName.setText(classData.getClassName());
            editLecturer.setText(classData.getLecturer());
            editLocation.setText(classData.getLocation());
            Helper.setSpinnerSelection(editDay, classData.getDay());
            Helper.setTimePicker(editStartTime, classData.getStartTime());
            Helper.setTimePicker(editEndTime, classData.getEndTime());
        }

        // Handle Update Button
        btnUpdate.setOnClickListener(v -> {
            String className = editClassName.getText().toString().trim();
            String lecturer = editLecturer.getText().toString().trim();
            String day = editDay.getSelectedItem().toString();
            String startTime = Helper.getTimeFromPicker(editStartTime);
            String endTime = Helper.getTimeFromPicker(editEndTime);
            String location = editLocation.getText().toString().trim();

            if (className.isEmpty() || lecturer.isEmpty() || day.isEmpty() ||
                    startTime.isEmpty() || endTime.isEmpty() || location.isEmpty()) {
                Toast.makeText(EditClassActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updated = dbHelper.updateClass(unitCode, className, lecturer, day, startTime, endTime, location);

            if (updated) {
                Toast.makeText(EditClassActivity.this, "Class updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditClassActivity.this, "Failed to update class", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Delete Button
        btnDelete.setOnClickListener(v -> {
            boolean deleted = dbHelper.deleteClass(unitCode);
            if (deleted) {
                Toast.makeText(EditClassActivity.this, "Class deleted successfully!", Toast.LENGTH_SHORT).show();
                finish(); // close activity
            } else {
                Toast.makeText(EditClassActivity.this, "Failed to delete class", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
