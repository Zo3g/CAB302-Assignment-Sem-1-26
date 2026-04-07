package com.example.cab302assignment.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SqliteOrganisationDAO implements OrganisationDAO {
    private Connection connection;

    public SqliteOrganisationDAO() {
        connection = DatabaseConnection.getInstance();
    }

    @Override
    public void addOrganisation(Organisation organisation) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO organisations (name) VALUES (?)");
            statement.setString(1, organisation.getName());
            statement.executeUpdate();
            // Set id of the new organisation
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                organisation.setId((generatedKeys.getInt(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrganisation(Organisation organisation) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE organisations SET name = ? WHERE id = ?");
            statement.setString(1, organisation.getName());
            statement.setInt(2, organisation.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOrganisation(int orgId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM organisations WHERE id = ?");
            statement.setInt(1, orgId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Organisation getOrganisationById(int OrgId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM organisations WHERE id = ?");
            statement.setInt(1, OrgId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapOrg(resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Organisation mapOrg(ResultSet resultSet) throws SQLException {
        String createdAtStr = resultSet.getString("created_at");
        LocalDateTime createdAt = null;
        if (createdAtStr != null) {
            createdAt = LocalDateTime.parse(createdAtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return new Organisation(
            resultSet.getInt("id"),
            resultSet.getString("name"),
            createdAt
        );
    }
}
