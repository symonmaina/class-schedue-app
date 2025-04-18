package com.example.myapplication.classscheduleapplication;

public class ClassModel {
    private String unitCode;
    private String className;
    private String lecturer;
    private String day;
    private String startTime;
    private String endTime;
    private String location;
    private String comment; // New field for comments

    // Default constructor (important for Cursor mapping or deserialization)
    public ClassModel() {
    }

    // Parameterized constructor without comment (default comment set to empty)
    public ClassModel(String unitCode, String className, String lecturer, String day,
                      String startTime, String endTime, String location) {
        this.unitCode = unitCode;
        this.className = className;
        this.lecturer = lecturer;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.comment = "";
    }

    // Parameterized constructor with comment
    public ClassModel(String unitCode, String className, String lecturer, String day,
                      String startTime, String endTime, String location, String comment) {
        this.unitCode = unitCode;
        this.className = className;
        this.lecturer = lecturer;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.comment = comment;
    }

    // Getters
    public String getUnitCode() {
        return unitCode;
    }

    public String getClassName() {
        return className;
    }

    public String getLecturer() {
        return lecturer;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public String getComment() {
        return comment;
    }

    // Setters
    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}