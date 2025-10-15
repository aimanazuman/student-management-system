/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.studentms.student.management.system;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller for the Login screen
 * Handles user authentication and role-based access
 */
public class LoginViewController {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private CheckBox rememberMeCheckBox;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    
    // Store the current logged-in user information
    private static UserSession currentUser;
    
    /**
     * Initialize method called after FXML loading
     */
    @FXML
    public void initialize() {
        // Populate role dropdown
        roleComboBox.getItems().addAll("Student Record Administrator", "Lecturer", "Student");
        roleComboBox.setValue("Student Record Administrator"); // Default selection
        
        // Clear any previous error messages
        errorLabel.setText("");
        
        // Add listener to clear error when user types
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> errorLabel.setText(""));
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> errorLabel.setText(""));
    }
    
    /**
     * Handles the login button click
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();
        
        // Validate inputs
        if (username.isEmpty()) {
            showError("Please enter your username");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter your password");
            passwordField.requestFocus();
            return;
        }
        
        if (role == null) {
            showError("Please select your role");
            roleComboBox.requestFocus();
            return;
        }
        
        // Authenticate user
        if (authenticateUser(username, password, role)) {
            // Store user session
            currentUser = new UserSession(username, role);
            
            // Show success (optional - remove for production)
            System.out.println("Login successful: " + username + " as " + role);
            
            // Open main application window
            openMainWindow();
        } else {
            showError("Invalid username, password, or role combination");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }
    
    /**
     * Authenticates user credentials
     * In a real application, this would query the database
     * For this demo, we use hardcoded credentials
     */
    private boolean authenticateUser(String username, String password, String role) {
        // Default credentials for demonstration
        // In production, these should be stored securely in the database with hashed passwords
        
        if (role.equals("Student Record Administrator")) {
            return username.equals("admin") && password.equals("admin123");
        } else if (role.equals("Lecturer")) {
            return username.equals("lecturer") && password.equals("lecturer123");
        } else if (role.equals("Student")) {
            return username.equals("student") && password.equals("student123");
        }
        
        return false;
    }
    
    /**
     * Opens the main application window after successful login
     */
    private void openMainWindow() {
        try {
            // Load the main student management view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentView.fxml"));
            Parent root = loader.load();
            
            // Create new scene
            Scene scene = new Scene(root, 1200, 700);
            
            // Get the current stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Set the new scene
            stage.setScene(scene);
            stage.setTitle("Student Management System - " + currentUser.getRole());
            stage.centerOnScreen();
            
            // Apply role-based permissions
            StudentViewController controller = loader.getController();
            controller.setUserSession(currentUser);
            
        } catch (IOException e) {
            showError("Error loading main window: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Displays error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
    }
    
    /**
     * Gets the current logged-in user
     */
    public static UserSession getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Inner class to store user session information
     */
    public static class UserSession {
        private final String username;
        private final String role;
        
        public UserSession(String username, String role) {
            this.username = username;
            this.role = role;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getRole() {
            return role;
        }
        
        public boolean isAdmin() {
            return "Student Record Administrator".equals(role);
        }
        
        public boolean isLecturer() {
            return "Lecturer".equals(role);
        }
        
        public boolean isStudent() {
            return "Student".equals(role);
        }
    }
}