package com.example.cab302assignment.controller;

import com.example.cab302assignment.app.ViewManager;
import com.example.cab302assignment.dao.OrganisationMembershipDAO;
import com.example.cab302assignment.dao.SqliteOrganisationMembershipDAO;
import com.example.cab302assignment.dao.SqliteUserDAO;
import com.example.cab302assignment.dao.UserDAO;
import com.example.cab302assignment.model.OrganisationMembership;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.service.PasswordUtil;
import com.example.cab302assignment.service.SessionManager;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignInController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Label noticeLabel;
    @FXML private Button signInButton;

    private final UserDAO userDAO;
    private final OrganisationMembershipDAO membershipDAO;

    public SignInController() {
        this(new SqliteUserDAO(), new SqliteOrganisationMembershipDAO());
    }

    public SignInController(UserDAO userDAO) {
        this(userDAO, new SqliteOrganisationMembershipDAO());
    }

    public SignInController(UserDAO userDAO, OrganisationMembershipDAO membershipDAO) {
        this.userDAO = userDAO;
        this.membershipDAO = membershipDAO;
    }

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

        SessionManager.setCurrentUser(user);
        SessionManager.setCurrentUserId(user.getUserId());
        SessionManager.setCurrentOrgId(resolveActiveOrgId(user.getUserId()));
        ViewManager.switchToWorkspace();
    }

    private int resolveActiveOrgId(int userId) {
        for (OrganisationMembership m : membershipDAO.getMembershipsForUser(userId)) {
            if (m.isActive()) return m.getOrgId();
        }
        return 0;
    }

    @FXML
    protected void onGoToSignUp() {
        ViewManager.switchView("sign-up-view.fxml", "Sign Up");
    }
}
