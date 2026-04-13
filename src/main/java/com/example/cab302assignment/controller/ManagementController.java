package com.example.cab302assignment.controller;

import com.example.cab302assignment.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ManagementController
{
    @FXML private TextField searchTextField;
    @FXML private Text userIdText;
    @FXML private Text nameText;
    @FXML private Text emailText;
    @FXML private Text statusText;
    @FXML private CheckBox confirmCheckBox;
    @FXML private Button removeButton;
    @FXML private Button addButton;

    private OrganisationManager organisationManager;

    public ManagementController() {
        organisationManager = new OrganisationManager(new SqliteOrganisationDAO(), new SqliteMembershipDAO(), new SqliteUserDAO());
    }

    // for mock test
    public ManagementController(OrganisationManager organisationManager) {
        this.organisationManager = organisationManager;
    }

    private MemberInfo searchedUserInfo;

    // Logged in manager's ID
    private int getCurrentUserId() {
        return SessionManager.getCurrentUser().getId();
    }

    // Logged in manager's org's ID
    private int getCurrentOrgId() {
        return SessionManager.getCurrentOrgId();
    }

    private void syncUser() {
        // clear output
        clearOutput();

        String email = searchTextField.getText();
        if (email == null || email.isBlank()) return;

        MemberInfo memberInfo = organisationManager.searchMember(getCurrentUserId(), getCurrentOrgId(), email);
        searchedUserInfo = memberInfo;

        if (memberInfo == null) return;

        // state machine for different member status handling
        switch (memberInfo.getMemberStatus()) {
            case NOT_FOUND -> {
//                displayMemberInfo(memberInfo);
                statusText.setText("User not found");
            }
            case NOT_A_MEMBER -> {
                displayMemberInfo(memberInfo);
                statusText.setText("Not a member");
                addButton.setVisible(true);
            }
            case ACTIVE -> {
                displayMemberInfo(memberInfo);
                statusText.setText("Active");
                confirmCheckBox.setVisible(true);
                removeButton.setVisible(true);
            }
            default -> clearOutput();
        }
    }

    private void clearOutput() {
        userIdText.setText("");
        nameText.setText("");
        emailText.setText("");
        statusText.setText("");

        confirmCheckBox.setSelected(false);
        confirmCheckBox.setVisible(false);
        removeButton.setDisable(true);
        removeButton.setVisible(false);
        addButton.setVisible(false);
    }

    private void displayMemberInfo(MemberInfo memberInfo) {
        userIdText.setText(String.valueOf(memberInfo.getUserId()));
        nameText.setText(memberInfo.getName());
        emailText.setText(memberInfo.getEmail());
    }

    public void initialize() {
        syncUser();

        // call syncUser when the search text field changes
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> syncUser());
    }

    @FXML
    private void onCheckboxClicked() {
        boolean accepted = confirmCheckBox.isSelected();
        removeButton.setDisable(!accepted);
    }

    @FXML
    private void onRemove() {
        if (searchedUserInfo == null) return;
        organisationManager.removeMember(getCurrentUserId(), searchedUserInfo.getUserId(), getCurrentOrgId());
        syncUser();
    }

    @FXML
    private void onAdd() {
        if (searchedUserInfo == null) return;
        organisationManager.addMember(getCurrentUserId(), searchedUserInfo.getUserId(), getCurrentOrgId());
        syncUser();
    }
}
