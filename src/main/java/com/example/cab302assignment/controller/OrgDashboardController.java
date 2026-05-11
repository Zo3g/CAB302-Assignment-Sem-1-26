package com.example.cab302assignment.controller;

import com.example.cab302assignment.dao.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.model.Prompt;
import com.example.cab302assignment.model.*;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import com.example.cab302assignment.dao.SqliteRedactionResultDAO;
import com.example.cab302assignment.dao.RedactionResultDAO;
import com.example.cab302assignment.dao.SqliteUserDAO;
import com.example.cab302assignment.dao.UserDAO;
import com.example.cab302assignment.dao.SqliteUserRiskScoreDAO;
import com.example.cab302assignment.dao.UserRiskScoreDAO;
import com.example.cab302assignment.dao.SqliteOrganisationMembershipDAO;
import com.example.cab302assignment.dao.OrganisationMembershipDAO;

import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.*;

import java.time.format.DateTimeFormatter;

public class OrgDashboardController {
    @FXML private Label currentRiskCategory;
    @FXML private Label totalUsersCount;
    @FXML private Label orgName;
    @FXML private PieChart sensitiveDataPieChart;
    @FXML private GridPane root;
    @FXML private GridPane lhs_container;
    @FXML private VBox rhs_container;
    @FXML private HBox lhsTop_container;
    @FXML private VBox orgRiskContainer;
    @FXML private AnchorPane pieChartContainer;
    @FXML private BarChart<String, Number> promptsBarChart;
    @FXML private TableView<OrganisationMembership> userTable;
    @FXML private TableColumn<OrganisationMembership, String> nameCol;
    @FXML private TableColumn<OrganisationMembership, String> emailCol;
    @FXML private TableColumn<OrganisationMembership, String> riskCol;

    private String riskCategory = "Medium";


    private final RedactionResultDAO redactionResultDAO;
    private final PromptDAO promptDAO;
    private final UserDAO userDAO;
    private final UserRiskScoreDAO userRiskScoreDAO;
    private final OrganisationMembershipDAO organisationMembershipDAO;

    public OrgDashboardController() {

        redactionResultDAO = new SqliteRedactionResultDAO();
        promptDAO = new SqlitePromptDAO();
        userDAO = new SqliteUserDAO();
        userRiskScoreDAO = new SqliteUserRiskScoreDAO();
        organisationMembershipDAO = new SqliteOrganisationMembershipDAO();
    }

    @FXML
    public void initialize() {
        //lhs_container.prefWidthProperty().bind(root.widthProperty().multiply(0.6));
        //rhs_container.prefWidthProperty().bind(root.widthProperty().multiply(0.4));
        //orgRiskContainer.maxHeightProperty().bind(orgRiskContainer.widthProperty());
        //orgRiskContainer.minHeightProperty().bind(orgRiskContainer.widthProperty());
        //orgRiskContainer.prefHeightProperty().bind(orgRiskContainer.widthProperty());
        //sensitiveDataPieChart.prefHeightProperty().bind(root.widthProperty().multiply(0.4));
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //emailCol.prefWidthProperty().bind(userTable.widthProperty().multiply(0.5));
        //nameCol.prefWidthProperty().bind(userTable.widthProperty().multiply(0.25));
        //riskCol.prefWidthProperty().bind(userTable.widthProperty().multiply(0.25));


        // Placeholder for risk category
        orgRiskContainer.getStyleClass().removeAll(
                "risk-low",
                "risk-medium",
                "risk-high",
                "risk-critical"
        );
        switch (riskCategory) {
            case "Low" -> orgRiskContainer.getStyleClass().add("risk-label-low");
            case "Medium" -> orgRiskContainer.getStyleClass().add("risk-label-medium");
            case "High" -> orgRiskContainer.getStyleClass().add("risk-label-high");
            case "Critical" -> orgRiskContainer.getStyleClass().add("risk-label-critical");
        }
        currentRiskCategory.setText(riskCategory);

        // Placeholder for pie chart
        int promptID = 1;
        RedactionResult result = redactionResultDAO.getByPromptId(promptID);
        loadPieChart(result);

        // Placeholder for bar chart
        int orgID = 1;
        loadBarChart(orgID);

        // Initialise user list columns
        nameCol.setCellValueFactory(cellData -> {
            int userId = cellData.getValue().getUserId();
            User user = userDAO.getUserById(userId);
            return new javafx.beans.property.SimpleStringProperty(
                    user != null ? user.getName() : "Unknown"
            );
        });
        emailCol.setCellValueFactory(cellData -> {
            int userId = cellData.getValue().getUserId();
            User user = userDAO.getUserById(userId);
            return new javafx.beans.property.SimpleStringProperty(
                    user != null ? user.getEmail() : "Unknown"
            );
        });
        riskCol.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    "No Risk"
            );
        });

        // Placeholder for user table
        int usersCount = loadUsers(orgID);
        totalUsersCount.setText("Total Users: " + usersCount);

        orgName.setText("Placeholder Pty Ltd");

    }

    private void loadPieChart(RedactionResult result) {
        PieChart.Data[] data = result.getTypeCounts().entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> {
                    String label = entry.getKey().name().replace("_", " ");
                    int count = entry.getValue();

                    return new PieChart.Data(
                            label + " [" + count + "]",
                            count
                    );
                })
                .toArray(PieChart.Data[]::new);

        sensitiveDataPieChart.getData().clear();
        sensitiveDataPieChart.getData().addAll(data);
        sensitiveDataPieChart.setLegendVisible(false);
    }

    private void loadBarChart(int orgId) {

        List<Prompt> prompts = promptDAO.getPromptsByOrg(orgId);

        // Last 7 days (including today)
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        // Initialize map with 0 counts
        Map<LocalDate, Integer> counts = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            counts.put(startDate.plusDays(i), 0);
        }

        // Count prompts per day
        for (Prompt p : prompts) {
            if (p.getSubmittedAt() != null) {
                LocalDate date = p.getSubmittedAt().toLocalDate();

                if (!date.isBefore(startDate) && !date.isAfter(today)) {
                    counts.put(date, counts.getOrDefault(date, 0) + 1);
                }
            }
        }

        // Create chart series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Prompts");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE"); // Mon, Tue...

        counts.forEach((date, count) -> {
            String label = date.format(formatter);
            series.getData().add(new XYChart.Data<>(label, count));
        });

        // Apply to chart
        promptsBarChart.getData().clear();
        promptsBarChart.getData().add(series);
    }

    public int loadUsers(int orgId) {

        List<OrganisationMembership> memberships =
                organisationMembershipDAO.getMembershipsForOrg(orgId);

        // sort alphabetically by user name
        memberships.sort(Comparator.comparing(m -> {
            User u = userDAO.getUserById(m.getUserId());
            return u != null ? u.getName() : "";
        }));

        userTable.setItems(FXCollections.observableArrayList(memberships));
        userTable.setSelectionModel(null);

        // return total users
        return memberships.size();
    }
}


