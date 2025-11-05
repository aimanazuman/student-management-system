/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.studentms.student.management.system;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ReportGenerator handles creation of various reports
 * Supports exporting student data and statistics
 */
public class ReportGenerator {
    
    private static final DateTimeFormatter dateTimeFormatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Generates a comprehensive student report in text format
     */
    public static boolean generateStudentReport(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            
            // Report Header
            writer.println("=".repeat(80));
            writer.println("STUDENT MANAGEMENT SYSTEM - COMPREHENSIVE REPORT");
            writer.println("=".repeat(80));
            writer.println("Generated: " + LocalDateTime.now().format(dateTimeFormatter));
            writer.println("=".repeat(80));
            writer.println();
            
            // Get all students
            try (ResultSet rs = DatabaseManager.getAllStudents()) {
                int studentCount = 0;
                int activeCount = 0;
                int inactiveCount = 0;
                
                writer.println("STUDENT LISTING");
                writer.println("-".repeat(80));
                writer.println();
                
                while (rs.next()) {
                    studentCount++;
                    String status = rs.getString("status");
                    
                    if ("Active".equalsIgnoreCase(status)) {
                        activeCount++;
                    } else {
                        inactiveCount++;
                    }
                    
                    // Print student details
                    writer.println("Student ID: " + rs.getInt("student_id"));
                    writer.println("Name: " + rs.getString("full_name"));
                    writer.println("Email: " + rs.getString("email"));
                    writer.println("Phone: " + rs.getString("phone"));
                    writer.println("Date of Birth: " + rs.getString("date_of_birth"));
                    writer.println("Gender: " + rs.getString("gender"));
                    writer.println("Address: " + rs.getString("address"));
                    writer.println("Enrollment Date: " + rs.getString("enrollment_date"));
                    writer.println("Status: " + status);
                    writer.println("-".repeat(80));
                }
                
                // Summary Statistics
                writer.println();
                writer.println("SUMMARY STATISTICS");
                writer.println("=".repeat(80));
                writer.println("Total Students: " + studentCount);
                writer.println("Active Students: " + activeCount);
                writer.println("Inactive Students: " + inactiveCount);
                writer.println("=".repeat(80));
                
            } catch (SQLException e) {
                System.err.println("Error generating report: " + e.getMessage());
                return false;
            }
            
            writer.println();
            writer.println("End of Report");
            writer.println("=".repeat(80));
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error writing report file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generates a CSV export of all students
     */
    public static boolean exportToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            
            // CSV Header
            writer.println("Student ID,Full Name,Email,Phone,Date of Birth,Gender,Address,Enrollment Date,Status");
            
            // Get all students
            try (ResultSet rs = DatabaseManager.getAllStudents()) {
                while (rs.next()) {
                    writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        rs.getInt("student_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone") != null ? rs.getString("phone") : "",
                        rs.getString("date_of_birth") != null ? rs.getString("date_of_birth") : "",
                        rs.getString("gender") != null ? rs.getString("gender") : "",
                        rs.getString("address") != null ? rs.getString("address").replace("\"", "\"\"") : "",
                        rs.getString("enrollment_date"),
                        rs.getString("status")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error exporting to CSV: " + e.getMessage());
                return false;
            }
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generates a gender distribution report
     */
    public static String generateGenderStatistics() {
        StringBuilder report = new StringBuilder();
        report.append("GENDER DISTRIBUTION REPORT\n");
        report.append("=".repeat(50)).append("\n\n");
        
        try (ResultSet rs = DatabaseManager.getAllStudents()) {
            int maleCount = 0;
            int femaleCount = 0;
            int otherCount = 0;
            int totalCount = 0;
            
            while (rs.next()) {
                totalCount++;
                String gender = rs.getString("gender");
                
                if ("Male".equalsIgnoreCase(gender)) {
                    maleCount++;
                } else if ("Female".equalsIgnoreCase(gender)) {
                    femaleCount++;
                } else {
                    otherCount++;
                }
            }
            
            if (totalCount > 0) {
                report.append(String.format("Total Students: %d%n", totalCount));
                report.append(String.format("Male: %d (%.1f%%)%n", maleCount, (maleCount * 100.0 / totalCount)));
                report.append(String.format("Female: %d (%.1f%%)%n", femaleCount, (femaleCount * 100.0 / totalCount)));
                report.append(String.format("Other: %d (%.1f%%)%n", otherCount, (otherCount * 100.0 / totalCount)));
            } else {
                report.append("No student data available.\n");
            }
            
        } catch (SQLException e) {
            report.append("Error generating statistics: ").append(e.getMessage());
        }
        
        report.append("\n").append("=".repeat(50));
        return report.toString();
    }
    
    /**
     * Generates a status distribution report
     */
    public static String generateStatusStatistics() {
        StringBuilder report = new StringBuilder();
        report.append("STUDENT STATUS REPORT\n");
        report.append("=".repeat(50)).append("\n\n");
        
        try (ResultSet rs = DatabaseManager.getAllStudents()) {
            int activeCount = 0;
            int inactiveCount = 0;
            int totalCount = 0;
            
            while (rs.next()) {
                totalCount++;
                String status = rs.getString("status");
                
                if ("Active".equalsIgnoreCase(status)) {
                    activeCount++;
                } else {
                    inactiveCount++;
                }
            }
            
            if (totalCount > 0) {
                report.append(String.format("Total Students: %d%n", totalCount));
                report.append(String.format("Active: %d (%.1f%%)%n", activeCount, (activeCount * 100.0 / totalCount)));
                report.append(String.format("Inactive: %d (%.1f%%)%n", inactiveCount, (inactiveCount * 100.0 / totalCount)));
            } else {
                report.append("No student data available.\n");
            }
            
        } catch (SQLException e) {
            report.append("Error generating statistics: ").append(e.getMessage());
        }
        
        report.append("\n").append("=".repeat(50));
        return report.toString();
    }
    
    /**
     * Generates a comprehensive grade report for a student (all semesters)
     */
    public static boolean generateStudentGradeReport(String filename, Student student,
            java.util.List<StudentPortalViewController.GradeRecord> grades,
            double cgpa) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            // Report Header
            writer.println("=".repeat(80));
            writer.println("STUDENT GRADE REPORT - ALL SEMESTERS");
            writer.println("=".repeat(80));
            writer.println("Generated: " + LocalDateTime.now().format(dateTimeFormatter));
            writer.println("=".repeat(80));
            writer.println();

            // Student Information
            writer.println("STUDENT INFORMATION");
            writer.println("-".repeat(80));
            writer.println("Student Code: " + student.getStudentCode());
            writer.println("Full Name: " + student.getFullName());
            writer.println("Email: " + student.getEmail());
            writer.println("Status: " + student.getStatus());
            writer.println("-".repeat(80));
            writer.println();

            // Group grades by semester
            java.util.Map<String, java.util.List<StudentPortalViewController.GradeRecord>> semesterMap = new java.util.HashMap<>();
            for (StudentPortalViewController.GradeRecord grade : grades) {
                String key = grade.getSemester() + " " + grade.getYear();
                semesterMap.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(grade);
            }

            // Sort semesters
            java.util.List<String> sortedSemesters = new java.util.ArrayList<>(semesterMap.keySet());
            sortedSemesters.sort((a, b) -> {
                String[] aParts = a.split(" ");
                String[] bParts = b.split(" ");
                int yearCompare = Integer.compare(Integer.parseInt(aParts[1]), Integer.parseInt(bParts[1]));
                if (yearCompare != 0) {
                    return yearCompare;
                }
                return a.compareTo(b);
            });

            int totalCredits = 0;

            // Print grades by semester
            for (String semester : sortedSemesters) {
                java.util.List<StudentPortalViewController.GradeRecord> semesterGrades = semesterMap.get(semester);

                writer.println(semester.toUpperCase());
                writer.println("-".repeat(80));
                writer.printf("%-15s %-35s %-10s %-10s %-10s %-10s%n",
                        "Subject Code", "Subject Name", "Section", "Credits", "Grade", "Grade Pt");
                writer.println("-".repeat(80));

                double semesterPoints = 0.0;
                int semesterCredits = 0;
                int semesterGradedCredits = 0;

                for (StudentPortalViewController.GradeRecord grade : semesterGrades) {
                    writer.printf("%-15s %-35s %-10s %-10d %-10s %-10.2f%n",
                            grade.getSubjectCode(),
                            truncate(grade.getSubjectName(), 35),
                            grade.getSection(),
                            grade.getCredits(),
                            grade.getGrade(),
                            grade.getGradePoint());

                    semesterCredits += grade.getCredits();
                    if (grade.getGradePoint() != null && grade.getGradePoint() > 0 && !grade.getGrade().equals("N/A")) {
                        semesterPoints += grade.getGradePoint() * grade.getCredits();
                        semesterGradedCredits += grade.getCredits();
                    }
                }

                double semesterGPA = semesterGradedCredits > 0 ? semesterPoints / semesterGradedCredits : 0.0;
                totalCredits += semesterCredits;

                writer.println("-".repeat(80));
                writer.printf("Semester Credits: %d | Semester GPA: %.2f%n", semesterCredits, semesterGPA);
                writer.println("-".repeat(80));
                writer.println();
            }

            // Overall Summary
            writer.println("OVERALL ACADEMIC SUMMARY");
            writer.println("=".repeat(80));
            writer.printf("Total Credits Completed: %d%n", totalCredits);
            writer.printf("Cumulative GPA (CGPA): %.2f%n", cgpa);
            writer.printf("Total Subjects: %d%n", grades.size());
            writer.println("=".repeat(80));
            writer.println();
            writer.println("End of Report");
            writer.println("=".repeat(80));

            return true;

        } catch (IOException e) {
            System.err.println("Error writing grade report: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generates a grade report for a specific semester
     */
    public static boolean generateSemesterGradeReport(String filename, Student student,
            java.util.List<StudentPortalViewController.GradeRecord> grades,
            String semester, double semesterGPA) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            // Report Header
            writer.println("=".repeat(80));
            writer.println("STUDENT GRADE REPORT - " + semester.toUpperCase());
            writer.println("=".repeat(80));
            writer.println("Generated: " + LocalDateTime.now().format(dateTimeFormatter));
            writer.println("=".repeat(80));
            writer.println();

            // Student Information
            writer.println("STUDENT INFORMATION");
            writer.println("-".repeat(80));
            writer.println("Student Code: " + student.getStudentCode());
            writer.println("Full Name: " + student.getFullName());
            writer.println("Email: " + student.getEmail());
            writer.println("Status: " + student.getStatus());
            writer.println("-".repeat(80));
            writer.println();

            // Semester Grades
            writer.println(semester.toUpperCase() + " - GRADES");
            writer.println("-".repeat(80));
            writer.printf("%-15s %-35s %-10s %-10s %-10s %-10s%n",
                    "Subject Code", "Subject Name", "Section", "Credits", "Grade", "Grade Pt");
            writer.println("-".repeat(80));

            int totalCredits = 0;

            for (StudentPortalViewController.GradeRecord grade : grades) {
                writer.printf("%-15s %-35s %-10s %-10d %-10s %-10.2f%n",
                        grade.getSubjectCode(),
                        truncate(grade.getSubjectName(), 35),
                        grade.getSection(),
                        grade.getCredits(),
                        grade.getGrade(),
                        grade.getGradePoint());

                totalCredits += grade.getCredits();
            }

            writer.println("-".repeat(80));
            writer.printf("Total Credits: %d | Semester GPA: %.2f%n", totalCredits, semesterGPA);
            writer.println("-".repeat(80));
            writer.println();
            writer.println("End of Report");
            writer.println("=".repeat(80));

            return true;

        } catch (IOException e) {
            System.err.println("Error writing semester grade report: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to truncate long strings
     */
    private static String truncate(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }
}

