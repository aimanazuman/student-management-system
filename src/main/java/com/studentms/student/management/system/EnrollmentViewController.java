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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Redesigned Controller for Subject Assignment and Grade Management
 * Separates subject assignment from grade entry for clarity
 */
public class EnrollmentViewController {
    
    @FXML private ComboBox<Student> studentComboBox;
    @FXML private ComboBox<Subject> subjectComboBox;
    @FXML private ComboBox<String> semesterComboBox;
    @FXML private ComboBox<String> gradeComboBox;
    @FXML private Spinner<Integer> yearSpinner;
    
    @FXML private TableView<EnrollmentRecord> enrollmentTable;
    @FXML private TableColumn<EnrollmentRecord, Integer> enrollmentIdColumn;
    @FXML private TableColumn<EnrollmentRecord, String> subjectCodeColumn;
    @FXML private TableColumn<EnrollmentRecord, String> subjectNameColumn;
    @FXML private TableColumn<EnrollmentRecord, String> sectionColumn;
    @FXML private TableColumn<EnrollmentRecord, Integer> creditsColumn;
    @FXML private TableColumn<EnrollmentRecord, String> semesterColumn;
    @FXML private TableColumn<EnrollmentRecord, Integer> yearColumn;
    @FXML private TableColumn<EnrollmentRecord, String> gradeColumn;
    
    @FXML private Label statusLabel;
    @FXML private Label studentInfoLabel;
    @FXML private Label cgpaLabel;
    @FXML private Label totalCreditsLabel;
    
    @FXML private Button assignButton;
    @FXML private Button removeAssignmentButton;
    
    private ObservableList<EnrollmentRecord> enrollmentList = FXCollections.observableArrayList();
    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private ObservableList<Subject> subjectList = FXCollections.observableArrayList();
    
    // Grade point mapping for GPA calculation
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
    }};
    
    @FXML
    public void initialize() {
        // Configure table columns
        enrollmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentId"));
        subjectCodeColumn.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        sectionColumn.setCellValueFactory(new PropertyValueFactory<>("section"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        
        enrollmentTable.setItems(enrollmentList);
        
        // Populate semester dropdown (1-9)
        semesterComboBox.getItems().addAll("Semester 1", "Semester 2", "Semester 3", 
                                            "Semester 4", "Semester 5", "Semester 6",
                                            "Semester 7", "Semester 8", "Semester 9");
        semesterComboBox.setValue("Semester 1");
        
        // Populate grade dropdown
        gradeComboBox.getItems().addAll("A+", "A", "A-", "B+", "B", "B-", 
                                        "C+", "C", "C-", "D+", "D", "F", "Not Graded");
        gradeComboBox.setValue("Not Graded");
        
        // Configure year spinner
        int currentYear = LocalDate.now().getYear();
        SpinnerValueFactory<Integer> yearValueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(2020, currentYear + 5, currentYear);
        yearSpinner.setValueFactory(yearValueFactory);
        
        // Configure student ComboBox display
        studentComboBox.setCellFactory(lv -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText("");
                } else {
                    setText(student.getStudentCode() + " - " + student.getFullName());
                }
            }
        });
        
        studentComboBox.setButtonCell(new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText("");
                } else {
                    setText(student.getStudentCode() + " - " + student.getFullName());
                }
            }
        });
        
        // Configure subject ComboBox display
        subjectComboBox.setCellFactory(lv -> new ListCell<Subject>() {
            @Override
            protected void updateItem(Subject subject, boolean empty) {
                super.updateItem(subject, empty);
                if (empty || subject == null) {
                    setText("");
                } else {
                    setText(subject.getSubjectCode() + "-" + subject.getSubjectSection() + 
                           " - " + subject.getSubjectName() + " (" + subject.getCredits() + " credits)");
                }
            }
        });
        
        subjectComboBox.setButtonCell(new ListCell<Subject>() {
            @Override
            protected void updateItem(Subject subject, boolean empty) {
                super.updateItem(subject, empty);
                if (empty || subject == null) {
                    setText("");
                } else {
                    setText(subject.getSubjectCode() + "-" + subject.getSubjectSection() + 
                           " (" + subject.getCredits() + " credits)");
                }
            }
        });
        
        // Load initial data
        loadStudents();
        loadSubjects();
        
        updateStatusLabel("Ready - Select a student to begin");
    }
    
    private void loadStudents() {
        studentList.clear();
        
        try (ResultSet rs = DatabaseManager.getAllStudents()) {
            while (rs.next()) {
                Student student = new Student(
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
                studentList.add(student);
            }
            studentComboBox.setItems(studentList);
        } catch (SQLException e) {
            showError("Database Error", "Failed to load students: " + e.getMessage());
        }
    }
    
    private void loadSubjects() {
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
            subjectComboBox.setItems(subjectList);
        } catch (SQLException e) {
            showError("Database Error", "Failed to load subjects: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleStudentSelection() {
        Student selectedStudent = studentComboBox.getValue();
        
        if (selectedStudent != null) {
            loadStudentEnrollments(selectedStudent.getStudentId());
            studentInfoLabel.setText("Student Code: " + selectedStudent.getStudentCode() + 
                                    " | Email: " + selectedStudent.getEmail());
            updateStatusLabel("Loaded assignments for " + selectedStudent.getFullName());
        }
    }
    
    private void loadStudentEnrollments(int studentId) {
        enrollmentList.clear();
        
        try (ResultSet rs = DatabaseManager.getStudentEnrollments(studentId)) {
            while (rs.next()) {
                EnrollmentRecord record = new EnrollmentRecord(
                    rs.getInt("enrollment_id"),
                    rs.getInt("subject_id"),
                    rs.getString("subject_code"),
                    rs.getString("subject_name"),
                    rs.getString("subject_section"),
                    rs.getInt("credits"),
                    rs.getString("semester"),
                    rs.getInt("enrollment_year"),
                    rs.getString("grade") != null ? rs.getString("grade") : "Not Graded"
                );
                enrollmentList.add(record);
            }
            
            calculateAndDisplayCGPA();
            
        } catch (SQLException e) {
            showError("Database Error", "Failed to load enrollments: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleAssignSubject() {
        Student selectedStudent = studentComboBox.getValue();
        Subject selectedSubject = subjectComboBox.getValue();
        String semester = semesterComboBox.getValue();
        int year = yearSpinner.getValue();
        
        if (selectedStudent == null) {
            showWarning("No Student Selected", "Please select a student first.");
            return;
        }
        
        if (selectedSubject == null) {
            showWarning("No Subject Selected", "Please select a subject to assign.");
            return;
        }
        
        // Check if already assigned
        if (DatabaseManager.isStudentEnrolledInSubject(selectedStudent.getStudentId(), 
                                                       selectedSubject.getSubjectId(), 
                                                       semester, year)) {
            showWarning("Already Assigned", 
                       "Student is already assigned this subject for the selected semester.");
            return;
        }
        
        int enrollmentId = DatabaseManager.enrollStudentInSubject(
            selectedStudent.getStudentId(),
            selectedSubject.getSubjectId(),
            semester,
            year
        );
        
        if (enrollmentId > 0) {
            EnrollmentRecord newRecord = new EnrollmentRecord(
                enrollmentId,
                selectedSubject.getSubjectId(),
                selectedSubject.getSubjectCode(),
                selectedSubject.getSubjectName(),
                selectedSubject.getSubjectSection(),
                selectedSubject.getCredits(),
                semester,
                year,
                "Not Graded"
            );
            enrollmentList.add(newRecord);
            
            showSuccess("Success", "Subject assigned successfully!");
            calculateAndDisplayCGPA();
            updateStatusLabel("Subject assigned to " + selectedStudent.getFullName());
        } else {
            showError("Error", "Failed to assign subject.");
        }
    }
    
    @FXML
    private void handleRemoveAssignment() {
        EnrollmentRecord selectedRecord = enrollmentTable.getSelectionModel().getSelectedItem();
        
        if (selectedRecord == null) {
            showWarning("No Selection", "Please select a subject assignment to remove.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Removal");
        confirmAlert.setHeaderText("Remove Subject Assignment");
        confirmAlert.setContentText("Are you sure you want to remove " + 
                                   selectedRecord.getSubjectCode() + " from this student?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = DatabaseManager.deleteEnrollment(selectedRecord.getEnrollmentId());
                
                if (success) {
                    enrollmentList.remove(selectedRecord);
                    showSuccess("Success", "Subject assignment removed!");
                    calculateAndDisplayCGPA();
                    updateStatusLabel("Assignment removed");
                } else {
                    showError("Error", "Failed to remove assignment.");
                }
            }
        });
    }
    
    @FXML
    private void handleUpdateGrade() {
        EnrollmentRecord selectedRecord = enrollmentTable.getSelectionModel().getSelectedItem();
        
        if (selectedRecord == null) {
            showWarning("No Selection", "Please select a subject assignment to grade.");
            return;
        }
        
        String newGrade = gradeComboBox.getValue();
        
        if (newGrade == null || newGrade.equals("Not Graded")) {
            // Allow setting to "Not Graded" to remove grade
            newGrade = null;
        }
        
        boolean success = DatabaseManager.updateGrade(selectedRecord.getEnrollmentId(), newGrade);
        
        if (success) {
            selectedRecord.setGrade(newGrade != null ? newGrade : "Not Graded");
            enrollmentTable.refresh();
            calculateAndDisplayCGPA();
            
            showSuccess("Success", "Grade updated successfully!");
            updateStatusLabel("Grade updated for " + selectedRecord.getSubjectCode());
        } else {
            showError("Error", "Failed to update grade.");
        }
    }
    
    @FXML
    private void handleManageSubjects() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SubjectView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1200, 700);
            Stage stage = (Stage) enrollmentTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Subject Management");
            
        } catch (IOException e) {
            showError("Navigation Error", "Failed to load subject view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBackToStudents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1200, 780);
            Stage stage = (Stage) enrollmentTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Student Management System");
            
        } catch (IOException e) {
            showError("Navigation Error", "Failed to load student view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Calculates and displays CGPA for the selected student
     * CGPA = Total Grade Points / Total Graded Credits
     */
    private void calculateAndDisplayCGPA() {
        double totalPoints = 0.0;
        int totalCredits = 0;
        int gradedCredits = 0;
        
        for (EnrollmentRecord record : enrollmentList) {
            String grade = record.getGrade();
            int credits = record.getCredits();
            
            totalCredits += credits;
            
            // Only count grades that have been assigned
            if (gradePoints.containsKey(grade)) {
                totalPoints += gradePoints.get(grade) * credits;
                gradedCredits += credits;
            }
        }
        
        if (gradedCredits > 0) {
            double cgpa = totalPoints / gradedCredits;
            cgpaLabel.setText(String.format("CGPA: %.2f", cgpa));
        } else {
            cgpaLabel.setText("CGPA: N/A");
        }
        
        totalCreditsLabel.setText("Total Credits: " + totalCredits + 
                                 " (Graded: " + gradedCredits + ")");
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
    
    /**
     * Inner class to represent an enrollment record for table display
     */
    public static class EnrollmentRecord {
        private final int enrollmentId;
        private final int subjectId;
        private final String subjectCode;
        private final String subjectName;
        private final String section;
        private final int credits;
        private final String semester;
        private final int year;
        private String grade;
        
        public EnrollmentRecord(int enrollmentId, int subjectId, String subjectCode, 
                               String subjectName, String section, int credits,
                               String semester, int year, String grade) {
            this.enrollmentId = enrollmentId;
            this.subjectId = subjectId;
            this.subjectCode = subjectCode;
            this.subjectName = subjectName;
            this.section = section;
            this.credits = credits;
            this.semester = semester;
            this.year = year;
            this.grade = grade;
        }
        
        public int getEnrollmentId() { return enrollmentId; }
        public int getSubjectId() { return subjectId; }
        public String getSubjectCode() { return subjectCode; }
        public String getSubjectName() { return subjectName; }
        public String getSection() { return section; }
        public int getCredits() { return credits; }
        public String getSemester() { return semester; }
        public int getYear() { return year; }
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
    }
}