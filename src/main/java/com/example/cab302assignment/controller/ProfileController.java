package com.example.cab302assignment.controller;

import com.example.cab302assignment.model.SessionManager;
import com.example.cab302assignment.model.SqliteUserDAO;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.model.UserDAO;
import com.example.cab302assignment.model.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Optional;

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
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            setStatus("You must be signed in to update your profile.");
            return;
        }

        String fullName = fullNameField != null ? fullNameField.getText() : null;
        String email = emailField != null ? emailField.getText() : null;

        if (fullName == null || fullName.isBlank() || email == null || email.isBlank()) {
            setStatus("Full name and email are required.");
            return;
        }

        user.setFullName(fullName.trim());
        user.setEmail(email.trim());

        try {
            userDAO.updateUser(user);
            SessionManager.setCurrentUser(user);
            setStatus("Profile updated successfully.");
        } catch (Exception ex) {
            setStatus("Failed to update profile: " + ex.getMessage());
        }
    }

    @FXML
    protected void onDeleteAccount() {
        //CRUD DELETE
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            setStatus("No account is currently signed in.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to permanently delete your account? This cannot be undone.",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText("Delete account");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            userDAO.deleteUser(user.getId());
            SessionManager.logout();
            ViewManager.switchView("sign-in-view.fxml", "Sign In");
        } catch (Exception ex) {
            setStatus("Failed to delete account: " + ex.getMessage());
        }
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}
