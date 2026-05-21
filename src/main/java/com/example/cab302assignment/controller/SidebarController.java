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

/**
 * Controller for the application's left-hand sidebar navigation.
 *
 * <p>The sidebar is responsible for swapping the centre content between the workspace,
 * profile, manager dashboard, and management pages. It also displays a
 * summary of the currently signed-in user and handles logout.</p>
 *
 * <p>Manager-only navigation items are shown or hidden based on whether the
 * signed-in {@link User} holds the {@link MemberRole#MANAGER} role in the
 * current organisation, as resolved through {@link SessionManager} and the
 * injected {@link OrganisationMembershipDAO}.</p>
 */
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

    /**
     * Constructs a {@code SidebarController} backed by the default
     * SQLite-based {@link OrganisationMembershipDAO}.
     *
     * <p>This is the constructor used by JavaFX's {@link FXMLLoader} when no
     * explicit controller factory is supplied.</p>
     */
    public SidebarController(){
        this(new SqliteOrganisationMembershipDAO());
    }

    /**
     * Constructs a {@code SidebarController} with an explicit membership DAO.
     *
     * <p>Intended primarily for testing, where an in-memory or mock DAO can
     * be supplied to control the manager-role resolution.</p>
     *
     * @param membershipDAO the DAO used to look up organisation memberships
     */
    public SidebarController(OrganisationMembershipDAO membershipDAO) {
        this.membershipDAO = membershipDAO;
    }

    /**
     * Initialises the sidebar after FXML injection has completed.
     *
     * <p>Populates the account labels, toggles the visibility of
     * manager-only navigation items based on the signed-in user's role, and
     * loads the workspace page as the default landing view.</p>
     */
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

    /**
     * Refreshes the account name and email labels from the current session.
     *
     * <p>If no user is signed in, the labels are set to {@code "Signed out"}
     * and an empty string. Otherwise the user's name is shown (falling back
     * to the email when the name is missing or blank), along with the email
     * address.</p>
     */
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

    /**
     * Handles navigation to the manager dashboard.
     *
     * <p>If the current user is not a manager of the active organisation,
     * falls back to loading the workspace page instead. This guards against
     * the manager button being triggered programmatically in a non-manager
     * session.</p>
     */
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

    /**
     * Handles navigation to the workspace page and marks the workspace
     * button as the active sidebar item.
     */
    @FXML
    private void workspace() {
        setActiveButton(workspaceButton);
        loadPage("workspace");
    }

    /**
     * Handles navigation to the profile page and marks the profile button
     * as the active sidebar item.
     */
    @FXML
    private void profile() {
        setActiveButton(profileButton);
        loadPage("profile");
    }

    /**
     * Handles navigation to the management page.
     *
     * <p>Only managers of the active organisation may open this view; for
     * any other user the controller redirects to the workspace page
     * instead.</p>
     */
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

    /**
     * Handles the logout action.
     *
     * <p>Clears the current session via {@link SessionManager#logout()} and
     * switches the application to the sign-in view.</p>
     */
    @FXML
    private void onLogout() {
        SessionManager.logout();
        ViewManager.switchView("sign-in-view.fxml", "Sign In");
    }

    /**
     * Marks the supplied button as the active sidebar item.
     *
     * <p>Clears the selected style class from every navigation button and
     * then applies it to {@code clickedButton}. A {@code null} argument is a
     * no-op.</p>
     *
     * @param clickedButton the button to mark as active
     */
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

    /**
     * Removes the selected-style CSS class from the given button if present.
     *
     * @param button the button to clear; {@code null} is ignored
     */
    private void removeSelectedStyle(Button button) {
        if (button != null) {
            button.getStyleClass().remove(SELECTED_STYLE_CLASS);
        }
    }

    /**
     * Toggles the visibility and managed state of the manager-only
     * navigation buttons.
     *
     * <p>Both visibility and managed flags are updated so that hidden
     * buttons do not occupy space in the sidebar layout.</p>
     *
     * @param visible {@code true} to show the manager navigation items,
     *                {@code false} to hide them
     */
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

    /**
     * Determines whether the signed-in user is an active manager of the
     * currently selected organisation.
     *
     * <p>Returns {@code false} when no organisation is selected
     * ({@code orgId < 0}), when no membership exists, when the membership is
     * inactive, or when the role is not {@link MemberRole#MANAGER}.</p>
     *
     * @return {@code true} if the current user is an active manager of the
     *         current organisation; {@code false} otherwise
     */
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

    /**
     * Loads the FXML view for the given page name into the centre of the
     * sidebar's {@link BorderPane}.
     *
     * <p>The resource path is resolved as
     * {@code /com/example/cab302assignment/views/<page>.fxml}. Missing
     * resources and I/O failures are logged at {@link Level#SEVERE} but do
     * not propagate, so a broken navigation target will not crash the
     * application shell.</p>
     *
     * @param page the page identifier (without the {@code .fxml} suffix)
     */
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
