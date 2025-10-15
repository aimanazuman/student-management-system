/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.studentms.student.management.system;

import javafx.beans.property.*;

/**
 * Enrollment model class
 * Represents a student's enrollment in a course with grade information
 */
public class Enrollment {
    
    private final IntegerProperty enrollmentId;
    private final IntegerProperty studentId;
    private final IntegerProperty courseId;
    private final StringProperty courseCode;
    private final StringProperty courseName;
    private final IntegerProperty credits;
    private final StringProperty semester;
    private final IntegerProperty enrollmentYear;
    private final StringProperty grade;
    
    /**
     * Complete constructor with all fields
     */
    public Enrollment(int enrollmentId, int studentId, int courseId,
                     String courseCode, String courseName, int credits,
                     String semester, int enrollmentYear, String grade) {
        this.enrollmentId = new SimpleIntegerProperty(enrollmentId);
        this.studentId = new SimpleIntegerProperty(studentId);
        this.courseId = new SimpleIntegerProperty(courseId);
        this.courseCode = new SimpleStringProperty(courseCode);
        this.courseName = new SimpleStringProperty(courseName);
        this.credits = new SimpleIntegerProperty(credits);
        this.semester = new SimpleStringProperty(semester);
        this.enrollmentYear = new SimpleIntegerProperty(enrollmentYear);
        this.grade = new SimpleStringProperty(grade != null ? grade : "N/A");
    }
    
    /**
     * Constructor for new enrollment (without grade)
     */
    public Enrollment(int studentId, int courseId, String courseCode, 
                     String courseName, int credits, String semester, int enrollmentYear) {
        this.enrollmentId = new SimpleIntegerProperty(0);
        this.studentId = new SimpleIntegerProperty(studentId);
        this.courseId = new SimpleIntegerProperty(courseId);
        this.courseCode = new SimpleStringProperty(courseCode);
        this.courseName = new SimpleStringProperty(courseName);
        this.credits = new SimpleIntegerProperty(credits);
        this.semester = new SimpleStringProperty(semester);
        this.enrollmentYear = new SimpleIntegerProperty(enrollmentYear);
        this.grade = new SimpleStringProperty("N/A");
    }
    
    // ==================== PROPERTY GETTERS ====================
    
    public IntegerProperty enrollmentIdProperty() {
        return enrollmentId;
    }
    
    public IntegerProperty studentIdProperty() {
        return studentId;
    }
    
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
    
    public StringProperty semesterProperty() {
        return semester;
    }
    
    public IntegerProperty enrollmentYearProperty() {
        return enrollmentYear;
    }
    
    public StringProperty gradeProperty() {
        return grade;
    }
    
    // ==================== VALUE GETTERS ====================
    
    public int getEnrollmentId() {
        return enrollmentId.get();
    }
    
    public int getStudentId() {
        return studentId.get();
    }
    
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
    
    public String getSemester() {
        return semester.get();
    }
    
    public int getEnrollmentYear() {
        return enrollmentYear.get();
    }
    
    public String getGrade() {
        return grade.get();
    }
    
    // ==================== VALUE SETTERS ====================
    
    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId.set(enrollmentId);
    }
    
    public void setGrade(String grade) {
        this.grade.set(grade != null ? grade : "N/A");
    }
    
    @Override
    public String toString() {
        return courseCode.get() + " - " + courseName.get() + " (" + semester.get() + " " + enrollmentYear.get() + ")";
    }
}