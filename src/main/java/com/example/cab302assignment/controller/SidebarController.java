package com.example.cab302assignment.controller;

import com.example.cab302assignment.app.ViewManager;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.service.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SidebarController {
    private static final String SELECTED_STYLE_CLASS = "sidebar-nav-button-selected";

    @FXML private BorderPane borderPane;
    @FXML private AnchorPane anchorPane;
    @FXML private Button managerDashboardButton;
    @FXML private Button workspaceButton;
    @FXML private Button profileButton;
    @FXML private Button managementButton;
    @FXML private Label accountNameLabel;
    @FXML private Label accountEmailLabel;

    @FXML
    private void initialize() {
        refreshAccountInfo();
        setActiveButton(managerDashboardButton);
        loadPage("manager-dashboard");
    }

    private void refreshAccountInfo() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            accountNameLabel.setText("Signed out");
            accountEmailLabel.setText("");
            return;
        }
        accountNameLabel.setText(user.getName() == null || user.getName().isBlank() ? user.getEmail() : user.getName());
        accountEmailLabel.setText(user.getEmail());
    }

    @FXML
    private void managerDashboard(ActionEvent event) {
        setActiveButton(managerDashboardButton);
        loadPage("manager-dashboard");
    }

    @FXML
    private void workspace(ActionEvent event) {
        setActiveButton(workspaceButton);
        loadPage("workspace");
    }

    @FXML
    private void profile(ActionEvent event) {
        setActiveButton(profileButton);
        loadPage("profile");
    }

    @FXML
    private void management(ActionEvent event) {
        setActiveButton(managementButton);
        loadPage("management");
    }

    @FXML
    private void onLogout(ActionEvent event) {
        SessionManager.logout();
        ViewManager.switchView("sign-in-view.fxml", "Sign In");
    }

    private void setActiveButton(Button clickedButton) {
        if (clickedButton == null) {
            return;
        }

        removeSelectedStyle(managerDashboardButton);
        removeSelectedStyle(workspaceButton);
        removeSelectedStyle(profileButton);
        removeSelectedStyle(managementButton);

        if (!clickedButton.getStyleClass().contains(SELECTED_STYLE_CLASS)) {
            clickedButton.getStyleClass().add(SELECTED_STYLE_CLASS);
        }
    }

    private void removeSelectedStyle(Button button) {
        if (button != null) {
            button.getStyleClass().remove(SELECTED_STYLE_CLASS);
        }
    }

    private void loadPage(String page) {
        try {
            String resourcePath = "/com/example/cab302assignment/views/" + page + ".fxml";
            URL fxmlLocation = getClass().getResource(resourcePath);
            if (fxmlLocation == null) {
                throw new IllegalArgumentException("FXML not found: " + resourcePath);
            }

            Parent root = FXMLLoader.load(fxmlLocation);
            borderPane.setCenter(root);
        } catch (IOException | IllegalArgumentException ex) {
            Logger.getLogger(SidebarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
