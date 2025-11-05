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
 * Controller for Subject Management
 */
public class SubjectViewController {
    
    @FXML private TableView<Subject> subjectTable;
    @FXML private TableColumn<Subject, Integer> subjectIdColumn;
    @FXML private TableColumn<Subject, String> subjectCodeColumn;
    @FXML private TableColumn<Subject, String> subjectNameColumn;
    @FXML private TableColumn<Subject, String> sectionColumn;
    @FXML private TableColumn<Subject, Integer> creditsColumn;
    @FXML private TableColumn<Subject, String> descriptionColumn;
    
    @FXML private TextField subjectCodeField;
    @FXML private TextField subjectNameField;
    @FXML private TextField sectionField;
    @FXML private Spinner<Integer> creditsSpinner;
    @FXML private TextArea descriptionArea;
    
    @FXML private Button addSubjectButton;
    @FXML private Button updateSubjectButton;
    @FXML private Button deleteSubjectButton;
    @FXML private Button clearSubjectButton;
    
    @FXML private Label statusLabel;
    
    private ObservableList<Subject> subjectList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // Configure table columns
        subjectIdColumn.setCellValueFactory(new PropertyValueFactory<>("subjectId"));
        subjectCodeColumn.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        sectionColumn.setCellValueFactory(new PropertyValueFactory<>("subjectSection"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        subjectTable.setItems(subjectList);
        
        // Add selection listener
        subjectTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    populateFormWithSubject(newValue);
                }
            }
        );
        
        // Configure credits spinner (1-6 credits)
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 3);
        creditsSpinner.setValueFactory(valueFactory);
        
        // Load subjects
        loadSubjectData();
        
        updateStatusLabel("Ready - " + subjectList.size() + " subjects loaded");
    }
    
    private void loadSubjectData() {
        subjectList.clear();
        
        try (ResultSet rs = DatabaseManager.getAllSubjects()) {
            while (rs.next()) {
                Subject subject = new Subject(
                    rs.getInt("subject_id"),
                    rs.getString("subject_code"),
                    rs.getString("subject_name"),
                    rs.getString("subject_section"),
                    rs.getInt("credits"),
                    rs.getString("description")
                );
                subjectList.add(subject);
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load subjects: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void populateFormWithSubject(Subject subject) {
        subjectCodeField.setText(subject.getSubjectCode());
        subjectNameField.setText(subject.getSubjectName());
        sectionField.setText(subject.getSubjectSection());
        creditsSpinner.getValueFactory().setValue(subject.getCredits());
        descriptionArea.setText(subject.getDescription());
    }
    
    @FXML
    private void handleAddSubject() {
        if (!validateInput()) {
            return;
        }
        
        String subjectCode = subjectCodeField.getText().trim();
        String subjectName = subjectNameField.getText().trim();
        String section = sectionField.getText().trim();
        int credits = creditsSpinner.getValue();
        String description = descriptionArea.getText().trim();
        
        int newId = DatabaseManager.createSubject(subjectCode, subjectName, section, credits, description);
        
        if (newId > 0) {
            Subject newSubject = new Subject(newId, subjectCode, subjectName, section, credits, description);
            subjectList.add(newSubject);
            
            showSuccess("Success", "Subject added successfully!");
            handleClearForm();
            updateStatusLabel("Subject added - Total: " + subjectList.size());
        } else {
            showError("Error", "Failed to add subject. Subject code + section combination may already exist.");
        }
    }
    
    @FXML
    private void handleUpdateSubject() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        
        if (selectedSubject == null) {
            showWarning("No Selection", "Please select a subject to update.");
            return;
        }
        
        if (!validateInput()) {
            return;
        }
        
        String subjectCode = subjectCodeField.getText().trim();
        String subjectName = subjectNameField.getText().trim();
        String section = sectionField.getText().trim();
        int credits = creditsSpinner.getValue();
        String description = descriptionArea.getText().trim();
        
        boolean success = DatabaseManager.updateSubject(
            selectedSubject.getSubjectId(), subjectCode, subjectName, section, credits, description
        );
        
        if (success) {
            selectedSubject.setSubjectCode(subjectCode);
            selectedSubject.setSubjectName(subjectName);
            selectedSubject.setSubjectSection(section);
            selectedSubject.setCredits(credits);
            selectedSubject.setDescription(description);
            
            subjectTable.refresh();
            
            showSuccess("Success", "Subject updated successfully!");
            updateStatusLabel("Subject updated");
        } else {
            showError("Error", "Failed to update subject.");
        }
    }
    
    @FXML
    private void handleDeleteSubject() {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        
        if (selectedSubject == null) {
            showWarning("No Selection", "Please select a subject to delete.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Subject");
        confirmAlert.setContentText("Are you sure you want to delete " + 
                                   selectedSubject.getSubjectCode() + "-" + selectedSubject.getSubjectSection() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = DatabaseManager.deleteSubject(selectedSubject.getSubjectId());
                
                if (success) {
                    subjectList.remove(selectedSubject);
                    showSuccess("Success", "Subject deleted successfully!");
                    handleClearForm();
                    updateStatusLabel("Subject deleted - Total: " + subjectList.size());
                } else {
                    showError("Error", "Failed to delete subject.");
                }
            }
        });
    }
    
    @FXML
    private void handleClearForm() {
        subjectCodeField.clear();
        subjectNameField.clear();
        sectionField.clear();
        creditsSpinner.getValueFactory().setValue(3);
        descriptionArea.clear();
        subjectTable.getSelectionModel().clearSelection();
        updateStatusLabel("Form cleared");
    }
    
    @FXML
    private void handleBackToStudents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1200, 780);
            Stage stage = (Stage) subjectTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Student Management System");
            
        } catch (IOException e) {
            showError("Navigation Error", "Failed to load student view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validateInput() {
        if (subjectCodeField.getText().trim().isEmpty()) {
            showWarning("Validation Error", "Please enter subject code.");
            return false;
        }
        
        if (subjectNameField.getText().trim().isEmpty()) {
            showWarning("Validation Error", "Please enter subject name.");
            return false;
        }
        
        if (sectionField.getText().trim().isEmpty()) {
            showWarning("Validation Error", "Please enter section.");
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