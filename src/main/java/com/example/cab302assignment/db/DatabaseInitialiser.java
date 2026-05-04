package com.example.cab302assignment.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitialiser {
    public static void createTables() {
        createTables(DatabaseConnection.getInstance());
    }

    public static void createTables(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users ("
                + "userId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "email VARCHAR NOT NULL UNIQUE, "
                + "name VARCHAR NOT NULL, "
                + "passwordHash VARCHAR NOT NULL, "
                + "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS organisations ("
                + "orgId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name VARCHAR NOT NULL UNIQUE, "
                + "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS organisation_memberships ("
                + "userId INTEGER NOT NULL, "
                + "orgId INTEGER NOT NULL, "
                + "memberRole VARCHAR NOT NULL, "
                + "active INTEGER NOT NULL DEFAULT 1, "
                + "joinedAt DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "PRIMARY KEY (userId, orgId), "
                + "FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE, "
                + "FOREIGN KEY (orgId) REFERENCES organisations(orgId) ON DELETE CASCADE"
                + ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS rulesets ("
                + "rulesetId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "orgId INTEGER NOT NULL UNIQUE, "
                + "updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (orgId) REFERENCES organisations(orgId) ON DELETE CASCADE"
                + ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS redaction_rules ("
                + "ruleId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "rulesetId INTEGER NOT NULL, "
                + "name VARCHAR NOT NULL, "
                + "regexPattern TEXT NOT NULL, "
                + "dataType VARCHAR NOT NULL, "
                + "enabled INTEGER NOT NULL DEFAULT 1, "
                + "placeholderFormat VARCHAR NOT NULL, "
                + "FOREIGN KEY (rulesetId) REFERENCES rulesets(rulesetId) ON DELETE CASCADE"
                + ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS prompts ("
                + "promptId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "orgId INTEGER NOT NULL, "
                + "userId INTEGER NOT NULL, "
                + "redactedText TEXT NOT NULL, "
                + "submittedAt DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (orgId) REFERENCES organisations(orgId) ON DELETE CASCADE, "
                + "FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE"
                + ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS redaction_results ("
                + "resultId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "promptId INTEGER NOT NULL UNIQUE, "
                + "redactedText TEXT NOT NULL, "
                + "totalDetections INTEGER NOT NULL DEFAULT 0, "
                + "typeCountsJson TEXT, "
                + "FOREIGN KEY (promptId) REFERENCES prompts(promptId) ON DELETE CASCADE"
                + ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS risk_analyses ("
                + "analysisId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "promptId INTEGER NOT NULL UNIQUE, "
                + "riskLevel VARCHAR NOT NULL, "
                + "score REAL NOT NULL, "
                + "summary TEXT, "
                + "typeCountsJson TEXT, "
                + "analysedAt DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (promptId) REFERENCES prompts(promptId) ON DELETE CASCADE"
                + ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS user_risk_scores ("
                + "scoreId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "userId INTEGER NOT NULL, "
                + "score REAL NOT NULL DEFAULT 0.0, "
                + "totalPromptsAnalysed INTEGER NOT NULL DEFAULT 0, "
                + "lastUpdated DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE"
                + ")"
            );

        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }
}
