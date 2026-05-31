package com.example.cab302assignment.controller;

import com.example.cab302assignment.app.ViewManager;
import com.example.cab302assignment.dao.*;
import com.example.cab302assignment.model.enums.RiskLevel;
import com.example.cab302assignment.model.enums.SensitiveDataType;
import com.example.cab302assignment.service.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.model.Prompt;
import com.example.cab302assignment.model.*;
import javafx.scene.chart.*;
import javafx.scene.input.MouseEvent;
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

/**
 * Controller for the organisation dashboard view.
 *
 * Handles loading and displaying organisation-wide analytics such as:
 * - Overall risk level across users
 * - Sensitive data breakdown (pie chart)
 * - Prompt activity over time (bar chart)
 * - User membership table with risk indicators
 *
 * Also manages navigation to user drill-down views.
 */
public class OrgDashboardController {
    @FXML private Label currentRiskCategory;
    @FXML private Label totalUsersCount;
    @FXML private Label orgName;
    @FXML private PieChart sensitiveDataPieChart;
    @FXML private GridPane root;
    @FXML private VBox orgRiskContainer;
    @FXML private BarChart<String, Number> promptsBarChart;
    @FXML private TableView<OrganisationMembership> userTable;
    @FXML private TableColumn<OrganisationMembership, String> nameCol;
    @FXML private TableColumn<OrganisationMembership, String> emailCol;
    @FXML private TableColumn<OrganisationMembership, String> riskCol;

    private final RedactionResultDAO redactionResultDAO;
    private final PromptDAO promptDAO;
    private final UserDAO userDAO;
    private final UserRiskScoreDAO userRiskScoreDAO;
    private final OrganisationMembershipDAO organisationMembershipDAO;


    /**
     * Initialises DAO dependencies for retrieving users, prompts,
     * redaction results, and risk scores from the database.
     */
    public OrgDashboardController() {

        redactionResultDAO = new SqliteRedactionResultDAO();
        promptDAO = new SqlitePromptDAO();
        userDAO = new SqliteUserDAO();
        userRiskScoreDAO = new SqliteUserRiskScoreDAO();
        organisationMembershipDAO = new SqliteOrganisationMembershipDAO();
    }

    /**
     * Initialises the dashboard UI after FXML loading.
     *
     * Loads organisation data including:
     * - Overall organisation risk level
     * - Sensitive data distribution pie chart
     * - Prompt activity bar chart (last 7 days)
     * - User membership table
     *
     * Also sets up table interactions and UI labels.
     */
    @FXML
    public void initialize() {
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        int orgId = SessionManager.getCurrentOrgId();

        // Set risk banner
        List<OrganisationMembership> memberships =
                organisationMembershipDAO.getMembershipsForOrg(orgId);

        RiskLevel overallRisk = calculateAverageRiskLevel(memberships);


        orgRiskContainer.getStyleClass().removeAll(
                "risk-label-none",
                "risk-label-low",
                "risk-label-medium",
                "risk-label-high",
                "risk-label-critical"
        );

        switch (overallRisk) {
            case RiskLevel.NO -> orgRiskContainer.getStyleClass().add("risk-label-none");
            case RiskLevel.LOW -> orgRiskContainer.getStyleClass().add("risk-label-low");
            case RiskLevel.MEDIUM -> orgRiskContainer.getStyleClass().add("risk-label-medium");
            case RiskLevel.HIGH -> orgRiskContainer.getStyleClass().add("risk-label-high");
            case RiskLevel.CRITICAL -> orgRiskContainer.getStyleClass().add("risk-label-critical");
        }

        currentRiskCategory.setText(overallRisk.scoreString());

        // Load data detections pie chart
        loadPieChart(orgId);

        // Load prompts bar chart
        loadBarChart(orgId);

        // Set org name
        orgName.setText(SessionManager.getCurrentOrgName());

        // Initialise user table
        initialiseUserTableCols();
        int usersCount = loadUsers(orgId);
        userTable.setOnMouseClicked(this::handleRowClick);
        totalUsersCount.setText("Total Users: " + usersCount);



    }

    /**
     * Calculates the average risk level across all users in the organisation.
     *
     * Retrieves the latest risk score for each user and computes an average,
     * then maps it back to a RiskLevel category.
     *
     * If no valid scores exist, returns NO risk.
     *
     * @param memberships list of organisation memberships
     * @return overall organisation risk level
     */
    private RiskLevel calculateAverageRiskLevel(List<OrganisationMembership> memberships) {

        List<UserRiskScore> scores = new ArrayList<>();

        for (OrganisationMembership membership : memberships) {

            UserRiskScore riskScore =
                    userRiskScoreDAO.getLatestForUser(membership.getUserId());

            if (riskScore != null) {
                scores.add(riskScore);
            }
        }

        if (scores == null || scores.isEmpty()) {
            return RiskLevel.NO;
        }

        double averageScore = scores.stream()
                .mapToDouble(UserRiskScore::getScore)
                .average()
                .orElse(0.0);

        if (averageScore >= RiskLevel.CRITICAL.scoreDouble()) {
            return RiskLevel.CRITICAL;
        } else if (averageScore >= RiskLevel.HIGH.scoreDouble()) {
            return RiskLevel.HIGH;
        } else if (averageScore >= RiskLevel.MEDIUM.scoreDouble()) {
            return RiskLevel.MEDIUM;
        } else if (averageScore >= RiskLevel.LOW.scoreDouble()) {
            return RiskLevel.LOW;
        }

        return RiskLevel.NO;
    }

    /**
     * Loads and aggregates sensitive data detection results into a pie chart.
     *
     * Groups redaction results across all prompts in the organisation and
     * sums occurrences of each SensitiveDataType.
     *
     * @param orgId organisation ID
     */
    private void loadPieChart(int orgId) {
        List<Prompt> prompts = promptDAO.getPromptsByOrg(orgId);
        List<RedactionResult> results = new ArrayList<>();

        for (Prompt p : prompts) {
            RedactionResult r = redactionResultDAO.getByPromptId(p.getPromptId());
            if (r != null) {
                results.add(r);
            }
        }

        Map<SensitiveDataType, Integer> aggregated = new EnumMap<>(SensitiveDataType.class);

        for (RedactionResult result : results) {
            result.getTypeCounts().forEach((type, count) ->
                    aggregated.merge(type, count, Integer::sum)
            );
        }

        PieChart.Data[] data = aggregated.entrySet().stream()
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

    /**
     * Loads prompt submission activity for the last 7 days into a bar chart.
     *
     * Counts how many prompts were submitted per day and displays them
     * using abbreviated weekday labels.
     *
     * @param orgId organisation ID
     */
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

    /**
     * Loads all users in the organisation into the table view.
     *
     * Users are sorted alphabetically by name and displayed with
     * email and risk level information.
     *
     * @param orgId organisation ID
     * @return total number of users loaded
     */
    public int loadUsers(int orgId) {

        List<OrganisationMembership> memberships =
                organisationMembershipDAO.getMembershipsForOrg(orgId);

        memberships.removeIf(m -> !m.isActive());

        // sort alphabetically by user name
        memberships.sort(Comparator.comparing(m -> {
            User u = userDAO.getUserById(m.getUserId());
            return u != null ? u.getName() : "";
        }));

        userTable.setItems(FXCollections.observableArrayList(memberships));

        // return total users
        return memberships.size();
    }

    /**
     * Handles double-click events on the user table.
     *
     * Opens the user drill-down view for the selected user.
     *
     * @param event mouse click event
     */
    private void handleRowClick(MouseEvent event) {
        if (event.getClickCount() == 2 && userTable.getSelectionModel().getSelectedItem() != null) {
            OrganisationMembership selectedMember = userTable.getSelectionModel().getSelectedItem();
            ViewManager.switchToUserDrilldown(selectedMember.getUserId());
        }
    }

    /**
     * Configures the user table columns including:
     * - Name
     * - Email
     * - Risk level (with styled badges)
     *
     * Fetches related user and risk data from DAOs.
     */
    private void initialiseUserTableCols() {
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

            int userId = cellData.getValue().getUserId();

            UserRiskScore userRiskScore =
                    userRiskScoreDAO.getLatestForUser(userId);

            if (userRiskScore == null) {
                return new javafx.beans.property.SimpleStringProperty("No Past Prompts");
            }

            RiskLevel riskLevel = userRiskScore.getRiskLevel();

            return new javafx.beans.property.SimpleStringProperty(
                    riskLevel.scoreString()
            );
        });
        riskCol.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    return;
                }

                Label badge = new Label(item);

                badge.getStyleClass().add("risk-label");

                String lower = item.toLowerCase();

                if (lower.contains("critical")) {
                    badge.getStyleClass().add("risk-label-critical");
                } else if (lower.contains("high")) {
                    badge.getStyleClass().add("risk-label-high");
                } else if (lower.contains("medium")) {
                    badge.getStyleClass().add("risk-label-medium");
                } else if (lower.contains("low")) {
                    badge.getStyleClass().add("risk-label-low");
                } else {
                    badge.getStyleClass().add("risk-label-none");
                }

                setGraphic(badge);
                setText(null);
            }
        });
    }

}


