package com.example.cab302assignment.controller;

import com.example.cab302assignment.dao.SqliteUserDAO;
import com.example.cab302assignment.model.MemberRiskSummary;
import com.example.cab302assignment.service.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.util.List;

public class OrgRiskDashboardController {

    @FXML
    private TableView<MemberRiskSummary> memberTable;
    @FXML
    private TableColumn<MemberRiskSummary, String> nameColumn;
    @FXML
    private TableColumn<MemberRiskSummary, Double> scoreColumn;

    @FXML
    public void initialize() {
        // Values that the table's columns need
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("riskScore"));

        memberTable.setOnMouseClicked(this::handleRowClick);

        // Load data into the table
        loadDashboardData();
    }

    private void handleRowClick(MouseEvent event) {
        if (event.getClickCount() == 2 && memberTable.getSelectionModel().getSelectedItem() != null) {
            MemberRiskSummary selectedEmployee = memberTable.getSelectionModel().getSelectedItem();


        }
    }

    private void loadDashboardData() {

        int currentOrgId = SessionManager.getCurrentOrgId();
        SqliteUserDAO userDAO = new SqliteUserDAO();

        List<MemberRiskSummary> realData = userDAO.getMemberRiskSummary(currentOrgId);

        ObservableList<MemberRiskSummary> tableData = FXCollections.observableArrayList(realData);
        memberTable.setItems(tableData);
    }
}