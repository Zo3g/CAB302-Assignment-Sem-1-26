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
import javafx.scene.text.Text;

public class ManagementController {
    @FXML private TextField searchTextField;
    @FXML private Text userIdText;
    @FXML private Text nameText;
    @FXML private Text emailText;
    @FXML private Text statusText;
    @FXML private CheckBox confirmCheckBox;
    @FXML private Button removeButton;
    @FXML private Button addButton;

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
        clearOutput();
        searchTextField.textProperty().addListener((obs, oldV, newV) -> syncUser());
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

        switch (memberInfo.getMemberStatus()) {
            case NOT_FOUND -> statusText.setText("User not found");
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
            case DEACTIVATED -> {
                displayMemberInfo(memberInfo);
                statusText.setText("Deactivated");
                addButton.setVisible(true);
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

    @FXML
    private void onCheckboxClicked() {
        removeButton.setDisable(!confirmCheckBox.isSelected());
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
