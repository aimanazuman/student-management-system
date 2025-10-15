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
 * Controller for Enrollment Management
 * Handles course enrollments and grade management for students
 */
public class EnrollmentViewController {
    
    @FXML private ComboBox<Student> studentComboBox;
    @FXML private ComboBox<Course> courseComboBox;
    @FXML private ComboBox<String> semesterComboBox;
    @FXML private ComboBox<String> gradeComboBox;
    @FXML private Spinner<Integer> yearSpinner;
    
    @FXML private TableView<Enrollment> enrollmentTable;
    @FXML private TableColumn<Enrollment, Integer> enrollmentIdColumn;
    @FXML private TableColumn<Enrollment, String> courseCodeColumn;
    @FXML private TableColumn<Enrollment, String> courseNameColumn;
    @FXML private TableColumn<Enrollment, Integer> creditsColumn;
    @FXML private TableColumn<Enrollment, String> semesterColumn;
    @FXML private TableColumn<Enrollment, Integer> yearColumn;
    @FXML private TableColumn<Enrollment, String> gradeColumn;
    
    @FXML private Label statusLabel;
    @FXML private Label studentInfoLabel;
    @FXML private Label gpaLabel;
    @FXML private Label totalCreditsLabel;
    
    @FXML private Button enrollButton;
    @FXML private Button updateGradeButton;
    @FXML private Button dropCourseButton;
    
    private ObservableList<Enrollment> enrollmentList = FXCollections.observableArrayList();
    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private ObservableList<Course> courseList = FXCollections.observableArrayList();
    
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
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentYear"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        
        enrollmentTable.setItems(enrollmentList);
        
        // Add selection listener for enrollment table
        enrollmentTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    populateFormWithEnrollment(newValue);
                }
            }
        );
        
        // Populate semester dropdown
        semesterComboBox.getItems().addAll("Semester 1", "Semester 2", "Summer");
        semesterComboBox.setValue("Semester 1");
        
        // Populate grade dropdown
        gradeComboBox.getItems().addAll("A+", "A", "A-", "B+", "B", "B-", 
                                        "C+", "C", "C-", "D+", "D", "F", "N/A");
        gradeComboBox.setValue("N/A");
        
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
                setText(empty || student == null ? "" : student.getFullName() + " (" + student.getEmail() + ")");
            }
        });
        
        studentComboBox.setButtonCell(new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                setText(empty || student == null ? "" : student.getFullName());
            }
        });
        
        // Configure course ComboBox display
        courseComboBox.setCellFactory(lv -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                setText(empty || course == null ? "" : course.getCourseCode() + " - " + course.getCourseName());
            }
        });
        
        courseComboBox.setButtonCell(new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                setText(empty || course == null ? "" : course.getCourseCode() + " - " + course.getCourseName());
            }
        });
        
        // Load initial data
        loadStudents();
        loadCourses();
        
        updateStatusLabel("Ready - Select a student to begin");
    }
    
    private void loadStudents() {
        studentList.clear();
        
        try (ResultSet rs = DatabaseManager.getAllStudents()) {
            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("student_id"),
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
    
    private void loadCourses() {
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
            courseComboBox.setItems(courseList);
        } catch (SQLException e) {
            showError("Database Error", "Failed to load courses: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleStudentSelection() {
        Student selectedStudent = studentComboBox.getValue();
        
        if (selectedStudent != null) {
            loadStudentEnrollments(selectedStudent.getStudentId());
            studentInfoLabel.setText("Student ID: " + selectedStudent.getStudentId() + 
                                    " | Status: " + selectedStudent.getStatus());
            updateStatusLabel("Loaded enrollments for " + selectedStudent.getFullName());
        }
    }
    
    private void loadStudentEnrollments(int studentId) {
        enrollmentList.clear();
        
        try (ResultSet rs = DatabaseManager.getStudentEnrollments(studentId)) {
            while (rs.next()) {
                Enrollment enrollment = new Enrollment(
                    rs.getInt("enrollment_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getInt("credits"),
                    rs.getString("semester"),
                    rs.getInt("enrollment_year"),
                    rs.getString("grade")
                );
                enrollmentList.add(enrollment);
            }
            
            calculateAndDisplayGPA();
            
        } catch (SQLException e) {
            showError("Database Error", "Failed to load enrollments: " + e.getMessage());
        }
    }
    
    private void populateFormWithEnrollment(Enrollment enrollment) {
        // Find and select the course
        for (Course course : courseList) {
            if (course.getCourseId() == enrollment.getCourseId()) {
                courseComboBox.setValue(course);
                break;
            }
        }
        
        semesterComboBox.setValue(enrollment.getSemester());
        yearSpinner.getValueFactory().setValue(enrollment.getEnrollmentYear());
        gradeComboBox.setValue(enrollment.getGrade());
    }
    
    @FXML
    private void handleEnrollStudent() {
        Student selectedStudent = studentComboBox.getValue();
        Course selectedCourse = courseComboBox.getValue();
        String semester = semesterComboBox.getValue();
        int year = yearSpinner.getValue();
        String grade = gradeComboBox.getValue();
        
        if (selectedStudent == null) {
            showWarning("No Student Selected", "Please select a student first.");
            return;
        }
        
        if (selectedCourse == null) {
            showWarning("No Course Selected", "Please select a course to enroll.");
            return;
        }
        
        // Check if already enrolled
        if (DatabaseManager.isStudentEnrolled(selectedStudent.getStudentId(), 
                                             selectedCourse.getCourseId(), 
                                             semester, year)) {
            showWarning("Already Enrolled", 
                       "Student is already enrolled in this course for the selected semester.");
            return;
        }
        
        int enrollmentId = DatabaseManager.enrollStudentInCourse(
            selectedStudent.getStudentId(),
            selectedCourse.getCourseId(),
            semester,
            year
        );
        
        if (enrollmentId > 0) {
            // If a grade was selected, update it immediately
            if (grade != null && !grade.equals("N/A")) {
                DatabaseManager.updateGrade(enrollmentId, grade);
            }
            
            Enrollment newEnrollment = new Enrollment(
                enrollmentId,
                selectedStudent.getStudentId(),
                selectedCourse.getCourseId(),
                selectedCourse.getCourseCode(),
                selectedCourse.getCourseName(),
                selectedCourse.getCredits(),
                semester,
                year,
                grade != null && !grade.equals("N/A") ? grade : "N/A"
            );
            enrollmentList.add(newEnrollment);
            
            showSuccess("Success", "Student enrolled successfully!");
            handleClearForm();
            calculateAndDisplayGPA();
            updateStatusLabel("Student enrolled in " + selectedCourse.getCourseCode());
        } else {
            showError("Error", "Failed to enroll student.");
        }
    }
    
    @FXML
    private void handleUpdateGrade() {
        Enrollment selectedEnrollment = enrollmentTable.getSelectionModel().getSelectedItem();
        
        if (selectedEnrollment == null) {
            showWarning("No Selection", "Please select an enrollment to update grade.");
            return;
        }
        
        String newGrade = gradeComboBox.getValue();
        
        if (newGrade == null || newGrade.equals("N/A")) {
            showWarning("Invalid Grade", "Please select a valid grade.");
            return;
        }
        
        boolean success = DatabaseManager.updateGrade(selectedEnrollment.getEnrollmentId(), newGrade);
        
        if (success) {
            selectedEnrollment.setGrade(newGrade);
            enrollmentTable.refresh();
            calculateAndDisplayGPA();
            
            showSuccess("Success", "Grade updated successfully!");
            updateStatusLabel("Grade updated for " + selectedEnrollment.getCourseCode());
        } else {
            showError("Error", "Failed to update grade.");
        }
    }
    
    @FXML
    private void handleDropCourse() {
        Enrollment selectedEnrollment = enrollmentTable.getSelectionModel().getSelectedItem();
        
        if (selectedEnrollment == null) {
            showWarning("No Selection", "Please select a course enrollment to drop.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Drop");
        confirmAlert.setHeaderText("Drop Course");
        confirmAlert.setContentText("Are you sure you want to drop " + 
                                   selectedEnrollment.getCourseCode() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = DatabaseManager.deleteEnrollment(selectedEnrollment.getEnrollmentId());
                
                if (success) {
                    enrollmentList.remove(selectedEnrollment);
                    showSuccess("Success", "Course dropped successfully!");
                    handleClearForm();
                    calculateAndDisplayGPA();
                    updateStatusLabel("Course dropped");
                } else {
                    showError("Error", "Failed to drop course.");
                }
            }
        });
    }
    
    @FXML
    private void handleClearForm() {
        courseComboBox.setValue(null);
        semesterComboBox.setValue("Semester 1");
        gradeComboBox.setValue("N/A");
        yearSpinner.getValueFactory().setValue(LocalDate.now().getYear());
        enrollmentTable.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void handleManageCourses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1200, 700);
            Stage stage = (Stage) enrollmentTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Course Management");
            
        } catch (IOException e) {
            showError("Navigation Error", "Failed to load course view: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBackToStudents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1200, 700);
            Stage stage = (Stage) enrollmentTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Student Management System");
            
        } catch (IOException e) {
            showError("Navigation Error", "Failed to load student view: " + e.getMessage());
        }
    }
    
    /**
     * Calculates and displays GPA for the selected student
     */
    private void calculateAndDisplayGPA() {
        double totalPoints = 0.0;
        int totalCredits = 0;
        int gradedCredits = 0;
        
        for (Enrollment enrollment : enrollmentList) {
            String grade = enrollment.getGrade();
            int credits = enrollment.getCredits();
            
            totalCredits += credits;
            
            if (gradePoints.containsKey(grade)) {
                totalPoints += gradePoints.get(grade) * credits;
                gradedCredits += credits;
            }
        }
        
        if (gradedCredits > 0) {
            double gpa = totalPoints / gradedCredits;
            gpaLabel.setText(String.format("GPA: %.2f", gpa));
        } else {
            gpaLabel.setText("GPA: N/A");
        }
        
        totalCreditsLabel.setText("Total Credits: " + totalCredits);
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