module com.studentms.student.management.system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    opens com.studentms.student.management.system to javafx.fxml;
    exports com.studentms.student.management.system;
}
