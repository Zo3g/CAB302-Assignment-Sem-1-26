package com.example.cab302assignment.controller;

import com.example.cab302assignment.SessionManager;
import com.example.cab302assignment.SqliteUserDAO;
import com.example.cab302assignment.User;
import com.example.cab302assignment.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProfileController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private Label statusLabel;

    private final UserDAO userDAO = new SqliteUserDAO();

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            if (fullNameField != null) fullNameField.setText(user.getFullName());
            if (emailField != null) emailField.setText(user.getEmail());
        }
    }

    @FXML
    protected void onUpdateProfile() {
        //CRUD UPDATE
        // TODO: validate inputs, update SessionManager user, call userDAO.updateUser(...)
        if (statusLabel != null) {
            statusLabel.setText("Update not implemented yet.");
        }
    }

    @FXML
    protected void onDeleteAccount() {
        //CRUD DELETE
        // TODO: confirm with user, call userDAO.deleteUser(currentUser.getId()),
        //       SessionManager.logout(), and return to sign-in view
        if (statusLabel != null) {
            statusLabel.setText("Delete not implemented yet.");
        }
    }
}
