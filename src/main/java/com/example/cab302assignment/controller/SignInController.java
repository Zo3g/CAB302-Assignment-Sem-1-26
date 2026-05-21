package com.example.cab302assignment.controller;

import com.example.cab302assignment.app.ViewManager;
import com.example.cab302assignment.dao.OrganisationMembershipDAO;
import com.example.cab302assignment.dao.SqliteOrganisationMembershipDAO;
import com.example.cab302assignment.dao.SqliteUserDAO;
import com.example.cab302assignment.dao.UserDAO;
import com.example.cab302assignment.dao.OrganisationDAO;
import com.example.cab302assignment.dao.SqliteOrganisationDAO;
import com.example.cab302assignment.model.OrganisationMembership;
import com.example.cab302assignment.model.Organisation;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.service.PasswordUtil;
import com.example.cab302assignment.service.SessionManager;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the Sign-In view.
 *
 * Handles user authentication, session initialisation,
 * and navigation into the main workspace.
 *
 * Responsibilities include:
 * - Validating user credentials
 * - Verifying password hashes
 * - Resolving the user's active organisation
 * - Initialising session state
 * - Redirecting to sign-up or workspace views
 */
public class SignInController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Label noticeLabel;
    @FXML private Button signInButton;

    private final UserDAO userDAO;
    private final OrganisationDAO organisationDAO;
    private final OrganisationMembershipDAO membershipDAO;

    /**
     * Default constructor using SQLite DAO implementations.
     */
    public SignInController() {
        this(new SqliteUserDAO(), new SqliteOrganisationDAO(), new SqliteOrganisationMembershipDAO());
    }

    /**
     * Constructor allowing custom UserDAO injection.
     *
     * Uses default SQLite implementations for organisation
     * and membership data.
     */
    public SignInController(UserDAO userDAO) {
        this(userDAO, new SqliteOrganisationDAO(), new SqliteOrganisationMembershipDAO());
    }

    /**
     * Full dependency injection constructor.
     *
     * Allows all DAOs to be provided externally
     */
    public SignInController(UserDAO userDAO, OrganisationDAO organisationDAO, OrganisationMembershipDAO membershipDAO) {
        this.userDAO = userDAO;
        this.organisationDAO = organisationDAO;
        this.membershipDAO = membershipDAO;
    }

    /**
     * Initialises the sign-in form UI.
     *
     * Sets up:
     * - Button disable binding for empty fields
     * - ENTER key handling for quick login submission
     * - Optional session timeout notification message
     */
    @FXML
    public void initialize() {
        BooleanBinding anyFieldEmpty = emailField.textProperty().isEmpty()
            .or(passwordField.textProperty().isEmpty());
        signInButton.disableProperty().bind(anyFieldEmpty);

        emailField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> onSignIn();
            }
        });
        passwordField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER -> onSignIn();
            }        
        });

        if (SessionManager.consumeTimedOut()) {
            noticeLabel.setText("You've been signed out due to inactivity. Please sign in again.");
            noticeLabel.setVisible(true);
            noticeLabel.setManaged(true);
        }
    }

    /**
     * Handles user sign-in authentication.
     *
     * Process:
     * 1. Retrieves user by email
     * 2. Verifies password against stored hash
     * 3. Resolves user's active organisation
     * 4. Initialises session state (user + organisation)
     * 5. Navigates to workspace on success
     *
     * Displays an error message if authentication fails.
     */
    @FXML
    protected void onSignIn() {
        errorLabel.setText("");

        String email = emailField.getText().trim();
        String password = passwordField.getText();
        //CRUD READ
        User user = userDAO.getUserByEmail(email);
        if (user == null || !PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            errorLabel.setText("Incorrect combination of email and password.");
            return;
        }

        int orgId = resolveActiveOrgId(user.getUserId());
        Organisation org = organisationDAO.getOrganisationById(orgId);

        SessionManager.setCurrentUser(user);
        SessionManager.setCurrentUserId(user.getUserId());

        if (org != null) {
            SessionManager.setCurrentOrgId(orgId);
            SessionManager.setCurrentOrgName(org.getName());
        } else {
            SessionManager.setCurrentOrgId(0);
            SessionManager.setCurrentOrgName("No Organisation");
        }

        ViewManager.switchToWorkspace();
    }

    /**
     * Finds the active organisation for a user.
     *
     * Iterates through all memberships and returns the organisation
     * marked as active.
     *
     * @param userId ID of the user
     * @return active organisation ID, or 0 if none found
     */
    private int resolveActiveOrgId(int userId) {
        for (OrganisationMembership m : membershipDAO.getMembershipsForUser(userId)) {
            if (m.isActive()) return m.getOrgId();
        }
        return 0;
    }

    /**
     * Navigates the user to the Sign-Up view.
     */
    @FXML
    protected void onGoToSignUp() {
        ViewManager.switchView("sign-up-view.fxml", "Sign Up");
    }
}
