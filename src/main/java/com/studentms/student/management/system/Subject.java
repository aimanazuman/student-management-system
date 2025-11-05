/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.studentms.student.management.system;

import javafx.beans.property.*;

/**
 * Subject model class with section support
 * Represents a subject that students can enroll in
 */
public class Subject {
    
    private final IntegerProperty subjectId;
    private final StringProperty subjectCode;
    private final StringProperty subjectName;
    private final StringProperty subjectSection;
    private final IntegerProperty credits;
    private final StringProperty description;
    
    /**
     * Constructor for creating a complete Subject object
     */
    public Subject(int subjectId, String subjectCode, String subjectName, 
                   String subjectSection, int credits, String description) {
        this.subjectId = new SimpleIntegerProperty(subjectId);
        this.subjectCode = new SimpleStringProperty(subjectCode);
        this.subjectName = new SimpleStringProperty(subjectName);
        this.subjectSection = new SimpleStringProperty(subjectSection);
        this.credits = new SimpleIntegerProperty(credits);
        this.description = new SimpleStringProperty(description);
    }
    
    /**
     * Constructor for creating a new subject (without ID)
     */
    public Subject(String subjectCode, String subjectName, String subjectSection, 
                   int credits, String description) {
        this.subjectId = new SimpleIntegerProperty(0);
        this.subjectCode = new SimpleStringProperty(subjectCode);
        this.subjectName = new SimpleStringProperty(subjectName);
        this.subjectSection = new SimpleStringProperty(subjectSection);
        this.credits = new SimpleIntegerProperty(credits);
        this.description = new SimpleStringProperty(description);
    }
    
    // ==================== PROPERTY GETTERS ====================
    
    public IntegerProperty subjectIdProperty() {
        return subjectId;
    }
    
    public StringProperty subjectCodeProperty() {
        return subjectCode;
    }
    
    public StringProperty subjectNameProperty() {
        return subjectName;
    }
    
    public StringProperty subjectSectionProperty() {
        return subjectSection;
    }
    
    public IntegerProperty creditsProperty() {
        return credits;
    }
    
    public StringProperty descriptionProperty() {
        return description;
    }
    
    // ==================== VALUE GETTERS ====================
    
    public int getSubjectId() {
        return subjectId.get();
    }
    
    public String getSubjectCode() {
        return subjectCode.get();
    }
    
    public String getSubjectName() {
        return subjectName.get();
    }
    
    public String getSubjectSection() {
        return subjectSection.get();
    }
    
    public int getCredits() {
        return credits.get();
    }
    
    public String getDescription() {
        return description.get();
    }
    
    // ==================== VALUE SETTERS ====================
    
    public void setSubjectId(int subjectId) {
        this.subjectId.set(subjectId);
    }
    
    public void setSubjectCode(String subjectCode) {
        this.subjectCode.set(subjectCode);
    }
    
    public void setSubjectName(String subjectName) {
        this.subjectName.set(subjectName);
    }
    
    public void setSubjectSection(String subjectSection) {
        this.subjectSection.set(subjectSection);
    }
    
    public void setCredits(int credits) {
        this.credits.set(credits);
    }
    
    public void setDescription(String description) {
        this.description.set(description);
    }
    
    /**
     * Returns full subject identifier (code + section)
     */
    public String getFullCode() {
        return subjectCode.get() + "-" + subjectSection.get();
    }
    
    @Override
    public String toString() {
        return subjectCode.get() + "-" + subjectSection.get() + " - " + subjectName.get();
    }
}