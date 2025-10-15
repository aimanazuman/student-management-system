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
        // SQL statement to create the students table
        // IF NOT EXISTS prevents errors if table already exists
        String createStudentsTable = """
            CREATE TABLE IF NOT EXISTS students (
                student_id INTEGER PRIMARY KEY AUTOINCREMENT,
                full_name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                phone TEXT,
                date_of_birth TEXT,
                gender TEXT,
                address TEXT,
                enrollment_date TEXT NOT NULL,
                status TEXT DEFAULT 'Active'
            )
        """;
        
        // SQL statement to create the courses table
        String createCoursesTable = """
            CREATE TABLE IF NOT EXISTS courses (
                course_id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_code TEXT UNIQUE NOT NULL,
                course_name TEXT NOT NULL,
                credits INTEGER,
                description TEXT
            )
        """;
        
        // SQL statement to create the enrollments table (links students to courses)
        String createEnrollmentsTable = """
            CREATE TABLE IF NOT EXISTS enrollments (
                enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                course_id INTEGER NOT NULL,
                semester TEXT,
                grade TEXT,
                enrollment_year INTEGER,
                FOREIGN KEY (student_id) REFERENCES students(student_id),
                FOREIGN KEY (course_id) REFERENCES courses(course_id),
                UNIQUE(student_id, course_id, semester, enrollment_year)
            )
        """;
        
        // Try-with-resources ensures connection and statement are closed automatically
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Execute each CREATE TABLE statement
            stmt.execute(createStudentsTable);
            stmt.execute(createCoursesTable);
            stmt.execute(createEnrollmentsTable);
            
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a new student record in the database
     * Returns the generated student ID, or -1 if insertion failed
     */
    public static int createStudent(String fullName, String email, String phone, 
                                   String dateOfBirth, String gender, String address, 
                                   String enrollmentDate) {
        // SQL INSERT statement with placeholders (?) for security
        // Using placeholders prevents SQL injection attacks
        String sql = """
            INSERT INTO students (full_name, email, phone, date_of_birth, 
                                 gender, address, enrollment_date, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'Active')
        """;
        
        try (Connection conn = getConnection();
             // RETURN_GENERATED_KEYS allows us to get the auto-generated student_id
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Set values for each placeholder in order
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, dateOfBirth);
            pstmt.setString(5, gender);
            pstmt.setString(6, address);
            pstmt.setString(7, enrollmentDate);
            
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
     * Enrolls a student in a course
     */
    public static int enrollStudentInCourse(int studentId, int courseId, 
                                           String semester, int year) {
        String sql = """
            INSERT INTO enrollments (student_id, course_id, semester, enrollment_year)
            VALUES (?, ?, ?, ?)
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
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
     * Gets all enrollments for a specific student
     */
    public static ResultSet getStudentEnrollments(int studentId) throws SQLException {
        Connection conn = getConnection();
        String sql = """
            SELECT e.enrollment_id, e.student_id, e.course_id, e.semester, 
                   e.grade, e.enrollment_year, c.course_code, c.course_name, c.credits
            FROM enrollments e
            JOIN courses c ON e.course_id = c.course_id
            WHERE e.student_id = ?
            ORDER BY e.enrollment_year DESC, e.semester
        """;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, studentId);
        return pstmt.executeQuery();
    }
}