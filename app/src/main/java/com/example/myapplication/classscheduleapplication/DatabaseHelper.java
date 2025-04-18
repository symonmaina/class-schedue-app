package com.example.myapplication.classscheduleapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ClassSchedule.db";
    public static final int DATABASE_VERSION = 2; // Updated version

    public static final String TABLE_CLASSES = "classes";
    public static final String COL_UNIT_CODE = "unit_code"; // Primary key
    public static final String COL_CLASS_NAME = "class_name";
    public static final String COL_LECTURER = "lecturer";
    public static final String COL_DAY = "day";
    public static final String COL_START_TIME = "start_time";
    public static final String COL_END_TIME = "end_time";
    public static final String COL_LOCATION = "location";
    public static final String COL_COMMENT = "comment"; // New column for comments

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the class table with the comment column included
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_CLASSES + " (" +
                COL_UNIT_CODE + " TEXT PRIMARY KEY, " +
                COL_CLASS_NAME + " TEXT, " +
                COL_LECTURER + " TEXT, " +
                COL_DAY + " TEXT, " +
                COL_START_TIME + " TEXT, " +
                COL_END_TIME + " TEXT, " +
                COL_LOCATION + " TEXT, " +
                COL_COMMENT + " TEXT)";
        db.execSQL(query);
    }

    // Upgrade the database. In this case, if upgrading from version 1, add the comment column.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_CLASSES + " ADD COLUMN " + COL_COMMENT + " TEXT");
        }
        // Optionally, handle future upgrades here
    }

    // Add a class along with a comment
    public boolean addClass(String unitCode, String className, String lecturer,
                            String day, String startTime, String endTime, String location, String comment) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_UNIT_CODE, unitCode);
        values.put(COL_CLASS_NAME, className);
        values.put(COL_LECTURER, lecturer);
        values.put(COL_DAY, day);
        values.put(COL_START_TIME, startTime);
        values.put(COL_END_TIME, endTime);
        values.put(COL_LOCATION, location);
        values.put(COL_COMMENT, comment);

        long result = db.insert(TABLE_CLASSES, null, values);
        return result != -1;
    }

    // Update class details (excluding comment)
    public boolean updateClass(String unitCode, String className, String lecturer,
                               String day, String startTime, String endTime, String location) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CLASS_NAME, className);
        values.put(COL_LECTURER, lecturer);
        values.put(COL_DAY, day);
        values.put(COL_START_TIME, startTime);
        values.put(COL_END_TIME, endTime);
        values.put(COL_LOCATION, location);

        int result = db.update(TABLE_CLASSES, values, COL_UNIT_CODE + "=?", new String[]{unitCode});
        return result > 0;
    }

    // Delete a class record identified by its unit code
    public boolean deleteClass(String unitCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CLASSES, COL_UNIT_CODE + "=?", new String[]{unitCode});
        return result > 0;
    }

    // Update the comment for the class with the given unit code
    public boolean updateComment(String unitCode, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COMMENT, comment);

        int result = db.update(TABLE_CLASSES, values, COL_UNIT_CODE + "=?", new String[]{unitCode});
        return result > 0;
    }

    // Fetch the comment for a class using its unit code
    public String getComment(String unitCode) {
        return getSingleStringValue(unitCode, COL_COMMENT);
    }

    // Generic helper to fetch a single string value for a given column by unit code
    private String getSingleStringValue(String unitCode, String column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASSES, new String[]{column},
                COL_UNIT_CODE + "=?", new String[]{unitCode}, null, null, null);

        String result = null;
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(0);
            cursor.close();
        }
        return result;
    }

    public String getUnitCode(String unitCode) {
        return getSingleStringValue(unitCode, COL_UNIT_CODE);
    }

    public String getClassName(String unitCode) {
        return getSingleStringValue(unitCode, COL_CLASS_NAME);
    }

    public String getLecturer(String unitCode) {
        return getSingleStringValue(unitCode, COL_LECTURER);
    }

    public String getDay(String unitCode) {
        return getSingleStringValue(unitCode, COL_DAY);
    }

    public String getStartTime(String unitCode) {
        return getSingleStringValue(unitCode, COL_START_TIME);
    }

    public String getEndTime(String unitCode) {
        return getSingleStringValue(unitCode, COL_END_TIME);
    }

    public String getLocation(String unitCode) {
        return getSingleStringValue(unitCode, COL_LOCATION);
    }

    // Retrieve all classes in the order of day and start time
    public Cursor getAllClassesCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CLASSES + " ORDER BY " + COL_DAY + ", " + COL_START_TIME, null);
    }

    // Retrieve a class record given its unit code
    public Cursor getClassCursorByUnitCode(String unitCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CLASSES + " WHERE " + COL_UNIT_CODE + "=?", new String[]{unitCode});
    }

    // Return a list of ClassModel objects for all classes in the database.
    // Note: Ensure your ClassModel contains a constructor that accepts the comment.
    public List<ClassModel> getAllClasses() {
        List<ClassModel> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASSES, null);

        if (cursor.moveToFirst()) {
            do {
                ClassModel classModel = new ClassModel(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT_CODE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASS_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LECTURER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DAY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_START_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_END_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_COMMENT))
                );
                classList.add(classModel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return classList;
    }
}