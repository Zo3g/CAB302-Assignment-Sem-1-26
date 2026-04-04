module com.example.cab302assignment {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.example.cab302assignment to javafx.fxml;
    exports com.example.cab302assignment;
    exports com.example.cab302assignment.controller;
    opens com.example.cab302assignment.controller to javafx.fxml;
}