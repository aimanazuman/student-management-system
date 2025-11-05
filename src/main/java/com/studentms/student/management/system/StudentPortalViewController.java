/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.studentms.student.management.system;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.*;

/**
 * Controller for Student Portal
 * Allows students to view their grades, GPA, and academic progress
 */
public class StudentPortalViewController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label studentCodeLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label statusLabel;
    @FXML private Label cgpaLabel;
    @FXML private Label totalCreditsLabel;
    @FXML private Label subjectCountLabel;
    @FXML private Label statusMessageLabel;
    
    @FXML private ComboBox<String> semesterFilterComboBox;
    @FXML private TextArea semesterGpaArea;
    
    @FXML private TableView<GradeRecord> gradesTable;
    @FXML private TableColumn<GradeRecord, String> subjectCodeColumn;
    @FXML private TableColumn<GradeRecord, String> subjectNameColumn;
    @FXML private TableColumn<GradeRecord, String> sectionColumn;
    @FXML private TableColumn<GradeRecord, Integer> creditsColumn;
    @FXML private TableColumn<GradeRecord, String> semesterColumn;
    @FXML private TableColumn<GradeRecord, Integer> yearColumn;
    @FXML private TableColumn<GradeRecord, String> gradeColumn;
    @FXML private TableColumn<GradeRecord, Double> gradePointColumn;
    
    private ObservableList<GradeRecord> gradesList = FXCollections.observableArrayList();
    private ObservableList<GradeRecord> allGrades = FXCollections.observableArrayList();
    private LoginViewController.UserSession userSession;
    private Student currentStudent;
    
    // Grade point mapping
    private final Map<String, Double> gradePoints = new HashMap<>() {{
        put("A+", 4.0);
        put("A", 4.0);
        put("A-", 3.7);
        put("B+", 3.3);
        put("B", 3.0);
        put("B-", 2.7);
        put("C+", 2.3);
        put("C", 2.0);
        put("C-", 1.7);
        put("D+", 1.3);
        put("D", 1.0);
        put("F", 0.0);
        put("N/A", null);
    }};
    
    @FXML
    public void initialize() {
        // Configure table columns
        subjectCodeColumn.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        sectionColumn.setCellValueFactory(new PropertyValueFactory<>("section"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradePointColumn.setCellValueFactory(new PropertyValueFactory<>("gradePoint"));
        
        gradesTable.setItems(gradesList);
        
        // Populate semester filter
        semesterFilterComboBox.getItems().addAll("All Semesters", "Semester 1", "Semester 2", 
                                                 "Semester 3", "Semester 4", "Semester 5", 
                                                 "Semester 6", "Semester 7", "Semester 8", "Semester 9");
        semesterFilterComboBox.setValue("All Semesters");
    }
    
    /**
     * Sets the user session and loads student data
     */
    public void setUserSession(LoginViewController.UserSession session) {
        this.userSession = session;
        loadStudentData();
    }
    
    /**
     * Loads the current student's data
     */
    private void loadStudentData() {
        if (userSession == null) return;
        
        String email = userSession.getUsername();
        
        try (ResultSet rs = DatabaseManager.getAllStudents()) {
            while (rs.next()) {
                if (rs.getString("email").equals(email)) {
                    currentStudent = new Student(
                        rs.getInt("student_id"),
                        rs.getString("student_code"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("date_of_birth"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("enrollment_date"),
                        rs.getString("status")
                    );
                    
                    displayStudentInfo();
                    loadGrades();
                    break;
                }
            }
        } catch (SQLException e) {
            showError("Error loading student data: " + e.getMessage());
        }
    }
    
    /**
     * Displays student information
     */
    private void displayStudentInfo() {
        if (currentStudent == null) return;
        
        welcomeLabel.setText("Welcome, " + currentStudent.getFullName() + "!");
        studentCodeLabel.setText(currentStudent.getStudentCode());
        fullNameLabel.setText(currentStudent.getFullName());
        emailLabel.setText(currentStudent.getEmail());
        statusLabel.setText(currentStudent.getStatus());
        statusLabel.setStyle(currentStudent.getStatus().equals("Active") ? 
            "-fx-text-fill: #27ae60; -fx-font-weight: bold;" : 
            "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }
    
    /**
     * Loads student's grades
     */
    private void loadGrades() {
        if (currentStudent == null) return;
        
        allGrades.clear();
        gradesList.clear();
        
        try (ResultSet rs = DatabaseManager.getStudentEnrollments(currentStudent.getStudentId())) {
            while (rs.next()) {
                String grade = rs.getString("grade");
                Double gradePoint = gradePoints.get(grade);
                
                GradeRecord record = new GradeRecord(
                    rs.getString("subject_code"),
                    rs.getString("subject_name"),
                    rs.getString("subject_section"),
                    rs.getInt("credits"),
                    rs.getString("semester"),
                    rs.getInt("enrollment_year"),
                    grade,
                    gradePoint != null ? gradePoint : 0.0
                );
                
                allGrades.add(record);
                gradesList.add(record);
            }
            
            calculateStatistics();
            calculateSemesterGPAs();
            
        } catch (SQLException e) {
            showError("Error loading grades: " + e.getMessage());
        }
    }
    
    /**
     * Calculates CGPA and other statistics
     */
    private void calculateStatistics() {
        double totalPoints = 0.0;
        int totalCredits = 0;
        int gradedCredits = 0;
        
        for (GradeRecord record : allGrades) {
            totalCredits += record.getCredits();
            
            if (record.getGradePoint() != null && record.getGradePoint() > 0 && !record.getGrade().equals("N/A")) {
                totalPoints += record.getGradePoint() * record.getCredits();
                gradedCredits += record.getCredits();
            }
        }
        
        double cgpa = gradedCredits > 0 ? totalPoints / gradedCredits : 0.0;
        
        cgpaLabel.setText(String.format("%.2f", cgpa));
        totalCreditsLabel.setText(String.valueOf(totalCredits));
        subjectCountLabel.setText(String.valueOf(allGrades.size()));
        
        statusMessageLabel.setText("Loaded " + allGrades.size() + " subjects | CGPA: " + String.format("%.2f", cgpa));
    }
    
    /**
     * Handles print grade button - generates grade report per semester
     */
    @FXML
    private void handlePrintGrade() {
        if (currentStudent == null) {
            showError("No student data loaded.");
            return;
        }

        String selectedSemester = semesterFilterComboBox.getValue();
        String filename;

        if (selectedSemester == null || selectedSemester.equals("All Semesters")) {
            // Print all grades
            filename = "Grade_Report_" + currentStudent.getStudentCode() + "_All_"
                    + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";

            boolean success = ReportGenerator.generateStudentGradeReport(
                    filename,
                    currentStudent,
                    allGrades,
                    calculateCGPA()
            );

            if (success) {
                showSuccess("Report Generated",
                        "Grade report has been generated successfully!\n\nFile: " + filename);
                statusMessageLabel.setText("Grade report generated: " + filename);
            } else {
                showError("Failed to generate grade report. Check console for details.");
            }
        } else {
            // Print grades for selected semester only
            List<GradeRecord> semesterGrades = new ArrayList<>();
            for (GradeRecord record : allGrades) {
                if (record.getSemester().equals(selectedSemester)) {
                    semesterGrades.add(record);
                }
            }

            if (semesterGrades.isEmpty()) {
                showWarning("No Grades", "No grades found for " + selectedSemester);
                return;
            }

            filename = "Grade_Report_" + currentStudent.getStudentCode() + "_"
                    + selectedSemester.replace(" ", "_") + "_"
                    + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";

            boolean success = ReportGenerator.generateSemesterGradeReport(
                    filename,
                    currentStudent,
                    semesterGrades,
                    selectedSemester,
                    calculateSemesterGPA(semesterGrades)
            );

            if (success) {
                showSuccess("Report Generated",
                        "Grade report for " + selectedSemester + " has been generated!\n\nFile: " + filename);
                statusMessageLabel.setText("Semester grade report generated: " + filename);
            } else {
                showError("Failed to generate grade report. Check console for details.");
            }
        }
    }

    /**
     * Calculates CGPA and returns it
     */
    private double calculateCGPA() {
        double totalPoints = 0.0;
        int gradedCredits = 0;

        for (GradeRecord record : allGrades) {
            if (record.getGradePoint() != null && record.getGradePoint() > 0 && !record.getGrade().equals("N/A")) {
                totalPoints += record.getGradePoint() * record.getCredits();
                gradedCredits += record.getCredits();
            }
        }

        return gradedCredits > 0 ? totalPoints / gradedCredits : 0.0;
    }
    
    /**
     * Calculates GPA for each semester
     */
    private void calculateSemesterGPAs() {
        Map<String, List<GradeRecord>> semesterMap = new HashMap<>();
        
        // Group by semester
        for (GradeRecord record : allGrades) {
            String key = record.getSemester() + " " + record.getYear();
            semesterMap.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        }
        
        // Calculate GPA for each semester
        StringBuilder summary = new StringBuilder();
        summary.append("GPA SUMMARY BY SEMESTER\n");
        summary.append("=".repeat(50)).append("\n\n");
        
        // Sort semesters
        List<String> sortedSemesters = new ArrayList<>(semesterMap.keySet());
        sortedSemesters.sort((a, b) -> {
            String[] aParts = a.split(" ");
            String[] bParts = b.split(" ");
            int yearCompare = Integer.compare(Integer.parseInt(bParts[1]), Integer.parseInt(aParts[1]));
            if (yearCompare != 0) return yearCompare;
            return b.compareTo(a);
        });
        
        for (String semester : sortedSemesters) {
            List<GradeRecord> records = semesterMap.get(semester);
            double totalPoints = 0.0;
            int totalCredits = 0;
            int gradedCredits = 0;
            
            for (GradeRecord record : records) {
                totalCredits += record.getCredits();
                if (record.getGradePoint() != null && record.getGradePoint() > 0 && !record.getGrade().equals("N/A")) {
                    totalPoints += record.getGradePoint() * record.getCredits();
                    gradedCredits += record.getCredits();
                }
            }
            
            double gpa = gradedCredits > 0 ? totalPoints / gradedCredits : 0.0;
            
            summary.append(String.format("%-20s : GPA %.2f (%d credits, %d subjects)\n", 
                semester, gpa, totalCredits, records.size()));
        }
        
        if (sortedSemesters.isEmpty()) {
            summary.append("No grades available yet.\n");
        }
        
        semesterGpaArea.setText(summary.toString());
    }
    
    /**
     * Calculates GPA for a list of grade records
     */
    private double calculateSemesterGPA(List<GradeRecord> grades) {
        double totalPoints = 0.0;
        int gradedCredits = 0;

        for (GradeRecord record : grades) {
            if (record.getGradePoint() != null && record.getGradePoint() > 0 && !record.getGrade().equals("N/A")) {
                totalPoints += record.getGradePoint() * record.getCredits();
                gradedCredits += record.getCredits();
            }
        }

        return gradedCredits > 0 ? totalPoints / gradedCredits : 0.0;
    }
    
    /**
     * Handles semester filter selection
     */
    @FXML
    private void handleSemesterFilter() {
        String selected = semesterFilterComboBox.getValue();
        
        if (selected == null || selected.equals("All Semesters")) {
            gradesList.setAll(allGrades);
        } else {
            gradesList.clear();
            for (GradeRecord record : allGrades) {
                if (record.getSemester().equals(selected)) {
                    gradesList.add(record);
                }
            }
        }
        
        statusMessageLabel.setText("Filtered: " + gradesList.size() + " subjects");
    }
    
    /**
     * Shows all grades
     */
    @FXML
    private void handleShowAll() {
        semesterFilterComboBox.setValue("All Semesters");
        gradesList.setAll(allGrades);
        statusMessageLabel.setText("Showing all " + gradesList.size() + " subjects");
    }
    
    /**
     * Handles logout
     */
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 450, 550);
            Stage stage = (Stage) gradesTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Student Management System - Login");
            stage.setResizable(false);
            stage.centerOnScreen();

            
        } catch (IOException e) {
            showError("Error loading login screen: " + e.getMessage());
        }
    }
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Inner class to represent a grade record for table display
     */
    public static class GradeRecord {
        private final SimpleStringProperty subjectCode;
        private final SimpleStringProperty subjectName;
        private final SimpleStringProperty section;
        private final int credits;
        private final SimpleStringProperty semester;
        private final int year;
        private final SimpleStringProperty grade;
        private final SimpleDoubleProperty gradePoint;
        
        public GradeRecord(String subjectCode, String subjectName, String section,
                          int credits, String semester, int year, String grade, double gradePoint) {
            this.subjectCode = new SimpleStringProperty(subjectCode);
            this.subjectName = new SimpleStringProperty(subjectName);
            this.section = new SimpleStringProperty(section);
            this.credits = credits;
            this.semester = new SimpleStringProperty(semester);
            this.year = year;
            this.grade = new SimpleStringProperty(grade);
            this.gradePoint = new SimpleDoubleProperty(gradePoint);
        }
        
        public String getSubjectCode() { return subjectCode.get(); }
        public String getSubjectName() { return subjectName.get(); }
        public String getSection() { return section.get(); }
        public int getCredits() { return credits; }
        public String getSemester() { return semester.get(); }
        public int getYear() { return year; }
        public String getGrade() { return grade.get(); }
        public Double getGradePoint() { return gradePoint.get(); }
    }
}