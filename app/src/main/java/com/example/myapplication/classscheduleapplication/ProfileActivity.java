package com.example.myapplication.classscheduleapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    EditText nameInput, regInput, courseInput, yearInput, websiteInput;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ProfileActivity", "ProfileActivity opened!");
        setContentView(R.layout.activity_profile);
        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);

        // Initialize views
        nameInput = findViewById(R.id.editName);
        regInput = findViewById(R.id.editReg);
        courseInput = findViewById(R.id.editCourse);
        yearInput = findViewById(R.id.editYear);
        websiteInput = findViewById(R.id.editWebsite);
        btnSave = findViewById(R.id.btnSaveProfile);

        // Pre-fill fields with stored values
        nameInput.setText(prefs.getString("name", ""));
        regInput.setText(prefs.getString("reg", ""));
        courseInput.setText(prefs.getString("course", ""));
        yearInput.setText(prefs.getString("year", ""));
        websiteInput.setText(prefs.getString("website", ""));

        btnSave.setOnClickListener(v -> {
            String newName = nameInput.getText().toString().trim();
            String newReg = regInput.getText().toString().trim();
            String newCourse = courseInput.getText().toString().trim();
            String newYear = yearInput.getText().toString().trim();
            String newWebsite = websiteInput.getText().toString().trim();

            if (newName.isEmpty() || newReg.isEmpty() || newCourse.isEmpty() || newYear.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Please fill in all required fields", Toast.LENGTH_LONG).show();
                return;
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("name", newName);
            editor.putString("reg", newReg);
            editor.putString("course", newCourse);
            editor.putString("year", newYear);
            editor.putString("website", newWebsite);
            editor.apply();

            Toast.makeText(ProfileActivity.this, "Profile saved!", Toast.LENGTH_LONG).show();

            // Send result back to MainActivity
            Intent resultIntent = new Intent(ProfileActivity.this, MainActivity.class);
            resultIntent.putExtra("updatedName", newName);
            resultIntent.putExtra("updatedReg", newReg);
            resultIntent.putExtra("updatedCourse", newCourse);
            resultIntent.putExtra("updatedYear", newYear);
            resultIntent.putExtra("updatedWebsite", newWebsite);

            startActivity(resultIntent); // Explicitly start MainActivity
            finish(); // Finish ProfileActivity
        });
    }
}