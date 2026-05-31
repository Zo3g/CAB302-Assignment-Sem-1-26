package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.OrganisationMembership;

import java.util.List;

/**
 * DAO interface for organisation memberships.
 *
 * <p>A membership is basically the link between a user and an organisation
 * (who's in what org and what their role is). This interface lists the
 * database operations we need to manage those links.</p>
 */
public interface OrganisationMembershipDAO {
    /**
     * Adds a new membership (puts a user into an org).
     *
     * @param m the membership to add
     */
    void addMembership(OrganisationMembership m);

    /**
     * Gets one specific membership for a user in a particular org.
     *
     * @param userId the user's ID
     * @param orgId the organisation's ID
     * @return the membership, or null if that user isn't in that org
     */
    OrganisationMembership getMembership(int userId, int orgId);

    /**
     * Gets all the memberships belonging to one user (all the orgs they're in).
     *
     * @param userId the user's ID
     * @return a list of that user's memberships
     */
    List<OrganisationMembership> getMembershipsForUser(int userId);

    /**
     * Gets all the memberships for one organisation (everyone in that org).
     *
     * @param orgId the organisation's ID
     * @return a list of memberships for that org
     */
    List<OrganisationMembership> getMembershipsForOrg(int orgId);

    /**
     * Updates an existing membership, e.g. changing a role or active status.
     *
     * @param m the membership with the updated info
     */
    void updateMembership(OrganisationMembership m);

    /**
     * Deletes a membership (removes a user from an org).
     *
     * @param userId the user's ID
     * @param orgId the organisation's ID
     */
    void deleteMembership(int userId, int orgId);
}
