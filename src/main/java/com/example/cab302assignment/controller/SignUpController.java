package com.example.cab302assignment.controller;

import com.example.cab302assignment.app.ViewManager;
import com.example.cab302assignment.dao.SqliteUserDAO;
import com.example.cab302assignment.dao.UserDAO;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.service.PasswordUtil;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Button signUpButton;

    private final UserDAO userDAO;

    public SignUpController() {
        this.userDAO = new SqliteUserDAO();
    }

    public SignUpController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @FXML
    public void initialize() {
        BooleanBinding anyFieldEmpty = nameField.textProperty().isEmpty()
            .or(emailField.textProperty().isEmpty())
            .or(passwordField.textProperty().isEmpty())
            .or(confirmPasswordField.textProperty().isEmpty());
        signUpButton.disableProperty().bind(anyFieldEmpty);
    }

    @FXML
    protected void onSignUp() {
        errorLabel.setText("");
        successLabel.setText("");

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        String error = validate(name, email, password, confirmPassword);
        if (error != null) {
            errorLabel.setText(error);
            return;
        }

        if (userDAO.getUserByEmail(email) != null) {
            errorLabel.setText("An account with this email already exists.");
            return;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User(email, name, hashedPassword);
        //CRUD CREATE
        userDAO.addUser(user);

        successLabel.setText("Account created successfully! Redirecting to sign in...");
        signUpButton.disableProperty().unbind();
        signUpButton.setDisable(true);

        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() ->
                ViewManager.switchView("sign-in-view.fxml", "Sign In")
            );
        }).start();
    }

    @FXML
    protected void onGoToSignIn() {
        ViewManager.switchView("sign-in-view.fxml", "Sign In");
    }

    public static String validate(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty()) {
            return "Name is required.";
        }
        if (email.isEmpty()) {
            return "Email address is required.";
        }
        if (!email.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            return "Please enter a valid email address.";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one digit.";
        }
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        return null;
    }
}
