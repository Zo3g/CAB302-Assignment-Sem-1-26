package com.example.cab302assignment;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LandingController {
    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getFullName() + "!");
        }
    }

    @FXML
    protected void onSignOut() {
        SessionManager.logout();
        ViewManager.switchView("sign-in-view.fxml", "Sign In");
    }
}
