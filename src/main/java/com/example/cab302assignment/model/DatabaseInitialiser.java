package com.example.cab302assignment.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitialiser {
    public static void createTables() {
        Connection connection = DatabaseConnection.getInstance();
        try {
            Statement stmt = connection.createStatement();

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "email VARCHAR NOT NULL UNIQUE, "
                + "fullName VARCHAR NOT NULL, "
                + "passwordHash VARCHAR NOT NULL, "
                + "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")"
            );

        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }
}
