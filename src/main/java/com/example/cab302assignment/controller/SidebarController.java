package com.example.cab302assignment.controller;

import com.example.cab302assignment.app.ViewManager;
import com.example.cab302assignment.dao.OrganisationMembershipDAO;
import com.example.cab302assignment.dao.SqliteOrganisationMembershipDAO;
import com.example.cab302assignment.model.OrganisationMembership;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.model.enums.MemberRole;
import com.example.cab302assignment.service.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SidebarController {
    private static final String SELECTED_STYLE_CLASS = "sidebar-nav-button-selected";

    private final OrganisationMembershipDAO membershipDAO;
    @FXML private BorderPane borderPane;
    @FXML private Button managerDashboardButton;
    @FXML private Button workspaceButton;
    @FXML private Button profileButton;
    @FXML private Button managementButton;
    @FXML private Label accountNameLabel;
    @FXML private Label accountEmailLabel;

    public SidebarController(){
        this(new SqliteOrganisationMembershipDAO());
    }

    public SidebarController(OrganisationMembershipDAO membershipDAO) {
        this.membershipDAO = membershipDAO;
    }

    @FXML
    private void initialize() {
        refreshAccountInfo();

        // Check if user is manager
        boolean isManager = isCurrentUserManagerForOrg();
        setManagerNav(isManager);

        if(isManager){
            setActiveButton(workspaceButton);
            loadPage("workspace");
        } else {
            setActiveButton(workspaceButton);
            loadPage("workspace");
        }

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
    private void managerDashboard() {
        if(!isCurrentUserManagerForOrg()){
            setActiveButton(workspaceButton);
            loadPage("workspace");
            return;
        }
        setActiveButton(managerDashboardButton);
        loadPage("manager-dashboard");
    }

    @FXML
    private void workspace() {
        setActiveButton(workspaceButton);
        loadPage("workspace");
    }

    @FXML
    private void profile() {
        setActiveButton(profileButton);
        loadPage("profile");
    }

    @FXML
    private void management() {
        if (!isCurrentUserManagerForOrg()) {
            setActiveButton(workspaceButton);
            loadPage("workspace");
            return;
        }
        setActiveButton(managementButton);
        loadPage("management");
    }

    @FXML
    private void onLogout() {
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

    // Private helper method to set manager navigation visibility
    private void setManagerNav(boolean visible){
        if (managerDashboardButton != null) {
            managerDashboardButton.setVisible(visible);
            managerDashboardButton.setManaged(visible);
        }
        if (managementButton != null) {
            managementButton.setVisible(visible);
            managementButton.setManaged(visible);
        }
    }

    // Private helper method to check if current user a manager in an organization
    private boolean isCurrentUserManagerForOrg(){
        User user = SessionManager.getCurrentUser();
        int userId = user.getUserId();

        int orgId = SessionManager.getCurrentOrgId();
        if(orgId < 0){
            return false;
        }

        // Check for membership
        OrganisationMembership membership = membershipDAO.getMembership(userId, orgId);
        return membership != null
                && membership.isActive()
                && membership.getMemberRole() == MemberRole.MANAGER;
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
