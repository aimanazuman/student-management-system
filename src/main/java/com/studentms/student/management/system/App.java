package com.studentms.student.management.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main Application class - Entry point for the Student Management System
 * This class extends Application, which is the base class for all JavaFX applications
 */
public class App extends Application {

    /**
     * The start method is the main entry point for JavaFX applications
     * It's called automatically after the JavaFX runtime is initialized
     * 
     * @param primaryStage The main window (stage) of the application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize the database before loading the UI
            // This creates tables if they don't exist
            System.out.println("Initializing database...");
            DatabaseManager.initializeDatabase();
            System.out.println("Database initialized successfully!");
            
            // Load the Login screen first
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
            Parent root = loader.load();
            
            // Create a scene with the loaded UI
            Scene scene = new Scene(root, 450, 550);
            
            // Configure the primary stage (main window)
            primaryStage.setTitle("Student Management System - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Login window should not be resizable
            
            // Show the window
            primaryStage.show();
            
            System.out.println("Application started successfully!");
            
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog to user
            showErrorDialog("Application Error", 
                          "Failed to load the application interface.\n" + 
                          "Please check that LoginView.fxml exists in the resources folder.");
        } catch (Exception e) {
            System.err.println("Unexpected error during application startup: " + e.getMessage());
            e.printStackTrace();
            
            showErrorDialog("Application Error", 
                          "An unexpected error occurred during startup.\n" + 
                          "Error: " + e.getMessage());
        }
    }
    
    /**
     * The stop method is called when the application is closing
     * Use this to clean up resources, close database connections, etc.
     */
    @Override
    public void stop() {
        System.out.println("Application is closing...");
        // Add any cleanup code here if needed
        // For example, closing database connections, saving user preferences, etc.
    }
    
    /**
     * Helper method to show error dialogs
     */
    private void showErrorDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * The main method is the entry point of the Java application
     * It launches the JavaFX application by calling launch()
     * 
     * @param args Command line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // Launch the JavaFX application
        // This calls the init() method, then start(), and handles the application lifecycle
        launch(args);
    }
}