package com.example.cab302assignment;

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
    @FXML private Button signInButton;

    private final UserDAO userDAO;

    public SignInController() {
        this.userDAO = new SqliteUserDAO();
    }

    public SignInController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @FXML
    public void initialize() {
        BooleanBinding anyFieldEmpty = emailField.textProperty().isEmpty()
            .or(passwordField.textProperty().isEmpty());
        signInButton.disableProperty().bind(anyFieldEmpty);
    }

    @FXML
    protected void onSignIn() {
        errorLabel.setText("");

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        User user = userDAO.getUserByEmail(email);
        if (user == null || !PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            errorLabel.setText("Incorrect combination of email and password.");
            return;
        }

        SessionManager.setCurrentUser(user);
        ViewManager.switchToWorkspace();
    }

    @FXML
    protected void onGoToSignUp() {
        ViewManager.switchView("sign-up-view.fxml", "Sign Up");
    }
}
