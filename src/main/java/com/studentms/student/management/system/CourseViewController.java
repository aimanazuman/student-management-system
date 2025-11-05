/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.studentms.student.management.system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller for Course Management
 */
public class CourseViewController {
    
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, Integer> courseIdColumn;
    @FXML private TableColumn<Course, String> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, Integer> courseCreditsColumn;
    @FXML private TableColumn<Course, String> courseDescriptionColumn;
    
    @FXML private TextField courseCodeField;
    @FXML private TextField courseNameField;
    @FXML private Spinner<Integer> creditsSpinner;
    @FXML private TextArea descriptionArea;
    
    @FXML private Button addCourseButton;
    @FXML private Button updateCourseButton;
    @FXML private Button deleteCourseButton;
    @FXML private Button clearCourseButton;
    
    @FXML private Label statusLabel;
    
    private ObservableList<Course> courseList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // Configure table columns
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseCreditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        courseDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        courseTable.setItems(courseList);
        
        // Add selection listener
        courseTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    populateFormWithCourse(newValue);
                }
            }
        );
        
        // Configure credits spinner (1-6 credits)
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 3);
        creditsSpinner.setValueFactory(valueFactory);
        
        // Load courses
        loadCourseData();
        
        updateStatusLabel("Ready - " + courseList.size() + " courses loaded");
    }
    
    private void loadCourseData() {
        courseList.clear();
        
        try (ResultSet rs = DatabaseManager.getAllCourses()) {
            while (rs.next()) {
                Course course = new Course(
                    rs.getInt("course_id"),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getInt("credits"),
                    rs.getString("description")
                );
                courseList.add(course);
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load courses: " + e.getMessage());
        }
    }
    
    private void populateFormWithCourse(Course course) {
        courseCodeField.setText(course.getCourseCode());
        courseNameField.setText(course.getCourseName());
        creditsSpinner.getValueFactory().setValue(course.getCredits());
        descriptionArea.setText(course.getDescription());
    }
    
    @FXML
    private void handleAddCourse() {
        if (!validateInput()) {
            return;
        }
        
        String courseCode = courseCodeField.getText().trim();
        String courseName = courseNameField.getText().trim();
        int credits = creditsSpinner.getValue();
        String description = descriptionArea.getText().trim();
        
        int newId = DatabaseManager.createCourse(courseCode, courseName, credits, description);
        
        if (newId > 0) {
            Course newCourse = new Course(newId, courseCode, courseName, credits, description);
            courseList.add(newCourse);
            
            showSuccess("Success", "Course added successfully!");
            handleClearForm();
            updateStatusLabel("Course added - Total: " + courseList.size());
        } else {
            showError("Error", "Failed to add course. Course code may already exist.");
        }
    }
    
    @FXML
    private void handleUpdateCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        
        if (selectedCourse == null) {
            showWarning("No Selection", "Please select a course to update.");
            return;
        }
        
        if (!validateInput()) {
            return;
        }
        
        String courseCode = courseCodeField.getText().trim();
        String courseName = courseNameField.getText().trim();
        int credits = creditsSpinner.getValue();
        String description = descriptionArea.getText().trim();
        
        boolean success = DatabaseManager.updateCourse(
            selectedCourse.getCourseId(), courseCode, courseName, credits, description
        );
        
        if (success) {
            selectedCourse.setCourseCode(courseCode);
            selectedCourse.setCourseName(courseName);
            selectedCourse.setCredits(credits);
            selectedCourse.setDescription(description);
            
            courseTable.refresh();
            
            showSuccess("Success", "Course updated successfully!");
            updateStatusLabel("Course updated");
        } else {
            showError("Error", "Failed to update course.");
        }
    }
    
    @FXML
    private void handleDeleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        
        if (selectedCourse == null) {
            showWarning("No Selection", "Please select a course to delete.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Course");
        confirmAlert.setContentText("Are you sure you want to delete " + 
                                   selectedCourse.getCourseCode() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = DatabaseManager.deleteCourse(selectedCourse.getCourseId());
                
                if (success) {
                    courseList.remove(selectedCourse);
                    showSuccess("Success", "Course deleted successfully!");
                    handleClearForm();
                    updateStatusLabel("Course deleted - Total: " + courseList.size());
                } else {
                    showError("Error", "Failed to delete course.");
                }
            }
        });
    }
    
    @FXML
    private void handleClearForm() {
        courseCodeField.clear();
        courseNameField.clear();
        creditsSpinner.getValueFactory().setValue(3);
        descriptionArea.clear();
        courseTable.getSelectionModel().clearSelection();
        updateStatusLabel("Form cleared");
    }
    
    @FXML
    private void handleBackToStudents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1200, 780);
            Stage stage = (Stage) courseTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Student Management System");
            
        } catch (IOException e) {
            showError("Navigation Error", "Failed to load student view: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        if (courseCodeField.getText().trim().isEmpty()) {
            showWarning("Validation Error", "Please enter course code.");
            return false;
        }
        
        if (courseNameField.getText().trim().isEmpty()) {
            showWarning("Validation Error", "Please enter course name.");
            return false;
        }
        
        return true;
    }
    
    private void updateStatusLabel(String message) {
        statusLabel.setText(message);
    }
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}