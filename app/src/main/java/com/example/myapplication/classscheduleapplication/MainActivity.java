package com.example.myapplication.classscheduleapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    DatabaseHelper dbHelper;
    FloatingActionButton fabAdd;
    TextView emptyView;
    TextView nameText, regNoText, courseText, yearText;

    // Modern Activity Result API
    private final ActivityResultLauncher<Intent> profileActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Log.d("DEBUG", "ProfileActivity returned successfully.");
                    loadProfileData(); // Refresh profile after returning
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        emptyView = findViewById(R.id.textEmpty);

        nameText = findViewById(R.id.textName);
        regNoText = findViewById(R.id.textRegNo);
        courseText = findViewById(R.id.textCourse);
        yearText = findViewById(R.id.textYear);

        dbHelper = new DatabaseHelper(this);

        loadProfileData(); // Load profile section

        // Edit profile button
        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(v -> {
            Log.d("DEBUG", "Edit Profile Button Clicked");
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            profileActivityLauncher.launch(intent); // New Activity Result API usage
        });

        // Student Portal button
        Button btnPortal = findViewById(R.id.btnPortal);
        btnPortal.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);
            String portalUrl = prefs.getString("website", "");

            if (!portalUrl.isEmpty()) {
                if (!portalUrl.startsWith("http://") && !portalUrl.startsWith("https://")) {
                    portalUrl = "http://" + portalUrl;
                }

                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(portalUrl));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Invalid URL.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "No portal URL set in profile.", Toast.LENGTH_LONG).show();
            }
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddClassActivity.class);
            startActivity(intent);
        });

        loadClasses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData(); // Refresh profile when returning from ProfileActivity
        loadClasses();     // Also refresh class list
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences("user_profile", MODE_PRIVATE);
        String name = prefs.getString("name", "N/A");
        String reg = prefs.getString("reg", "N/A");
        String course = prefs.getString("course", "N/A");
        String year = prefs.getString("year", "N/A");
        String portalUrl = prefs.getString("website", "");

        nameText.setText("Name : " + name);
        regNoText.setText("Reg No : " + reg);
        courseText.setText("Course : " + course);
        yearText.setText("Year : " + year);

        Log.d("PROFILE", "Name: " + name);
        Log.d("PROFILE", "Reg No: " + reg);
        Log.d("PROFILE", "Course: " + course);
        Log.d("PROFILE", "Year: " + year);
        Log.d("PROFILE", "Portal URL: " + portalUrl);
    }

    private void loadClasses() {
        List<ClassModel> classList = dbHelper.getAllClasses();

        if (classList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            int todayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

            Collections.sort(classList, new Comparator<ClassModel>() {
                @Override
                public int compare(ClassModel c1, ClassModel c2) {
                    int i1 = getDayIndex(c1.getDay());
                    int i2 = getDayIndex(c2.getDay());

                    if (i1 == todayIndex && i2 != todayIndex) return -1;
                    else if (i1 != todayIndex && i2 == todayIndex) return 1;
                    else return Integer.compare(i1, i2);
                }

                private int getDayIndex(String day) {
                    for (int i = 0; i < daysOfWeek.length; i++) {
                        if (daysOfWeek[i].equalsIgnoreCase(day)) return i;
                    }
                    return 7;
                }
            });

            if (classAdapter == null) {
                classAdapter = new ClassAdapter(this, classList);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(classAdapter);
            } else {
                classAdapter.updateClassList(classList);
            }
        }
    }
}