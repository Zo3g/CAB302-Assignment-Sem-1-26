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

public class ManagementController {
    @FXML private TextField searchTextField;
    @FXML private GridPane userInfoGrid;
    @FXML private Text userIdText;
    @FXML private Text nameText;
    @FXML private Text emailText;
    @FXML private Text statusText;
    @FXML private Text warningText;
    @FXML private CheckBox confirmCheckBox;
    @FXML private Button removeButton;
    @FXML private Button addButton;
    @FXML private VBox removeGroup;

    private final OrganisationManager organisationManager;
    private MemberInfo searchedUserInfo;

    public ManagementController() {
        this.organisationManager = new OrganisationManager(
            new SqliteOrganisationDAO(),
            new SqliteOrganisationMembershipDAO(),
            new SqliteUserDAO()
        );
    }

    public ManagementController(OrganisationManager organisationManager) {
        this.organisationManager = organisationManager;
    }

    private int getCurrentUserId() {
        return SessionManager.getCurrentUser().getUserId();
    }

    private int getCurrentOrgId() {
        return SessionManager.getCurrentOrgId();
    }

    @FXML
    public void initialize() {
        syncUser();
    }

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

    private void displayMemberInfo(MemberInfo memberInfo) {
        userInfoGrid.setVisible(true);
        userInfoGrid.setManaged(true);

        userIdText.setText(String.valueOf(memberInfo.getUserId()));
        nameText.setText(memberInfo.getName());
        emailText.setText(memberInfo.getEmail());
    }

    @FXML
    private void onSearch() { syncUser(); }

    @FXML
    private void onCheckboxClicked() {
        removeButton.setDisable(!confirmCheckBox.isSelected());
    }

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

    private void hideActionControls() {
        confirmCheckBox.setSelected(false);
        removeButton.setDisable(true);

        removeGroup.setVisible(false);
        removeGroup.setManaged(false);

        addButton.setVisible(false);
        addButton.setManaged(false);
    }

    private void showMessage(String message) {
        warningText.setText(message);
        warningText.setVisible(true);
        warningText.setManaged(true);
    }
}
