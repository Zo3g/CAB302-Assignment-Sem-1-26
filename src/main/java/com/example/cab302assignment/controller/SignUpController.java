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

/**
 * Controller for the Sign-Up view.
 *
 * Handles creation of new user accounts including:
 * - Input validation (name, email, password rules)
 * - Duplicate email checking
 * - Password hashing
 * - User creation and persistence
 * - Navigation back to sign-in after successful registration
 */
public class SignUpController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;
    @FXML
    private Button signUpButton;
    @FXML
    private Label lengthReqLabel;
    @FXML
    private Label upperReqLabel;
    @FXML
    private Label lowerReqLabel;
    @FXML
    private Label digitReqLabel;

    private final UserDAO userDAO;

    /**
     * Default constructor using SQLite implementation of UserDAO.
     */
    public SignUpController() {
        this.userDAO = new SqliteUserDAO();
    }

    /**
     * Constructor allowing injection of a custom UserDAO.
     */
    public SignUpController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    /**
     * Initialises the sign-up form UI.
     *
     * Disables the sign-up button until all required fields are filled.
     *
     * Creates labels for password requirement that actively check if its being fulfilled.
     *
     */
    @FXML
    public void initialize() {
        BooleanBinding anyFieldEmpty = nameField.textProperty().isEmpty()
            .or(emailField.textProperty().isEmpty())
            .or(passwordField.textProperty().isEmpty())
            .or(confirmPasswordField.textProperty().isEmpty());
        signUpButton.disableProperty().bind(anyFieldEmpty);

        if (passwordField != null) {
            passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
                updateRequirementUI(lengthReqLabel, newValue.length() >= 8, "Minimum 8 characters");
                updateRequirementUI(upperReqLabel, newValue.matches(".*[A-Z].*"), "At least one uppercase letter");
                updateRequirementUI(lowerReqLabel, newValue.matches(".*[a-z].*"), "At least one lowercase letter");
                updateRequirementUI(digitReqLabel, newValue.matches(".*\\d.*"), "At least one digit");
            });
        }
    }

    /**
     * Handles user registration.
     *
     * Process:
     * 1. Clears previous messages
     * 2. Validates user input (name, email, password rules)
     * 3. Checks for existing account with same email
     * 4. Hashes password and creates new user
     * 5. Saves user to database
     * 6. Shows success message and redirects to sign-in screen
     *
     * Redirect includes a short delay for user feedback.
     */
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

    /**
     * Navigates directly to the Sign-In view.
     */
    @FXML
    protected void onGoToSignIn() {
        ViewManager.switchView("sign-in-view.fxml", "Sign In");
    }

    /**
     * Updates the visual state of a specific password requirement label.
     * Changes the text to include a checkmark or a cross, and updates the text color
     * to green (met) or red (unmet).
     *
     * @param label   The JavaFX Label associated with the specific password requirement.
     * @param isMet   A boolean indicating whether the user's input currently satisfies the rule.
     * @param reqText The descriptive text of the requirement (e.g., "Minimum 8 characters").
     */
    private void updateRequirementUI(Label label, boolean isMet, String reqText) {
        if (label == null) return;

        if (isMet) {
            label.setText("✔ " + reqText);
            label.setStyle("-fx-text-fill: green;");
        } else {
            label.setText("✖ " + reqText);
            label.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Validates user registration input fields.
     *
     * Checks:
     * - Name is not empty
     * - Email format is valid
     * - Password meets security requirements:
     *   - Minimum 8 characters
     *   - At least one uppercase letter
     *   - At least one lowercase letter
     *   - At least one digit
     * - Password and confirmation match
     *
     * @param name user full name
     * @param email user email address
     * @param password chosen password
     * @param confirmPassword repeated password entry
     * @return error message if validation fails, or null if valid
     */
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

        if (password.length() < 8 ||
                !password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") ||
                !password.matches(".*\\d.*")) {
            return "Please ensure all password requirements are met.";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        return null;
    }
}
