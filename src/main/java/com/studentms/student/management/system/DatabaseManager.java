/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.studentms.student.management.system;

import java.sql.*;

/**
 * DatabaseManager handles all database operations for the Student Management System
 * This includes connection management, table creation, and CRUD operations
 */
public class DatabaseManager {
    
    // Database URL pointing to the SQLite file in resources
    // The jdbc:sqlite: prefix tells JDBC we're using SQLite
    private static final String DB_URL = "jdbc:sqlite:studentdb.db";
    
    /**
     * Establishes and returns a connection to the SQLite database
     * Each operation should get a fresh connection and close it when done
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load the SQLite JDBC driver explicitly
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
    }
    
    /**
     * Initializes the database by creating necessary tables if they don't exist
     * This should be called when the application starts
     */
    public static void initializeDatabase() {
        String createCoursesTable = """
            CREATE TABLE IF NOT EXISTS courses (
                course_id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_code TEXT UNIQUE NOT NULL,
                course_name TEXT NOT NULL,
                credits INTEGER,
                description TEXT
            )
        """;

        String createStudentsTable = """
            CREATE TABLE IF NOT EXISTS students (
                student_id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_code TEXT UNIQUE NOT NULL,
                full_name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                phone TEXT,
                date_of_birth TEXT,
                gender TEXT,
                address TEXT,
                enrollment_date TEXT NOT NULL,
                status TEXT DEFAULT 'Active',
                course_id INTEGER,
                FOREIGN KEY (course_id) REFERENCES courses(course_id)
            )
        """;

        String createSubjectsTable = """
            CREATE TABLE IF NOT EXISTS subjects (
                subject_id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject_code TEXT NOT NULL,
                subject_name TEXT NOT NULL,
                subject_section TEXT NOT NULL,
                credits INTEGER,
                description TEXT,
                course_id INTEGER,
                FOREIGN KEY (course_id) REFERENCES courses(course_id),
                UNIQUE(subject_code, subject_section)
            )
        """;

        String createEnrollmentsTable = """
            CREATE TABLE IF NOT EXISTS enrollments (
                enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                subject_id INTEGER NOT NULL,
                semester TEXT,
                grade TEXT,
                enrollment_year INTEGER,
                FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE,
                UNIQUE(student_id, subject_id, semester, enrollment_year)
            )
        """;
        
        // Try-with-resources ensures connection and statement are closed automatically
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Execute each CREATE TABLE statement
            stmt.execute(createCoursesTable);
            stmt.execute(createStudentsTable);
            stmt.execute(createSubjectsTable);
            stmt.execute(createEnrollmentsTable);
            
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generates the next student code (ST001, ST002, etc.)
     */
    public static String generateNextStudentCode() {
        String sql = "SELECT student_code FROM students ORDER BY student_id DESC LIMIT 1";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                String lastCode = rs.getString("student_code");
                // Extract number from ST001 format
                int number = Integer.parseInt(lastCode.substring(2));
                return String.format("ST%03d", number + 1);
            } else {
                return "ST001"; // First student
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating student code: " + e.getMessage());
            return "ST001";
        }
    }
    
    /**
     * Creates a new student record in the database
     * Returns the generated student ID, or -1 if insertion failed
     */
    public static int createStudent(String studentCode, String fullName, String email, String phone, 
                                   String dateOfBirth, String gender, String address, 
                                   String enrollmentDate) {
        // SQL INSERT statement with placeholders (?) for security
        // Using placeholders prevents SQL injection attacks
        String sql = """
            INSERT INTO students (student_code, full_name, email, phone, date_of_birth, 
                                 gender, address, enrollment_date, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Active')
        """;
        
        try (Connection conn = getConnection();
             // RETURN_GENERATED_KEYS allows us to get the auto-generated student_id
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Set values for each placeholder in order
            pstmt.setString(1, studentCode);
            pstmt.setString(2, fullName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, dateOfBirth);
            pstmt.setString(6, gender);
            pstmt.setString(7, address);
            pstmt.setString(8, enrollmentDate);
            
            // Execute the insert
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Retrieve the generated student ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating student: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1; // Indicates failure
    }
    
    /**
     * Retrieves all students from the database
     * Returns a ResultSet that must be closed by the caller
     */
    public static ResultSet getAllStudents() throws SQLException {
        Connection conn = getConnection();
        String sql = "SELECT * FROM students ORDER BY full_name";
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
    
    /**
     * Updates an existing student record
     * Returns true if update was successful
     */
    public static boolean updateStudent(int studentId, String fullName, String email, 
                                       String phone, String dateOfBirth, String gender, 
                                       String address, String status) {
        String sql = """
            UPDATE students 
            SET full_name = ?, email = ?, phone = ?, date_of_birth = ?,
                gender = ?, address = ?, status = ?
            WHERE student_id = ?
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, dateOfBirth);
            pstmt.setString(5, gender);
            pstmt.setString(6, address);
            pstmt.setString(7, status);
            pstmt.setInt(8, studentId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates student with course enrollment
     */
    public static boolean updateStudentWithCourse(int studentId, String fullName, String email, 
                                                 String phone, String dateOfBirth, String gender, 
                                                 String address, String status, int courseId) {
        String sql = """
            UPDATE students 
            SET full_name = ?, email = ?, phone = ?, date_of_birth = ?,
                gender = ?, address = ?, status = ?, course_id = ?
            WHERE student_id = ?
        """;

        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, dateOfBirth);
            pstmt.setString(5, gender);
            pstmt.setString(6, address);
            pstmt.setString(7, status);
            pstmt.setInt(8, courseId);
            pstmt.setInt(9, studentId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes a student record from the database
     * Returns true if deletion was successful
     */
    public static boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Searches for students by name (partial match supported)
     */
    public static ResultSet searchStudentsByName(String name) throws SQLException {
        Connection conn = getConnection();
        String sql = "SELECT * FROM students WHERE full_name LIKE ? ORDER BY full_name";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        // The % symbols allow matching any characters before and after the search term
        pstmt.setString(1, "%" + name + "%");
        return pstmt.executeQuery();
    }
    
    // ==================== COURSE MANAGEMENT METHODS ====================
    
    /**
     * Creates a new course in the database
     */
    public static int createCourse(String courseCode, String courseName, 
                                   int credits, String description) {
        String sql = """
            INSERT INTO courses (course_code, course_name, credits, description)
            VALUES (?, ?, ?, ?)
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, courseCode);
            pstmt.setString(2, courseName);
            pstmt.setInt(3, credits);
            pstmt.setString(4, description);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating course: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Retrieves all courses from the database
     */
    public static ResultSet getAllCourses() throws SQLException {
        Connection conn = getConnection();
        String sql = "SELECT * FROM courses ORDER BY course_code";
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
    
    /**
     * Updates a course in the database
     */
    public static boolean updateCourse(int courseId, String courseCode, String courseName,
                                      int credits, String description) {
        String sql = """
            UPDATE courses 
            SET course_code = ?, course_name = ?, credits = ?, description = ?
            WHERE course_id = ?
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseCode);
            pstmt.setString(2, courseName);
            pstmt.setInt(3, credits);
            pstmt.setString(4, description);
            pstmt.setInt(5, courseId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes a course from the database
     */
    public static boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets a course by its ID
     */
    public static ResultSet getCourseById(int courseId) throws SQLException {
        Connection conn = getConnection();
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, courseId);
        return pstmt.executeQuery();
    }
    
    // ==================== SUBJECT MANAGEMENT METHODS ====================
    
    /**
     * Creates a new subject in the database (with course link)
     */
    public static int createSubject(String subjectCode, String subjectName, String subjectSection, 
                                   int credits, String description, int courseId) {
        String sql = """
            INSERT INTO subjects (subject_code, subject_name, subject_section, credits, description, course_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, subjectCode);
            pstmt.setString(2, subjectName);
            pstmt.setString(3, subjectSection);
            pstmt.setInt(4, credits);
            pstmt.setString(5, description);
            pstmt.setInt(6, courseId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating subject: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }
    
    /**
     * Creates a new subject in the database (backward compatibility - no course link)
     */
    public static int createSubject(String subjectCode, String subjectName, String subjectSection, 
                                   int credits, String description) {
        return createSubject(subjectCode, subjectName, subjectSection, credits, description, 0);
    }
    
    /**
     * Retrieves all subjects from the database
     */
    public static ResultSet getAllSubjects() throws SQLException {
        Connection conn = getConnection();
        String sql = "SELECT * FROM subjects ORDER BY subject_code, subject_section";
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
    
    /**
     * Updates a subject in the database (with course link)
     */
    public static boolean updateSubject(int subjectId, String subjectCode, String subjectName, 
                                       String subjectSection, int credits, String description, int courseId) {
        String sql = """
            UPDATE subjects 
            SET subject_code = ?, subject_name = ?, subject_section = ?, credits = ?, description = ?, course_id = ?
            WHERE subject_id = ?
        """;

        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, subjectCode);
            pstmt.setString(2, subjectName);
            pstmt.setString(3, subjectSection);
            pstmt.setInt(4, credits);
            pstmt.setString(5, description);
            pstmt.setInt(6, courseId);
            pstmt.setInt(7, subjectId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating subject: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates a subject in the database (backward compatibility - no course link)
     */
    public static boolean updateSubject(int subjectId, String subjectCode, String subjectName, 
                                       String subjectSection, int credits, String description) {
        return updateSubject(subjectId, subjectCode, subjectName, subjectSection, credits, description, 0);
    }
    
    /**
     * Deletes a subject from the database
     */
    public static boolean deleteSubject(int subjectId) {
        String sql = "DELETE FROM subjects WHERE subject_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting subject: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== ENROLLMENT MANAGEMENT METHODS ====================
    
    /**
     * Enrolls a student in a subject
     */
    public static int enrollStudentInSubject(int studentId, int subjectId, 
                                           String semester, int year) {
        String sql = """
            INSERT INTO enrollments (student_id, subject_id, semester, enrollment_year)
            VALUES (?, ?, ?, ?)
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            pstmt.setString(3, semester);
            pstmt.setInt(4, year);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error enrolling student: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Gets all enrollments for a specific student
     */
    public static ResultSet getStudentEnrollments(int studentId) throws SQLException {
        Connection conn = getConnection();
        String sql = """
            SELECT e.enrollment_id, e.student_id, e.subject_id, e.semester, 
                   e.grade, e.enrollment_year, s.subject_code, s.subject_name, 
                   s.subject_section, s.credits
            FROM enrollments e
            JOIN subjects s ON e.subject_id = s.subject_id
            WHERE e.student_id = ?
            ORDER BY e.enrollment_year DESC, e.semester
        """;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, studentId);
        return pstmt.executeQuery();
    }
    
    /**
     * Checks if a student is already enrolled in a subject for a specific semester
     */
    public static boolean isStudentEnrolledInSubject(int studentId, int subjectId, String semester, int year) {
        String sql = """
            SELECT COUNT(*) as count FROM enrollments 
            WHERE student_id = ? AND subject_id = ? AND semester = ? AND enrollment_year = ?
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            pstmt.setString(3, semester);
            pstmt.setInt(4, year);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking enrollment: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Updates a grade for a student's enrollment
     */
    public static boolean updateGrade(int enrollmentId, String grade) {
        String sql = "UPDATE enrollments SET grade = ? WHERE enrollment_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, grade);
            pstmt.setInt(2, enrollmentId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating grade: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes an enrollment record
     */
    public static boolean deleteEnrollment(int enrollmentId) {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollmentId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting enrollment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== AUTHENTICATION METHODS ====================
    
    /**
     * Authenticates a student using email and student code
     */
    public static ResultSet authenticateStudent(String email, String studentCode) throws SQLException {
        Connection conn = getConnection();
        String sql = "SELECT * FROM students WHERE email = ? AND student_code = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, email);
        pstmt.setString(2, studentCode);
        return pstmt.executeQuery();
    }
    
    /**
     * Gets student by student code
     */
    public static ResultSet getStudentByCode(String studentCode) throws SQLException {
        Connection conn = getConnection();
        String sql = "SELECT * FROM students WHERE student_code = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, studentCode);
        return pstmt.executeQuery();
    }
}
