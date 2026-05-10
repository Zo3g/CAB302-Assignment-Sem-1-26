package com.example.cab302assignment.controller;

import com.example.cab302assignment.model.EmployeeRiskSummary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class OrgRiskDashboardController {

    @FXML
    private TableView<EmployeeRiskSummary> employeeTable;
    @FXML
    private TableColumn<EmployeeRiskSummary, String> nameColumn;
    @FXML
    private TableColumn<EmployeeRiskSummary, Double> scoreColumn;

    @FXML
    public void initialize() {
        // Values that the table's columns need
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("riskScore"));

        // Row click checking to access users' prompt tables
        employeeTable.setOnMouseClicked(this::handleRowClick);

        // Load data into the table
        loadDashboardData();
    }

    private void handleRowClick(MouseEvent event) {
        if (event.getClickCount() == 2 && employeeTable.getSelectionModel().getSelectedItem() != null) {
            EmployeeRiskSummary selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

            System.out.println("Manager wants to drill down into User ID: " + selectedEmployee.getUserId());

        }
    }

    private void loadDashboardData() {
        // Placeholder data
        ObservableList<EmployeeRiskSummary> mockData = FXCollections.observableArrayList(
                new EmployeeRiskSummary(1, "Alice Smith", 12.5),
                new EmployeeRiskSummary(2, "Bob Jones", 0.0),
                new EmployeeRiskSummary(3, "Charlie Davis", 85.0), // Needs a talking to!
                new EmployeeRiskSummary(4, "Dan Developer", 25.0)
        );

        employeeTable.setItems(mockData);
    }
}