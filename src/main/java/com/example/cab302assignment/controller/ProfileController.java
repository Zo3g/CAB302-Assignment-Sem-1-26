package com.example.cab302assignment.controller;

import com.example.cab302assignment.app.ViewManager;
import com.example.cab302assignment.dao.*;
import com.example.cab302assignment.model.OrganisationMembership;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.model.UserRiskScore;
import com.example.cab302assignment.model.enums.MemberRole;
import com.example.cab302assignment.service.OrganisationManager;
import com.example.cab302assignment.service.PasswordUtil;
import com.example.cab302assignment.service.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.util.List;
import java.util.Optional;

public class ProfileController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;
    @FXML private Button leaveOrgButton;
    @FXML private Label userScore;

    private final OrganisationDAO organisationDAO = new SqliteOrganisationDAO();
    private final OrganisationMembershipDAO membershipDAO = new SqliteOrganisationMembershipDAO();
    private final UserDAO userDAO = new SqliteUserDAO();
    private final RiskAnalysisDAO riskAnalysisDAO = new SqliteRiskAnalysisDAO();
    private final UserRiskScoreDAO userRiskScoreDAO = new SqliteUserRiskScoreDAO();
    private final OrganisationManager organisationManager;

    public ProfileController() {
        this.organisationManager = new OrganisationManager(this.organisationDAO, this.membershipDAO, this.userDAO);
    }

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            if (nameField != null) nameField.setText(user.getName());
            if (emailField != null) emailField.setText(user.getEmail());

            // Fetch and display user risk score
            displayUserRiskScore(user.getUserId());

            int currentOrgId = SessionManager.getCurrentOrgId();
            if (currentOrgId <= 0) {
                leaveOrgButton.setVisible(false);
                return;
            }
            OrganisationMembership m = membershipDAO.getMembership(user.getUserId(), currentOrgId);

            leaveOrgButton.setVisible(m != null && m.isActive());
        }
    }
    private void displayUserRiskScore(int userId) {
        UserRiskScore riskScore = userRiskScoreDAO.getLatestForUser(userId);

        if (riskScore != null && riskScore.getScore() > 0) {
            if (userScore != null) {
                userScore.setText(String.format("%.2f", riskScore.getScore()));
            }
        } else {
            if (userScore != null) userScore.setText("No data");
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

        String name = nameField != null ? nameField.getText() : null;
        String email = emailField != null ? emailField.getText() : null;
        String newPassword = newPasswordField != null ? newPasswordField.getText() : "";
        String confirmPassword = confirmPasswordField != null ? confirmPasswordField.getText() : "";

        if (name == null || name.isBlank()) {
            setStatus("Name is required.");
            return;
        }
        if (email == null || email.isBlank()) {
            setStatus("Email is required.");
            return;
        }
        if (!email.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            setStatus("Please enter a valid email address.");
            return;
        }

        user.setName(name.trim());
        user.setEmail(email.trim());

        if (newPassword != null && !newPassword.isEmpty()) {
            if (newPassword.length() < 8
                || !newPassword.matches(".*[A-Z].*")
                || !newPassword.matches(".*[a-z].*")
                || !newPassword.matches(".*\\d.*")) {
                setStatus("Password must be 8+ chars with upper, lower, and a digit.");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                setStatus("Passwords do not match.");
                return;
            }
            user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
        }

        try {
            userDAO.updateUser(user);
            SessionManager.setCurrentUser(user);
            if (newPasswordField != null) newPasswordField.clear();
            if (confirmPasswordField != null) confirmPasswordField.clear();
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
            userDAO.deleteUser(user.getUserId());
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

    @FXML
    protected void onLeaveOrganisation() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        int currentOrgId = SessionManager.getCurrentOrgId();
        if (currentOrgId <= 0) {
            setStatus("You are not a member in any organisation.");
            return;
        }

        OrganisationMembership m = membershipDAO.getMembership(user.getUserId(), currentOrgId);
        if (m == null || !m.isActive()) {
            setStatus("You are not an active member of this organisation.");
            return;
        }

        if (m.getMemberRole() == MemberRole.MANAGER) {
            setStatus("As a Manager, you cannot leave organisation.");
            return;
        }

        // Eligible to leave organisation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Are you sure you want to leave this organisation?",
            ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            organisationManager.leaveOrganisation(m);
            setStatus("You have left the organisation.");
            leaveOrgButton.setVisible(false);
        } catch (Exception ex) {
            setStatus("Failed to leave organisation: " + ex.getMessage());
        }
    }
}
