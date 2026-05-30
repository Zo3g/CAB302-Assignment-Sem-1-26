package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.OrganisationMembership;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A fake (in-memory) version of {@link OrganisationMembershipDAO} for testing.
 *
 * <p>Stores the memberships in an ArrayList instead of a real database. A
 * membership is uniquely identified by the combination of its userId and
 * orgId, so we use both of those together when looking things up.</p>
 */
public class MockOrganisationMembershipDAO implements OrganisationMembershipDAO {
    /** The in-memory list standing in for the memberships table. */
    private final List<OrganisationMembership> memberships = new ArrayList<>();

    /**
     * Adds a membership, but skips it if the same user is already in the same
     * org. Sets the joined time to now if it wasn't already filled in.
     *
     * @param m the membership to add
     */
    @Override
    public void addMembership(OrganisationMembership m) {
        if (getMembership(m.getUserId(), m.getOrgId()) != null) return;
        if (m.getJoinedAt() == null) m.setJoinedAt(LocalDateTime.now());
        memberships.add(m);
    }

    /**
     * Finds the membership for a specific user/org pair.
     *
     * @param userId the user's ID
     * @param orgId the organisation's ID
     * @return the membership, or null if that user isn't in that org
     */
    @Override
    public OrganisationMembership getMembership(int userId, int orgId) {
        return memberships.stream()
            .filter(m -> m.getUserId() == userId && m.getOrgId() == orgId)
            .findFirst()
            .orElse(null);
    }

    /**
     * Collects every membership belonging to one user.
     *
     * @param userId the user's ID
     * @return a list of that user's memberships
     */
    @Override
    public List<OrganisationMembership> getMembershipsForUser(int userId) {
        List<OrganisationMembership> out = new ArrayList<>();
        for (OrganisationMembership m : memberships) if (m.getUserId() == userId) out.add(m);
        return out;
    }

    /**
     * Collects every membership belonging to one organisation.
     *
     * @param orgId the organisation's ID
     * @return a list of memberships for that org
     */
    @Override
    public List<OrganisationMembership> getMembershipsForOrg(int orgId) {
        List<OrganisationMembership> out = new ArrayList<>();
        for (OrganisationMembership m : memberships) if (m.getOrgId() == orgId) out.add(m);
        return out;
    }

    /**
     * Finds the matching membership (by user/org) and replaces it.
     *
     * @param m the membership with the updated info
     */
    @Override
    public void updateMembership(OrganisationMembership m) {
        for (int i = 0; i < memberships.size(); i++) {
            OrganisationMembership existing = memberships.get(i);
            if (existing.getUserId() == m.getUserId() && existing.getOrgId() == m.getOrgId()) {
                memberships.set(i, m);
                return;
            }
        }
    }

    /**
     * Removes the membership for the given user/org pair.
     *
     * @param userId the user's ID
     * @param orgId the organisation's ID
     */
    @Override
    public void deleteMembership(int userId, int orgId) {
        memberships.removeIf(m -> m.getUserId() == userId && m.getOrgId() == orgId);
    }
}
