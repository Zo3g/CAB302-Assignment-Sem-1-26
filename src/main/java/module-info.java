module com.example.cab302assignment {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.sql;
    requires eu.hansolo.medusa;
    requires com.google.genai;
    requires io.github.cdimascio.dotenv.java;

    exports com.example.cab302assignment.app;
    exports com.example.cab302assignment.controller;
    exports com.example.cab302assignment.model;
    exports com.example.cab302assignment.model.enums;
    exports com.example.cab302assignment.dao;
    exports com.example.cab302assignment.service;
    exports com.example.cab302assignment.db;

    opens com.example.cab302assignment.app to javafx.fxml;
    opens com.example.cab302assignment.controller to javafx.fxml;
    opens com.example.cab302assignment.model to javafx.fxml;
}
