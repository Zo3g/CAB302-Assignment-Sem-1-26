package com.example.cab302assignment.dao;

import com.example.cab302assignment.db.DatabaseConnection;
import com.example.cab302assignment.model.OrganisationMembership;
import com.example.cab302assignment.model.enums.MemberRole;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The real {@link OrganisationMembershipDAO} backed by SQLite.
 *
 * <p>Handles the organisation_memberships table. A membership is identified by
 * the user/org pair rather than a single ID, so the lookups, updates and
 * deletes all take both userId and orgId. The member role is stored as text
 * (the enum's name) and active is stored as 1 or 0.</p>
 */
public class SqliteOrganisationMembershipDAO implements OrganisationMembershipDAO {
    /** The timestamp format SQLite uses, for parsing joinedAt. */
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** The database connection used for all queries. */
    private final Connection connection;

    /** Default constructor - uses the shared singleton connection. */
    public SqliteOrganisationMembershipDAO() { this.connection = DatabaseConnection.getInstance(); }

    /**
     * Constructor for passing in your own connection (used in tests).
     *
     * @param connection the connection to use
     */
    public SqliteOrganisationMembershipDAO(Connection connection) { this.connection = connection; }

    /**
     * Inserts a new membership. The role gets stored as its name and the
     * active flag as 1 (true) or 0 (false).
     *
     * @param m the membership to add
     */
    @Override
    public void addMembership(OrganisationMembership m) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO organisation_memberships (userId, orgId, memberRole, active) VALUES (?, ?, ?, ?)"
            );
            stmt.setInt(1, m.getUserId());
            stmt.setInt(2, m.getOrgId());
            stmt.setString(3, m.getMemberRole().name());
            stmt.setInt(4, m.isActive() ? 1 : 0);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Finds the membership for one specific user/org pair.
     *
     * @param userId the user's ID
     * @param orgId the organisation's ID
     * @return the membership, or null if that user isn't in that org
     */
    @Override
    public OrganisationMembership getMembership(int userId, int orgId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM organisation_memberships WHERE userId = ? AND orgId = ?"
            );
            stmt.setInt(1, userId);
            stmt.setInt(2, orgId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException ex) { System.err.println(ex); }
        return null;
    }

    /**
     * Gets all the memberships for one user (every org they belong to).
     *
     * @param userId the user's ID
     * @return a list of that user's memberships
     */
    @Override
    public List<OrganisationMembership> getMembershipsForUser(int userId) {
        List<OrganisationMembership> list = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM organisation_memberships WHERE userId = ?"
            );
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ex) { System.err.println(ex); }
        return list;
    }

    /**
     * Gets all the memberships for one organisation (everyone in it).
     *
     * @param orgId the organisation's ID
     * @return a list of memberships for that org
     */
    @Override
    public List<OrganisationMembership> getMembershipsForOrg(int orgId) {
        List<OrganisationMembership> list = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM organisation_memberships WHERE orgId = ?"
            );
            stmt.setInt(1, orgId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ex) { System.err.println(ex); }
        return list;
    }

    /**
     * Updates a membership's role and active status, matched by user/org pair.
     *
     * @param m the membership with the updated info
     */
    @Override
    public void updateMembership(OrganisationMembership m) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE organisation_memberships SET memberRole = ?, active = ? WHERE userId = ? AND orgId = ?"
            );
            stmt.setString(1, m.getMemberRole().name());
            stmt.setInt(2, m.isActive() ? 1 : 0);
            stmt.setInt(3, m.getUserId());
            stmt.setInt(4, m.getOrgId());
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Deletes the membership for the given user/org pair.
     *
     * @param userId the user's ID
     * @param orgId the organisation's ID
     */
    @Override
    public void deleteMembership(int userId, int orgId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM organisation_memberships WHERE userId = ? AND orgId = ?"
            );
            stmt.setInt(1, userId);
            stmt.setInt(2, orgId);
            stmt.execute();
        } catch (SQLException ex) { System.err.println(ex); }
    }

    /**
     * Helper that builds an OrganisationMembership from a result set row. It
     * converts the role text back into the enum and the active int back into a
     * boolean, and handles a possibly-null joinedAt.
     *
     * @param rs the result set positioned on the row to read
     * @return an OrganisationMembership built from that row
     * @throws SQLException if a column can't be read
     */
    private OrganisationMembership map(ResultSet rs) throws SQLException {
        String joinedAtStr = rs.getString("joinedAt");
        LocalDateTime joinedAt = joinedAtStr == null ? null : LocalDateTime.parse(joinedAtStr, DT);
        return new OrganisationMembership(
            rs.getInt("userId"),
            rs.getInt("orgId"),
            MemberRole.valueOf(rs.getString("memberRole")),
            rs.getInt("active") == 1,
            joinedAt
        );
    }
}
