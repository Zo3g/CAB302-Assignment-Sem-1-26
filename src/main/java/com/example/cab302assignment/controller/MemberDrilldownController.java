package com.example.cab302assignment.controller;

import com.example.cab302assignment.model.PromptHistorySummary;
import com.example.cab302assignment.dao.SqliteUserDAO;
import com.example.cab302assignment.dao.SqliteUserRiskScoreDAO;
import com.example.cab302assignment.dao.SqlitePromptDAO;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.model.UserRiskScore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MemberDrilldownController {

    @FXML private Label memberNameLabel;
    @FXML private Label memberEmailLabel;
    @FXML private Label memberScoreLabel;

    @FXML private TableView<PromptHistorySummary> historyTable;
    @FXML private TableColumn<PromptHistorySummary, String> dateColumn;
    @FXML private TableColumn<PromptHistorySummary, String> promptColumn;
    @FXML private TableColumn<PromptHistorySummary, String> riskColumn;
    @FXML private TableColumn<PromptHistorySummary, Double> scoreColumn;
    @FXML private TableColumn<PromptHistorySummary, Integer> detectionsColumn;

    private int currentUserId;

    @FXML
    public void initialize() {
        // Table columns that align with PromptHistorySummary
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        promptColumn.setCellValueFactory(new PropertyValueFactory<>("redactedText"));
        riskColumn.setCellValueFactory(new PropertyValueFactory<>("riskLevel"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        detectionsColumn.setCellValueFactory(new PropertyValueFactory<>("totalDetections"));
    }

    // Method for the dashboard to provide the specific user ID
    public void initData(int userId) {
        this.currentUserId = userId;

        loadUserProfile();
        loadUserHistory();
    }

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
            // Format user risk score to 1 decimal place
            memberScoreLabel.setText(String.format("%.1f", riskScore.getScore()));
        } else {
            memberScoreLabel.setText("0.0");
        }
    }

    private void loadUserHistory() {
        SqlitePromptDAO promptDAO = new SqlitePromptDAO();

        // Fetch the joined timeline from SQLite
        List<PromptHistorySummary> historyData = promptDAO.getHistoryForUser(currentUserId);

        // Push it to the UI
        ObservableList<PromptHistorySummary> tableData = FXCollections.observableArrayList(historyData);
        historyTable.setItems(tableData);
    }

}