package com.example.cab302assignment.controller;

import com.example.cab302assignment.app.ViewManager;
import com.example.cab302assignment.dao.*;
import com.example.cab302assignment.model.OrganisationMembership;
import com.example.cab302assignment.model.RiskAnalysis;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.model.UserRiskScore;
import com.example.cab302assignment.model.enums.MemberRole;
import com.example.cab302assignment.model.enums.RiskLevel;
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

/**
 * Controller for the user profile view.
 *
 * <p>Handles displaying and updating the currently signed-in user's profile
 * details (name, email, password), surfacing their latest risk score, and
 * providing the ability to leave the current organisation or delete the
 * account entirely.</p>
 *
 * <p>The controller relies on {@link SessionManager} to determine the active
 * user and organisation, and delegates persistence to a set of DAOs and the
 * {@link OrganisationManager} service.</p>
 */
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

    /**
     * Constructs a new {@code ProfileController} and wires up the
     * {@link OrganisationManager} service with the DAOs required to manage
     * organisation memberships from the profile view.
     */
    public ProfileController() {
        this.organisationManager = new OrganisationManager(this.organisationDAO, this.membershipDAO, this.userDAO);
    }

    /**
     * Initialises the profile view after FXML injection has completed.
     *
     * <p>Populates the name and email fields from the currently signed-in
     * user, refreshes and displays the user's risk score, and decides whether
     * the {@code leaveOrgButton} should be visible based on whether the user
     * has an active membership in the current organisation.</p>
     *
     * <p>If no user is signed in, no fields are populated.</p>
     */
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
    /**
     * Recalculates and displays the risk score for the given user.
     *
     * <p>Fetches the user's historical {@link RiskAnalysis} entries, loads
     * (or creates) their {@link UserRiskScore}, recalculates the aggregate
     * score, and persists the result. The corresponding {@link RiskLevel}
     * category is then rendered in the {@code userScore} label. If the user
     * has no analysis history, the label is set to {@code "No history"}.</p>
     *
     * @param userId the identifier of the user whose risk score should be
     *               recalculated and displayed
     */
    private void displayUserRiskScore(int userId) {
        List<RiskAnalysis> analyses = riskAnalysisDAO.getByUserId(userId);

        UserRiskScore riskScore = userRiskScoreDAO.getLatestForUser(userId);
        if (riskScore == null) {
            riskScore = new UserRiskScore();
            riskScore.setUserId(userId);
        }

        riskScore.recalculate(analyses);
        saveUserRiskScore(riskScore);

        if (analyses == null || analyses.isEmpty()) {
            if (userScore != null) userScore.setText("No history");
            return;
        }

        if (userScore != null) {
            RiskLevel category = RiskLevel.fromCalculatedScore(riskScore.getScore());
            userScore.setText(category.name());
        }
    }

    /**
     * Persists the supplied {@link UserRiskScore}.
     *
     * <p>If the score already has a database identifier ({@code scoreId > 0})
     * it is updated in place; otherwise a new record is inserted. Any
     * persistence errors are caught and logged to {@code System.err} so they
     * do not interrupt the profile view from loading.</p>
     *
     * @param score the user risk score to insert or update
     */
    private void saveUserRiskScore(UserRiskScore score) {
        try {
            if (score.getScoreId() > 0) {
                userRiskScoreDAO.updateScore(score);
            } else {
                userRiskScoreDAO.addScore(score);
            }
        } catch (Exception ex) {
            System.err.println("Error saving user risk score: " + ex.getMessage());
        }
    }

    /**
     * Handles the "Update Profile" action (CRUD update).
     *
     * <p>Validates the name, email, and (optionally) new password supplied in
     * the form, then writes the changes to the underlying {@link User} record
     * via {@link UserDAO#updateUser(User)} and refreshes the current session.
     * Validation failures and persistence errors are surfaced through the
     * status label rather than thrown.</p>
     *
     * <p>The password is only changed when a non-empty value is entered in
     * {@code newPasswordField}; in that case it must be at least 8 characters
     * long, contain upper- and lower-case letters and a digit, and match the
     * value in {@code confirmPasswordField}.</p>
     */
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

    /**
     * Handles the "Delete Account" action (CRUD delete).
     *
     * <p>Prompts the user with a confirmation dialog before permanently
     * removing the currently signed-in {@link User} from the database. On
     * success, the session is cleared via {@link SessionManager#logout()} and
     * the application is returned to the sign-in view. If no user is signed
     * in, or the user cancels the confirmation, the action is a no-op.</p>
     */
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

    /**
     * Updates the status label with the given message, if the label is
     * available.
     *
     * <p>Used to provide validation feedback and confirmation messages to the
     * user without throwing exceptions when {@code statusLabel} has not been
     * injected (e.g. in unit tests).</p>
     *
     * @param message the message to display in the status label
     */
    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    /**
     * Handles the "Leave Organisation" action.
     *
     * <p>Verifies that the signed-in user has an active membership in the
     * current organisation and is not acting as a {@link MemberRole#MANAGER}
     * (managers must transfer ownership before leaving). After confirmation,
     * delegates to {@link OrganisationManager#leaveOrganisation(OrganisationMembership)}
     * to perform the deactivation and hides the leave button on success.</p>
     *
     * <p>All failure modes (no signed-in user, no current organisation, no
     * active membership, manager role, persistence error) are reported via
     * the status label.</p>
     */
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
