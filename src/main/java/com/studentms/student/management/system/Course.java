/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.studentms.student.management.system;

import javafx.beans.property.*;

/**
 * Course model class using JavaFX Property objects
 * Represents a course that students can enroll in
 */
public class Course {
    
    private final IntegerProperty courseId;
    private final StringProperty courseCode;
    private final StringProperty courseName;
    private final IntegerProperty credits;
    private final StringProperty description;
    
    /**
     * Constructor for creating a complete Course object
     */
    public Course(int courseId, String courseCode, String courseName, 
                  int credits, String description) {
        this.courseId = new SimpleIntegerProperty(courseId);
        this.courseCode = new SimpleStringProperty(courseCode);
        this.courseName = new SimpleStringProperty(courseName);
        this.credits = new SimpleIntegerProperty(credits);
        this.description = new SimpleStringProperty(description);
    }
    
    /**
     * Constructor for creating a new course (without ID)
     */
    public Course(String courseCode, String courseName, int credits, String description) {
        this.courseId = new SimpleIntegerProperty(0);
        this.courseCode = new SimpleStringProperty(courseCode);
        this.courseName = new SimpleStringProperty(courseName);
        this.credits = new SimpleIntegerProperty(credits);
        this.description = new SimpleStringProperty(description);
    }
    
    // ==================== PROPERTY GETTERS ====================
    
    public IntegerProperty courseIdProperty() {
        return courseId;
    }
    
    public StringProperty courseCodeProperty() {
        return courseCode;
    }
    
    public StringProperty courseNameProperty() {
        return courseName;
    }
    
    public IntegerProperty creditsProperty() {
        return credits;
    }
    
    public StringProperty descriptionProperty() {
        return description;
    }
    
    // ==================== VALUE GETTERS ====================
    
    public int getCourseId() {
        return courseId.get();
    }
    
    public String getCourseCode() {
        return courseCode.get();
    }
    
    public String getCourseName() {
        return courseName.get();
    }
    
    public int getCredits() {
        return credits.get();
    }
    
    public String getDescription() {
        return description.get();
    }
    
    // ==================== VALUE SETTERS ====================
    
    public void setCourseId(int courseId) {
        this.courseId.set(courseId);
    }
    
    public void setCourseCode(String courseCode) {
        this.courseCode.set(courseCode);
    }
    
    public void setCourseName(String courseName) {
        this.courseName.set(courseName);
    }
    
    public void setCredits(int credits) {
        this.credits.set(credits);
    }
    
    public void setDescription(String description) {
        this.description.set(description);
    }
    
    @Override
    public String toString() {
        return courseCode.get() + " - " + courseName.get();
    }
}