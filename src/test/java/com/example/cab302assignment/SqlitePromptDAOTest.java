package com.example.cab302assignment;

import com.example.cab302assignment.dao.SqlitePromptDAO;
import com.example.cab302assignment.db.DatabaseInitialiser;
import com.example.cab302assignment.model.PromptHistorySummary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqlitePromptDAOTest {
    private Connection connection;
    private SqlitePromptDAO promptDAO;

    // These tests are only for the more complex getHistoryForUser method.

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        DatabaseInitialiser.createTables(connection);
        promptDAO = new SqlitePromptDAO(connection);

        try (Statement stmt = connection.createStatement()) {
            // Create an org and two users
            stmt.execute("INSERT INTO organisations (name) VALUES ('Test Org')"); // Becomes orgId 1
            stmt.execute("INSERT INTO users (email, name, passwordHash) VALUES ('john@example.com', 'Johm', 'hash')"); // Becomes userId 1
            stmt.execute("INSERT INTO users (email, name, passwordHash) VALUES ('alice@example.com', 'Alice', 'hash')"); // Becomes userId 2

            // 1. Complete data for John (Has a score and redactions)
            stmt.execute("INSERT INTO prompts (orgId, userId, redactedText, submittedAt) VALUES (1, 1, 'Prompt 1', '2026-05-11 10:00:00')"); // Becomes promptId 1
            stmt.execute("INSERT INTO risk_analyses (promptId, riskLevel, score) VALUES (1, 'HIGH', 85.5)");
            stmt.execute("INSERT INTO redaction_results (promptId, redactedText, totalDetections) VALUES (1, 'Prompt 1', 3)");

            // 2. Incomplete data for John (COALESCE test)
            stmt.execute("INSERT INTO prompts (orgId, userId, redactedText, submittedAt) VALUES (1, 1, 'Prompt 2', '2026-05-11 11:00:00')"); // Becomes promptId 2

            // 3. Belongs to Alice (To test the WHERE statement)
            stmt.execute("INSERT INTO prompts (orgId, userId, redactedText, submittedAt) VALUES (1, 2, 'Prompt 3', '2026-05-11 12:00:00')"); // Becomes promptId 3
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testGetHistoryForUserFiltersCorrectly() {
        List<PromptHistorySummary> history = promptDAO.getHistoryForUser(1);
        //It should ignore Alice's prompt entirely
        assertEquals(2, history.size(), "Should only return prompts belonging to userId 1");
    }

    @Test
    void testGetHistoryForUserOrdersByDateDesc() {
        List<PromptHistorySummary> history = promptDAO.getHistoryForUser(1);

        // Prompt 2 (11:00) should appear before Prompt 1 (10:00)
        PromptHistorySummary newestPrompt = history.get(0);
        assertEquals("Prompt 2", newestPrompt.getRedactedText());
    }

    @Test
    void testGetHistoryForUserHandlesMissingData() {
        List<PromptHistorySummary> history = promptDAO.getHistoryForUser(1);

        PromptHistorySummary incompletePrompt = history.get(0);

        // The COALESCE function in the SQL should handle nulls properly
        assertEquals("PENDING", incompletePrompt.getRiskLevel());
        assertEquals(0.0, incompletePrompt.getScore());
        assertEquals(0, incompletePrompt.getTotalDetections());
    }

    @Test
    void testGetHistoryForUserReturnsCompleteData() {
        List<PromptHistorySummary> history = promptDAO.getHistoryForUser(1);

        PromptHistorySummary completePrompt = history.get(1);

        // Standard joins should work
        assertEquals("Prompt 1", completePrompt.getRedactedText());
        assertEquals("HIGH", completePrompt.getRiskLevel());
        assertEquals(85.5, completePrompt.getScore());
        assertEquals(3, completePrompt.getTotalDetections());
    }
}