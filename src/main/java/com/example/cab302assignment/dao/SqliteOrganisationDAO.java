package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.Organisation;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The real {@link OrganisationDAO} that talks to the SQLite database.
 *
 * <p>Runs SQL against the organisations table using prepared statements. Like
 * the other SQLite DAOs, any errors just get printed to System.err so one bad
 * query doesn't take down the app.</p>
 */
public class SqliteOrganisationDAO implements OrganisationDAO {
    /** The format SQLite uses for timestamps, used when parsing createdAt. */
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** The database connection used for all queries. */
    private final Connection connection;

    /** Default constructor - uses the shared singleton connection. */
    public SqliteOrganisationDAO() { this.connection = DatabaseConnection.getInstance(); }

    /**
     * Constructor for passing in your own connection (used in tests).
     *
     * @param connection the connection to use
     */
    public SqliteOrganisationDAO(Connection connection) { this.connection = connection; }

    /**
     * Inserts a new organisation and reads back its auto-generated ID.
     *
     * @param org the organisation to add
     */
    @Override
    public void addOrganisation(Organisation org) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO organisations (name) VALUES (?)");
            stmt.setString(1, org.getName());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) org.setOrgId(keys.getInt(1));
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Looks up an organisation by its ID.
     *
     * @param orgId the organisation's ID
     * @return the organisation, or null if not found
     */
    @Override
    public Organisation getOrganisationById(int orgId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM organisations WHERE orgId = ?");
            stmt.setInt(1, orgId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    /**
     * Looks up an organisation by its name.
     *
     * @param name the name to search for
     * @return the organisation, or null if not found
     */
    @Override
    public Organisation getOrganisationByName(String name) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM organisations WHERE name = ?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    /**
     * Grabs every organisation in the table.
     *
     * @return a list of all organisations
     */
    @Override
    public List<Organisation> getAllOrganisations() {
        List<Organisation> orgs = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM organisations");
            while (rs.next()) orgs.add(map(rs));
        } catch (SQLException ex) { System.err.println(ex); }
        return orgs;
    }

    /**
     * Updates an organisation's name, matched by ID.
     *
     * @param org the organisation with the updated details
     */
    @Override
    public void updateOrganisation(Organisation org) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE organisations SET name = ? WHERE orgId = ?");
            stmt.setString(1, org.getName());
            stmt.setInt(2, org.getOrgId());
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Deletes the organisation with the given ID.
     *
     * @param orgId the ID of the organisation to delete
     */
    @Override
    public void deleteOrganisation(int orgId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM organisations WHERE orgId = ?");
            stmt.setInt(1, orgId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Helper that builds an Organisation from a result set row. The createdAt
     * column might be null so we handle that before parsing.
     *
     * @param rs the result set positioned on the row to read
     * @return an Organisation built from that row
     * @throws SQLException if a column can't be read
     */
    private Organisation map(ResultSet rs) throws SQLException {
        String createdAtStr = rs.getString("createdAt");
        LocalDateTime createdAt = createdAtStr == null ? null : LocalDateTime.parse(createdAtStr, DT);
        return new Organisation(rs.getInt("orgId"), rs.getString("name"), createdAt);
    }
}
