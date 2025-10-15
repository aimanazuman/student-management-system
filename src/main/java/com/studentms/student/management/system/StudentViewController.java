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
import java.time.format.DateTimeFormatter;

/**
 * Controller class for the Student Management interface.
 * Handles all user interactions and connects the UI with the database.
 */
public class StudentViewController {

    // ==================== FXML INJECTED UI COMPONENTS ====================

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> phoneColumn;
    @FXML private TableColumn<Student, String> dobColumn;
    @FXML private TableColumn<Student, String> genderColumn;
    @FXML private TableColumn<Student, String> addressColumn;
    @FXML private TableColumn<Student, String> enrollDateColumn;
    @FXML private TableColumn<Student, String> statusColumn;

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private DatePicker enrollmentDatePicker;
    @FXML private TextArea addressArea;
    @FXML private CheckBox activeCheckBox;
    @FXML private TextField searchField;

    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private RadioButton otherRadio;
    @FXML private ToggleGroup genderGroup;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    @FXML private Label statusLabel;

    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LoginViewController.UserSession userSession;

    // ==================== INITIALIZATION ====================
    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        enrollDateColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        studentTable.setItems(studentList);

        studentTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    populateFormWithStudent(newValue);
                }
            }
        );

        enrollmentDatePicker.setValue(LocalDate.now());
        loadStudentData();
        updateStatusLabel("Ready - " + studentList.size() + " students loaded");
    }

    // ==================== USER SESSION & PERMISSIONS ====================
    public void setUserSession(LoginViewController.UserSession session) {
        this.userSession = session;
        applyRoleBasedPermissions();
        updateStatusLabel("Logged in as: " + session.getUsername() + " (" + session.getRole() + ")");
    }

    private void applyRoleBasedPermissions() {
        if (userSession == null) return;

        if (userSession.isStudent()) {
            addButton.setDisable(true);
            updateButton.setDisable(true);
            deleteButton.setDisable(true);

            fullNameField.setEditable(false);
            emailField.setEditable(false);
            phoneField.setEditable(false);
            dateOfBirthPicker.setDisable(true);
            enrollmentDatePicker.setDisable(true);
            addressArea.setEditable(false);
            maleRadio.setDisable(true);
            femaleRadio.setDisable(true);
            otherRadio.setDisable(true);
            activeCheckBox.setDisable(true);

            updateStatusLabel("Student view: Read-only access");

        } else if (userSession.isLecturer()) {
            addButton.setDisable(true);
            deleteButton.setDisable(true);
            updateStatusLabel("Lecturer view: Can update student information");

        } else if (userSession.isAdmin()) {
            updateStatusLabel("Administrator view: Full access");
        }
    }

    // ==================== DATA HANDLING ====================
    private void loadStudentData() {
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
        } catch (SQLException e) {
            showError("Database Error", "Failed to load student data: " + e.getMessage());
        }
    }

    private void populateFormWithStudent(Student student) {
        fullNameField.setText(student.getFullName());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhone());
        addressArea.setText(student.getAddress());

        try {
            dateOfBirthPicker.setValue(LocalDate.parse(student.getDateOfBirth(), dateFormatter));
        } catch (Exception e) {
            dateOfBirthPicker.setValue(null);
        }

        try {
            enrollmentDatePicker.setValue(LocalDate.parse(student.getEnrollmentDate(), dateFormatter));
        } catch (Exception e) {
            enrollmentDatePicker.setValue(LocalDate.now());
        }

        String gender = student.getGender();
        if ("Male".equalsIgnoreCase(gender)) maleRadio.setSelected(true);
        else if ("Female".equalsIgnoreCase(gender)) femaleRadio.setSelected(true);
        else otherRadio.setSelected(true);

        activeCheckBox.setSelected("Active".equalsIgnoreCase(student.getStatus()));
    }

    // ==================== BUTTON HANDLERS ====================

    @FXML
    private void handleAddStudent() {
        if (!validateInput()) return;

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String dateOfBirth = dateOfBirthPicker.getValue() != null ? dateOfBirthPicker.getValue().format(dateFormatter) : "";
        String gender = getSelectedGender();
        String address = addressArea.getText().trim();
        String enrollmentDate = enrollmentDatePicker.getValue() != null ?
                enrollmentDatePicker.getValue().format(dateFormatter) : LocalDate.now().format(dateFormatter);

        int newId = DatabaseManager.createStudent(fullName, email, phone, dateOfBirth, gender, address, enrollmentDate);

        if (newId > 0) {
            String status = activeCheckBox.isSelected() ? "Active" : "Inactive";
            Student newStudent = new Student(newId, fullName, email, phone, dateOfBirth, gender, address, enrollmentDate, status);
            studentList.add(newStudent);

            showSuccess("Success", "Student added successfully!");
            handleClearForm();
            updateStatusLabel("Student added - Total: " + studentList.size());
        } else {
            showError("Error", "Failed to add student. Email may already exist.");
        }
    }

    @FXML
    private void handleUpdateStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showWarning("No Selection", "Please select a student to update.");
            return;
        }
        if (!validateInput()) return;

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String dateOfBirth = dateOfBirthPicker.getValue() != null ? dateOfBirthPicker.getValue().format(dateFormatter) : "";
        String gender = getSelectedGender();
        String address = addressArea.getText().trim();
        String status = activeCheckBox.isSelected() ? "Active" : "Inactive";

        boolean success = DatabaseManager.updateStudent(selectedStudent.getStudentId(), fullName, email, phone, dateOfBirth, gender, address, status);

        if (success) {
            selectedStudent.setFullName(fullName);
            selectedStudent.setEmail(email);
            selectedStudent.setPhone(phone);
            selectedStudent.setDateOfBirth(dateOfBirth);
            selectedStudent.setGender(gender);
            selectedStudent.setAddress(address);
            selectedStudent.setStatus(status);
            studentTable.refresh();

            showSuccess("Success", "Student updated successfully!");
            updateStatusLabel("Student updated");
        } else {
            showError("Error", "Failed to update student.");
        }
    }

    @FXML
    private void handleDeleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showWarning("No Selection", "Please select a student to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Student");
        confirmAlert.setContentText("Are you sure you want to delete " + selectedStudent.getFullName() + "?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = DatabaseManager.deleteStudent(selectedStudent.getStudentId());
                if (success) {
                    studentList.remove(selectedStudent);
                    showSuccess("Success", "Student deleted successfully!");
                    handleClearForm();
                    updateStatusLabel("Student deleted - Total: " + studentList.size());
                } else {
                    showError("Error", "Failed to delete student.");
                }
            }
        });
    }

    @FXML
    private void handleClearForm() {
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressArea.clear();
        dateOfBirthPicker.setValue(null);
        enrollmentDatePicker.setValue(LocalDate.now());
        genderGroup.selectToggle(null);
        activeCheckBox.setSelected(true);
        studentTable.getSelectionModel().clearSelection();
        updateStatusLabel("Form cleared");
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadStudentData();
            return;
        }

        studentList.clear();
        try (ResultSet rs = DatabaseManager.searchStudentsByName(searchTerm)) {
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
            updateStatusLabel("Found " + studentList.size() + " student(s)");
        } catch (SQLException e) {
            showError("Search Error", "Failed to search: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadStudentData();
        handleClearForm();
        updateStatusLabel("Data refreshed - " + studentList.size() + " students loaded");
    }

    @FXML
    private void handleGenerateReport() {
        String filename = "Student_Report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
        boolean success = ReportGenerator.generateStudentReport(filename);

        if (success) {
            showSuccess("Report Generated", "Report generated successfully!\nFile: " + filename);
            updateStatusLabel("Report generated: " + filename);
        } else {
            showError("Report Error", "Failed to generate report.");
        }
    }

    @FXML
    private void handleExportCSV() {
        String filename = "Student_Export_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
        boolean success = ReportGenerator.exportToCSV(filename);

        if (success) {
            showSuccess("Export Successful", "Exported to CSV!\nFile: " + filename);
            updateStatusLabel("Data exported: " + filename);
        } else {
            showError("Export Error", "Failed to export data.");
        }
    }

    @FXML
    private void handleShowStatistics() {
        Alert statsDialog = new Alert(Alert.AlertType.INFORMATION);
        statsDialog.setTitle("Student Statistics");
        statsDialog.setHeaderText("Statistical Summary");

        String genderStats = ReportGenerator.generateGenderStatistics();
        String statusStats = ReportGenerator.generateStatusStatistics();
        String fullStats = genderStats + "\n\n" + statusStats;

        TextArea textArea = new TextArea(fullStats);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        statsDialog.getDialogPane().setContent(textArea);
        statsDialog.getDialogPane().setPrefWidth(600);
        statsDialog.getDialogPane().setPrefHeight(400);
        statsDialog.showAndWait();
        updateStatusLabel("Statistics displayed");
    }

    @FXML
    private void handleManageEnrollments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentms/student/management/system/EnrollmentView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 700);
            Stage stage = (Stage) studentTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Student Enrollment & Results Management");
        } catch (IOException e) {
            showError("Navigation Error", "Failed to load enrollment view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageCourses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentms/student/management/system/CourseView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 700);
            Stage stage = (Stage) studentTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Course Management");
        } catch (IOException e) {
            showError("Navigation Error", "Failed to load course view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== VALIDATION & UTILITIES ====================

    private boolean validateInput() {
        if (fullNameField.getText().trim().isEmpty()) {
            showWarning("Validation Error", "Please enter student's full name.");
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showWarning("Validation Error", "Please enter student's email.");
            return false;
        }

        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showWarning("Validation Error", "Please enter a valid email address.");
            return false;
        }

        if (genderGroup.getSelectedToggle() == null) {
            showWarning("Validation Error", "Please select a gender.");
            return false;
        }

        return true;
    }

    private String getSelectedGender() {
        if (maleRadio.isSelected()) return "Male";
        if (femaleRadio.isSelected()) return "Female";
        if (otherRadio.isSelected()) return "Other";
        return "";
    }

    private void updateStatusLabel(String message) {
        statusLabel.setText(message);
    }

    // ==================== ALERT HELPERS ====================
    private void showSuccess(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    private void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    private void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
