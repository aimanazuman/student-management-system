/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.studentms.student.management.system;

import javafx.beans.property.*;

/**
 * Student model class using JavaFX Property objects
 * Properties allow automatic UI updates when values change (data binding)
 * This is crucial for TableView to display and update data correctly
 */
public class Student {
    
    // IntegerProperty for numeric ID
    private final IntegerProperty studentId;
    private final StringProperty studentCode;
    
    // StringProperty for text fields - these automatically notify JavaFX when changed
    private final StringProperty fullName;
    private final StringProperty email;
    private final StringProperty phone;
    private final StringProperty dateOfBirth;
    private final StringProperty gender;
    private final StringProperty address;
    private final StringProperty enrollmentDate;
    private final StringProperty status;
    
    /**
     * Constructor for creating a complete Student object with all fields
     * This is used when loading existing students from the database
     */
    public Student(int studentId, String studentCode, String fullName, String email, String phone,
                   String dateOfBirth, String gender, String address,
                   String enrollmentDate, String status) {
        
        this.studentId = new SimpleIntegerProperty(studentId);
        this.studentCode = new SimpleStringProperty(studentCode);
        this.fullName = new SimpleStringProperty(fullName);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.dateOfBirth = new SimpleStringProperty(dateOfBirth);
        this.gender = new SimpleStringProperty(gender);
        this.address = new SimpleStringProperty(address);
        this.enrollmentDate = new SimpleStringProperty(enrollmentDate);
        this.status = new SimpleStringProperty(status);
    }
    
    /**
     * Constructor for creating a new student (without ID)
     * The ID will be assigned by the database when inserted
     */
    public Student(String studentCode, String fullName, String email, String phone,
                   String dateOfBirth, String gender, String address,
                   String enrollmentDate) {
        
        this.studentId = new SimpleIntegerProperty(0); // Temporary, will be set after DB insert
        this.studentCode = new SimpleStringProperty(studentCode);
        this.fullName = new SimpleStringProperty(fullName);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.dateOfBirth = new SimpleStringProperty(dateOfBirth);
        this.gender = new SimpleStringProperty(gender);
        this.address = new SimpleStringProperty(address);
        this.enrollmentDate = new SimpleStringProperty(enrollmentDate);
        this.status = new SimpleStringProperty("Active");
    }
    
    // ==================== PROPERTY GETTERS ====================
    // These return the Property objects themselves, needed for TableView binding
    
    public IntegerProperty studentIdProperty() {
        return studentId;
    }
    
    public StringProperty studentCodeProperty() {
        return studentCode;
    }
    
    public StringProperty fullNameProperty() {
        return fullName;
    }
    
    public StringProperty emailProperty() {
        return email;
    }
    
    public StringProperty phoneProperty() {
        return phone;
    }
    
    public StringProperty dateOfBirthProperty() {
        return dateOfBirth;
    }
    
    public StringProperty genderProperty() {
        return gender;
    }
    
    public StringProperty addressProperty() {
        return address;
    }
    
    public StringProperty enrollmentDateProperty() {
        return enrollmentDate;
    }
    
    public StringProperty statusProperty() {
        return status;
    }
    
    // ==================== VALUE GETTERS ====================
    // These return the actual values, used in normal Java code
    
    public int getStudentId() {
        return studentId.get();
    }

    public String getStudentCode() {
        return studentCode.get();
    }
    
    public String getFullName() {
        return fullName.get();
    }
    
    public String getEmail() {
        return email.get();
    }
    
    public String getPhone() {
        return phone.get();
    }
    
    public String getDateOfBirth() {
        return dateOfBirth.get();
    }
    
    public String getGender() {
        return gender.get();
    }
    
    public String getAddress() {
        return address.get();
    }
    
    public String getEnrollmentDate() {
        return enrollmentDate.get();
    }
    
    public String getStatus() {
        return status.get();
    }
    
    // ==================== VALUE SETTERS ====================
    // These update the values and automatically notify any bound UI components
    
    public void setStudentId(int studentId) {
        this.studentId.set(studentId);
    }
    
    public void setStudentCode(String studentCode) {
        this.studentCode.set(studentCode);
    }
    
    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }
    
    public void setEmail(String email) {
        this.email.set(email);
    }
    
    public void setPhone(String phone) {
        this.phone.set(phone);
    }
    
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth.set(dateOfBirth);
    }
    
    public void setGender(String gender) {
        this.gender.set(gender);
    }
    
    public void setAddress(String address) {
        this.address.set(address);
    }
    
    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate.set(enrollmentDate);
    }
    
    public void setStatus(String status) {
        this.status.set(status);
    }
    
    /**
     * Override toString for debugging purposes
     * Makes it easy to see student information when printing to console
     */
    @Override
    public String toString() {
        return "Student{" +
                "ID=" + getStudentId() +
                ", Name='" + getFullName() + '\'' +
                ", Email='" + getEmail() + '\'' +
                ", Status='" + getStatus() + '\'' +
                '}';
    }
}