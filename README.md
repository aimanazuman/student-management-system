# Student Management System 📚

A comprehensive JavaFX desktop application for educational institutions to manage student records, academic enrollments, grade tracking, and generate detailed reports with automated GPA calculations.

![JavaFX](https://img.shields.io/badge/JavaFX-17-orange)
![Java](https://img.shields.io/badge/Java-8+-blue)
![SQLite](https://img.shields.io/badge/SQLite-3-green)
![NetBeans](https://img.shields.io/badge/NetBeans-IDE-red)

## 🚀 Features

- **👥 Student Management** - Complete CRUD operations for student records
- **📖 Subject Catalog** - Manage subjects with multiple sections support
- **🎓 Course Management** - Organize academic programs and curricula
- **📝 Enrollment System** - Assign subjects to students with semester tracking
- **📊 Grade Management** - Enter grades with automatic GPA calculation (A+ to F scale)
- **🔐 Role-Based Access Control** - Three user levels (Admin, Coordinator, Student)
- **📄 Report Generation** - Automated transcripts, CSV exports, and statistical reports
- **💯 CGPA Calculation** - Real-time cumulative and semester-wise GPA computation
- **🔍 Search & Filter** - Quick student lookup and semester-based grade filtering
- **🎨 Modern UI Design** - Intuitive interface with color-coded actions and responsive layouts

## 🛠️ Technologies Used

### Backend
- **JavaFX 17** - Desktop application framework
- **Java SE 8+** - Programming language
- **SQLite 3** - Lightweight embedded database
- **JDBC** - Database connectivity
- **FXML** - Declarative UI markup

### Frontend
- **Scene Builder** - Visual UI design tool
- **CSS Styling** - Custom themes and color schemes
- **Property Binding** - Reactive UI updates
- **TableView** - Data grid displays

### Architecture
- **MVC Pattern** - Model-View-Controller separation
- **Singleton DatabaseManager** - Centralized data access
- **JavaFX Properties** - Observable data models
- **Try-with-Resources** - Automatic resource management

## 📋 Prerequisites

- [Java Runtime Environment (JRE) 8](https://www.oracle.com/java/technologies/downloads/) or later
- [NetBeans IDE 12+](https://netbeans.apache.org/download/) (for development)
- Screen resolution: 1024×768 minimum (1920×1080 recommended)
- Operating System: Windows 10+, macOS 10.14+, or Linux (Ubuntu 18.04+)

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/aimanazuman/student-management-system.git
cd student-management-system
```

### 2. Open in NetBeans
1. Launch NetBeans IDE
2. File → Open Project
3. Navigate to the cloned directory
4. Select the project and click "Open Project"

### 3. Build the Project
```bash
# Using Maven
mvn clean install

# Or in NetBeans
Right-click project → Clean and Build
```

### 4. Run the Application
```bash
# Using command line
java target/App.java

# Or in NetBeans
Press F6 or click Run Project
```

### 5. Login with Default Credentials

**Administrator:**
- Username: `admin`
- Password: `admin123`
- Full system access

**Coordinator:**
- Username: `coordinator`
- Password: `coord123`
- Update and enrollment access

**Student:**
- Username: `[student email]`
- Password: `[student code]` (e.g., ST001)
- View personal records only

## 🏗️ Project Structure

```
student-management-system/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/studentms/student/management/system/
│       │       ├── App.java                      # Main application entry
│       │       ├── DatabaseManager.java          # Database operations
│       │       ├── LoginViewController.java      # Authentication logic
│       │       ├── StudentViewController.java    # Student management
│       │       ├── EnrollmentViewController.java # Grade management
│       │       ├── SubjectViewController.java    # Subject catalog
│       │       ├── CourseViewController.java     # Course management
│       │       ├── StudentPortalViewController.java # Student portal
│       │       ├── ReportGenerator.java          # Report generation
│       │       ├── Student.java                  # Student model
│       │       ├── Subject.java                  # Subject model
│       │       ├── Course.java                   # Course model
│       │       └── Enrollment.java               # Enrollment model
│       └── resources/
│           └── com/studentms/student/management/system/
│               ├── LoginView.fxml                # Login screen UI
│               ├── StudentView.fxml              # Main dashboard
│               ├── EnrollmentView.fxml           # Enrollment UI
│               ├── SubjectView.fxml              # Subject management
│               ├── CourseView.fxml               # Course management
│               └── StudentPortalView.fxml        # Student portal
├── studentdb.db                                  # SQLite database
├── pom.xml                                       # Maven dependencies
└── README.md                                     # This file
```

## 🎯 Usage Examples

### Student Management
```
1. Login as administrator
2. Fill in student information form
3. Click "Add Student" (auto-generates student code: ST001, ST002...)
4. Update: Select student from table, modify fields, click "Update Student"
5. Delete: Select student, click "Delete Student", confirm
6. Search: Type name in search box for real-time filtering
```

### Subject Enrollment
```
1. Click "Manage Enrollments" from main dashboard
2. Select student from dropdown
3. Switch to "Assign Subjects" tab
4. Choose subject, semester, and year
5. Click "Assign Subject"
```

### Grade Entry
```
1. In Enrollment view, select student
2. Switch to "Enter/Update Grades" tab
3. Click on subject in table
4. Select grade from dropdown (A+ through F)
5. Click "Update Grade"
6. CGPA automatically recalculates
```

### Report Generation
```
# Student Reports
- Click "Generate Full Report" → Creates text file with all students
- Click "Export to CSV" → Excel-compatible spreadsheet
- Click "Show Statistics" → Gender and status distribution

# Grade Transcripts (Student Portal)
- Login as student
- Click "Print Grade" → Generates academic transcript
- Filter by semester for specific term reports
```

## ⚙️ Configuration

### Database Schema
```sql
-- Auto-created on first launch
CREATE TABLE students (
    student_id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_code TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone TEXT,
    date_of_birth TEXT,
    gender TEXT,
    address TEXT,
    enrollment_date TEXT NOT NULL,
    status TEXT DEFAULT 'Active'
);

CREATE TABLE subjects (
    subject_id INTEGER PRIMARY KEY AUTOINCREMENT,
    subject_code TEXT NOT NULL,
    subject_name TEXT NOT NULL,
    subject_section TEXT NOT NULL,
    credits INTEGER,
    description TEXT,
    UNIQUE(subject_code, subject_section)
);

CREATE TABLE enrollments (
    enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    subject_id INTEGER NOT NULL,
    semester TEXT,
    grade TEXT,
    enrollment_year INTEGER,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE
);
```

### Grading Scale
```
A+, A  : 4.0    B+ : 3.3    C+ : 2.3    D+ : 1.3
A-     : 3.7    B  : 3.0    C  : 2.0    D  : 1.0
                B- : 2.7    C- : 1.7    F  : 0.0
```

### GPA Calculation Formula
```
CGPA = Σ(Grade Point × Credits) / Σ(Graded Credits)

Example:
Subject 1: A (4.0) × 3 credits = 12.0 points
Subject 2: B+ (3.3) × 3 credits = 9.9 points
Subject 3: A- (3.7) × 3 credits = 11.1 points
Total: 33.0 points / 9 credits = 3.67 CGPA
```

## 🛡️ Security Features

- **Password Protection** - Secure authentication system
- **Role-Based Access** - Three-tier permission levels
- **Data Validation** - Email uniqueness, required fields enforcement
- **SQL Injection Prevention** - PreparedStatement parameter binding
- **Cascade Deletion Protection** - Confirmation dialogs for destructive operations
- **Session Management** - UserSession class tracks logged-in users

## 🎨 User Interface Highlights

### Color-Coded Actions
- 🟢 **Green** - Add/Create operations
- 🟠 **Orange** - Update/Edit operations
- 🔴 **Red** - Delete/Remove operations
- ⚪ **Gray** - Clear/Cancel operations
- 🔵 **Blue** - Navigation operations
- 🟣 **Purple** - Special functions (enrollment, reports)

### Layout Features
- **Card-Style Forms** - White backgrounds with subtle shadows
- **Gradient Headers** - Dark blue for admin, purple for students
- **Responsive Tables** - Sortable columns, click-to-edit rows
- **Status Messages** - Real-time feedback in footer status bar
- **Tooltips** - Helpful hints on hover
- **Modal Dialogs** - Confirmation and success messages

## 🧪 Testing

### Sample Data Population
Use the provided SQL script to populate the database with mock data:
```bash
sqlite3 studentdb.db < mock_data.sql
```

This creates:
- 30 students with diverse backgrounds
- 30 subjects across multiple sections
- 5 academic courses
- 400+ enrollment records with grades
- Complete academic histories (1-4 years)

### Test Scenarios
```
✅ Add student with duplicate email → Error message displayed
✅ Update student without selection → Warning shown
✅ Delete student with enrollments → Cascade deletion works
✅ Assign duplicate subject → Prevented by unique constraint
✅ Calculate GPA with mixed grades → Correct computation
✅ Search with partial name → Real-time filtering
✅ Generate report with 100+ students → Fast performance
✅ Student login with wrong password → Access denied
```

## 📚 Learning Outcomes

This project demonstrates:

- **JavaFX Application Development** - Desktop UI with FXML
- **Database Design** - Normalized relational schema
- **JDBC Operations** - CRUD with PreparedStatements
- **MVC Architecture** - Clean separation of concerns
- **Property Binding** - Reactive UI updates
- **Error Handling** - Try-catch-finally patterns
- **User Authentication** - Role-based access control
- **Report Generation** - File I/O and formatting
- **Algorithm Implementation** - GPA calculation logic
- **Software Engineering Practices** - Code organization, comments, documentation

## 🎓 Academic Context

**Course**: Advance Application Development  
**Course Code**: SWC4243/SWC4453  
**Institution**: Universiti Poly Tech Malaysia (UPTM)  
**Semester**: Semester 7  
**Lecturer**: Pn. Wan Nor Asnida binti Wan Jusoh

### Project Requirements Fulfilled
- ✅ JavaFX desktop application
- ✅ Database integration (SQLite)
- ✅ CRUD operations (Create, Read, Update, Delete)
- ✅ Multiple user roles
- ✅ Report generation functionality
- ✅ Proper error handling
- ✅ Professional UI design
- ✅ Comprehensive documentation
- ✅ Code comments and structure

## 👥 Developer

**Name**: Aiman Azman  
**Student ID**: [REDACTED]  
**Email**: [contact.aiman.azman@gmail.com](contact.aiman.azman@gmail.com)
**GitHub**: [github.com/aimanazuman](https://github.com/aimanazuman)

### Responsibilities
- Requirements analysis and system design
- Database schema design and implementation
- JavaFX UI development with Scene Builder
- Controller logic and event handling
- API integration and service layer
- Report generation functionality
- Testing and debugging
- Documentation and user manual

## 🐛 Known Issues & Limitations

### Current Limitations
- **Single-User Access** - No concurrent multi-user support
- **Local Database** - SQLite not suitable for network deployment
- **Manual Backup** - No automated backup system
- **Fixed Grading Scale** - Requires code changes to modify scale
- **No Photo Upload** - Student profiles don't support images
- **English Only** - No multi-language support

### Future Enhancements
- [ ] Multi-user database (PostgreSQL/MySQL)
- [ ] Automated email notifications
- [ ] Attendance tracking module
- [ ] Fee management system
- [ ] Timetable generation
- [ ] Web-based interface
- [ ] Mobile app version
- [ ] Data analytics dashboard
- [ ] Barcode/QR code student IDs
- [ ] Bulk import from Excel

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add student photo upload'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style Guidelines
- Follow Java naming conventions
- Add JavaDoc comments to all public methods
- Use meaningful variable names
- Keep methods under 50 lines
- Write unit tests for new features

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Oracle** - For JavaFX framework and comprehensive documentation
- **SQLite Team** - For the reliable embedded database
- **Scene Builder Community** - For the visual FXML editor
- **NetBeans Team** - For the excellent IDE
- **Course Instructor** - For guidance and project requirements
- **Stack Overflow Community** - For troubleshooting assistance
- **GitHub** - For version control and collaboration platform

## 📞 Support & Troubleshooting

### Common Issues

**Issue**: "Database is locked"  
**Solution**: Close all application instances, check task manager for Java processes

**Issue**: Login fails with correct credentials  
**Solution**: Verify role selection matches user type, check caps lock

**Issue**: CGPA shows "N/A"  
**Solution**: Ensure at least one subject has a letter grade (not "Not Graded")

**Issue**: Report generation fails  
**Solution**: Check write permissions in application folder, close open reports

### Getting Help
1. Check the [User Manual](USER_MANUAL.md) for detailed instructions (Not available yet)
2. Review [Issues](https://github.com/aimanazuman/student-management-system/issues) on GitHub
3. Contact developer: [contact.aiman.azman@gmail.com](contact.aiman.azman@gmail.com)
4. Review console output for detailed error messages

## 📖 Documentation

- [User Manual](USER_MANUAL.md) - Complete usage guide
- [Final Report](FINAL_REPORT.md) - Technical documentation
- [API Documentation](docs/API.md) - DatabaseManager methods
- [Troubleshooting Guide](docs/TROUBLESHOOTING.md) - Common issues

---

**⭐ If you found this project helpful for your studies, please give it a star!**

---

*This project was developed as part of the Advance Application Development course (SWC4243/SWC4453) to demonstrate JavaFX desktop application development with database integration.*

**Last Updated**: November 2025
**Version**: 1.2.0  
**Build Status**: Stable
