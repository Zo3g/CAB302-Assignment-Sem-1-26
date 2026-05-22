package com.example.cab302assignment.controller;

import com.example.cab302assignment.dao.SqliteOrganisationDAO;
import com.example.cab302assignment.dao.SqliteOrganisationMembershipDAO;
import com.example.cab302assignment.dao.SqliteUserDAO;
import com.example.cab302assignment.model.MemberInfo;
import com.example.cab302assignment.service.OrganisationManager;
import com.example.cab302assignment.service.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Controller responsible for organisation member management operations.
 *
 * <p>This controller allows managers to search users,
 * activate or deactivate memberships, and display user information and membership status
 * within the management view.</p>
 */
public class ManagementController {

    /** Input field used to search users by email */
    @FXML private TextField searchTextField;

    /** Grid displaying searched user information */
    @FXML private GridPane userInfoGrid;

    /** Text displaying the user ID */
    @FXML private Text userIdText;

    /** Text displaying the user's name */
    @FXML private Text nameText;

    /** Text displaying the user's email */
    @FXML private Text emailText;

    /** Text displaying membership status information */
    @FXML private Text statusText;

    /** Text displaying warnings or action feedback messages */
    @FXML private Text warningText;

    /** Confirmation checkbox required before deactivation */
    @FXML private CheckBox confirmCheckBox;

    /** Button used to deactivate memberships */
    @FXML private Button removeButton;

    /** Button used to activate memberships */
    @FXML private Button addButton;

    /** Container holding deactivation controls */
    @FXML private VBox removeGroup;

    /** Service handling organisation management logic */
    private final OrganisationManager organisationManager;

    /** Stores the currently searched user information */
    private MemberInfo searchedUserInfo;

    /**
     * Constructs a ManagementController using SQLite DAO implementations.
     */
    public ManagementController() {
        this.organisationManager = new OrganisationManager(
            new SqliteOrganisationDAO(),
            new SqliteOrganisationMembershipDAO(),
            new SqliteUserDAO()
        );
    }

    /**
     * Constructs a ManagementController with a custom OrganisationManager.
     *
     * @param organisationManager the organisation manager service
     */
    public ManagementController(OrganisationManager organisationManager) {
        this.organisationManager = organisationManager;
    }

    /**
     * Gets the currently logged-in user's ID.
     *
     * @return the current user ID
     */
    private int getCurrentUserId() {
        return SessionManager.getCurrentUser().getUserId();
    }

    /**
     * Gets the currently logged-in user's organisation ID
     * if the user has an active membership.
     *
     * @return the current organisation ID
     */
    private int getCurrentOrgId() {
        return SessionManager.getCurrentOrgId();
    }

    /**
     * Initializes the controller state when the view is loaded.
     */
    @FXML
    public void initialize() {
        syncUser();
    }

    /**
     * Synchronises the UI with the searched user's latest membership state.
     *
     * <p>This method performs the user search, updates the displayed member
     * information, and controls the visibility of action buttons depending
     * on membership status.</p>
     */
    private void syncUser() {
        clearOutput();

        String email = searchTextField.getText();
        if (email == null || email.isBlank()) return;

        MemberInfo memberInfo;
        try {
            memberInfo = organisationManager.searchMember(getCurrentUserId(), getCurrentOrgId(), email);
        } catch (RuntimeException ex) {
            statusText.setText("Permission denied");
            return;
        }
        searchedUserInfo = memberInfo;

        if (memberInfo == null) return;
        boolean isSelf = searchedUserInfo.getUserId() == getCurrentUserId();

        switch (memberInfo.getMemberStatus()) {
            case NOT_FOUND -> statusText.setText("User not found");
            case NOT_A_MEMBER -> {
                displayMemberInfo(memberInfo);
                statusText.setText("Not a member");
                addButton.setVisible(true);
                addButton.setManaged(true);
            }
            case ACTIVE -> {
                displayMemberInfo(memberInfo);
                statusText.setText("Active");
                if (isSelf) {
                    warningText.setText("You cannot modify your own membership.");
                    warningText.setVisible(true);
                    warningText.setManaged(true);
                } else {
                    removeGroup.setVisible(true);
                    removeGroup.setManaged(true);
                }
            }
            case DEACTIVATED -> {
                displayMemberInfo(memberInfo);
                statusText.setText("Deactivated");
                if (isSelf) {
                    warningText.setText("You cannot modify your own membership.");
                    warningText.setVisible(true);
                    warningText.setManaged(true);
                } else {
                    addButton.setVisible(true);
                    addButton.setManaged(true);
                }
            }
            default -> clearOutput();
        }
    }

    /**
     * Clears all displayed user information and resets UI controls.
     */
    private void clearOutput() {
        userInfoGrid.setVisible(false);
        userInfoGrid.setManaged(false);
        userIdText.setText("");
        nameText.setText("");
        emailText.setText("");
        statusText.setText("");
        confirmCheckBox.setSelected(false);
        removeButton.setDisable(true);
        removeGroup.setVisible(false);
        removeGroup.setManaged(false);
        addButton.setVisible(false);
        addButton.setManaged(false);
        warningText.setVisible(false);
        warningText.setManaged(false);
    }

    /**
     * Displays member information in the UI.
     *
     * @param memberInfo the member information to display (user ID, name, email)
     */
    private void displayMemberInfo(MemberInfo memberInfo) {
        userInfoGrid.setVisible(true);
        userInfoGrid.setManaged(true);

        userIdText.setText(String.valueOf(memberInfo.getUserId()));
        nameText.setText(memberInfo.getName());
        emailText.setText(memberInfo.getEmail());
    }

    /**
     * Handles the search button or Enter key action.
     */
    @FXML
    private void onSearch() { syncUser(); }

    /**
     * Enables or disables the remove button depending on
     * the confirmation checkbox state.
     */
    @FXML
    private void onCheckboxClicked() {
        removeButton.setDisable(!confirmCheckBox.isSelected());
    }

    /**
     * Handles member deactivation requests.
     *
     * <p>If successful, the member status is refreshed and
     * a success message is displayed.</p>
     */
    @FXML
    private void onRemove() {
        if (searchedUserInfo == null) return;
        try {
            organisationManager.removeMember(
                getCurrentUserId(), searchedUserInfo.getUserId(), getCurrentOrgId()
            );
            syncUser(); // update latest statusText
            hideActionControls(); // hide opposite action button
            showMessage("Membership has been deactivated successfully.");
        } catch (RuntimeException ex) {
            showMessage("Failed to deactivate membership: " + ex.getMessage());
        }
    }

    /**
     * Handles member activation requests.
     *
     * <p>If successful, the member status is refreshed and
     * a success message is displayed.</p>
     */
    @FXML
    private void onAdd() {
        if (searchedUserInfo == null) return;
        try {
            organisationManager.addMember(getCurrentUserId(), searchedUserInfo.getUserId(), getCurrentOrgId());
            syncUser();
            hideActionControls();
            showMessage("Membership has been activated successfully.");
        } catch (RuntimeException ex) {
            showMessage("Failed to activate membership: " + ex.getMessage());
        }
    }

    /**
     * Hides membership action controls after an update operation.
     */
    private void hideActionControls() {
        confirmCheckBox.setSelected(false);
        removeButton.setDisable(true);

        removeGroup.setVisible(false);
        removeGroup.setManaged(false);

        addButton.setVisible(false);
        addButton.setManaged(false);
    }

    /**
     * Displays a feedback or warning message to the user.
     *
     * @param message the message to display
     */
    private void showMessage(String message) {
        warningText.setText(message);
        warningText.setVisible(true);
        warningText.setManaged(true);
    }
}
