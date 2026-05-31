package com.example.cab302assignment.controller;

import com.example.cab302assignment.model.PromptHistorySummary;
import com.example.cab302assignment.dao.SqliteUserDAO;
import com.example.cab302assignment.dao.SqliteUserRiskScoreDAO;
import com.example.cab302assignment.dao.SqlitePromptDAO;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.model.UserRiskScore;
import com.example.cab302assignment.model.enums.RiskLevel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.util.List;

/**
 * Controller responsible for managing the Member Drilldown view.
 * This view displays a specific user's current categorical risk score,
 * and a detailed table of their prompt analysis history.
 */
public class MemberDrilldownController {

    @FXML private Label memberNameLabel;
    @FXML private Label memberEmailLabel;
    @FXML private Label memberScoreLabel;

    @FXML private TableView<PromptHistorySummary> historyTable;
    @FXML private TableColumn<PromptHistorySummary, String> dateColumn;
    @FXML private TableColumn<PromptHistorySummary, String> promptColumn;
    @FXML private TableColumn<PromptHistorySummary, String> riskColumn;
    @FXML private TableColumn<PromptHistorySummary, Integer> detectionsColumn;

    /**
     * The ID of the user currently being viewed in the drilldown.
     */
    private int currentUserId;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. It sets up the table column
     * factories and custom text wrapping for long prompts.
     * States text that will be used in the header tooltips
     */
    @FXML
    public void initialize() {
        // Table columns that align with PromptHistorySummary
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        promptColumn.setCellValueFactory(new PropertyValueFactory<>("redactedText"));
        riskColumn.setCellValueFactory(new PropertyValueFactory<>("riskLevel"));
        detectionsColumn.setCellValueFactory(new PropertyValueFactory<>("totalDetections"));

        addHeaderTooltip(dateColumn, "Date");
        addHeaderTooltip(promptColumn, "Prompt");
        addHeaderTooltip(riskColumn, "Risk Level");
        addHeaderTooltip(detectionsColumn, "Detections");

        // Custom cell factory to allow long prompts to wrap around
        promptColumn.setCellFactory(tc -> {
            javafx.scene.control.TableCell<PromptHistorySummary, String> cell = new javafx.scene.control.TableCell<>() {
                private final javafx.scene.text.Text text = new javafx.scene.text.Text();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        text.setText(item);
                        text.wrappingWidthProperty().bind(promptColumn.widthProperty().subtract(10)); // 10px padding
                        setGraphic(text);
                    }
                }
            };

            cell.setPrefHeight(javafx.scene.control.Control.USE_COMPUTED_SIZE);
            return cell;
        });
    }

    /**
     * Initializes the view with a specific user's data.
     * This must be called immediately after the FXML is loaded but before
     * it is displayed to the user to ensure the correct data is fetched.
     *
     * @param userId The unique database ID of the user to display.
     */
    public void initData(int userId) {
        this.currentUserId = userId;

        loadUserProfile();
        loadUserHistory();
    }

    /**
     * Fetches the user's core profile details and their latest calculated
     * risk score from the database, then updates the corresponding UI labels.
     */
    private void loadUserProfile() {
        SqliteUserDAO userDAO = new SqliteUserDAO();
        SqliteUserRiskScoreDAO scoreDAO = new SqliteUserRiskScoreDAO();

        User user = userDAO.getUserById(currentUserId);
        if (user != null) {
            memberNameLabel.setText(user.getName());
            memberEmailLabel.setText(user.getEmail());
        }

        UserRiskScore riskScore = scoreDAO.getLatestForUser(currentUserId);
        if (riskScore != null) {
            memberScoreLabel.setText(String.format(riskScore.getRiskLevel().scoreString()));
        } else {
            memberScoreLabel.setText("No Data");
        }
    }

    /**
     * Fetches the user's historical prompt submissions and their corresponding
     * AI risk analyses from the database, then populates the history table.
     */
    private void loadUserHistory() {
        SqlitePromptDAO promptDAO = new SqlitePromptDAO();

        // Fetch the joined timeline from SQLite
        List<PromptHistorySummary> historyData = promptDAO.getHistoryForUser(currentUserId);

        // Push it to the UI
        ObservableList<PromptHistorySummary> tableData = FXCollections.observableArrayList(historyData);
        historyTable.setItems(tableData);
    }

    /**
     * Replaces the standard text of a TableColumn header with a custom Label containing a Tooltip.
     * Because JavaFX TableColumns do not natively support tooltips on their headers, this method
     * uses a graphic node workaround to provide a visual cue that the column is sortable.
     *
     * @param column     The JavaFX TableColumn to modify. Allows any generic type.
     * @param headerText The text to display inside the new header label.
     */

    private void addHeaderTooltip(TableColumn<?, ?> column, String headerText) {
        Label label = new Label(headerText);

        Tooltip headerTooltip = new Tooltip("Click to sort by " + headerText);
        headerTooltip.setShowDelay(javafx.util.Duration.millis(0));

        headerTooltip.getStyleClass().add("custom-tooltip");

        label.setTooltip(headerTooltip);
        column.setGraphic(label);
        column.setText("");
    }
}