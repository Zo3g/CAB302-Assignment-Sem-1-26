package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.OrganisationMembership;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockOrganisationMembershipDAO implements OrganisationMembershipDAO {
    private final List<OrganisationMembership> memberships = new ArrayList<>();

    @Override
    public void addMembership(OrganisationMembership m) {
        if (getMembership(m.getUserId(), m.getOrgId()) != null) return;
        if (m.getJoinedAt() == null) m.setJoinedAt(LocalDateTime.now());
        memberships.add(m);
    }

    @Override
    public OrganisationMembership getMembership(int userId, int orgId) {
        return memberships.stream()
            .filter(m -> m.getUserId() == userId && m.getOrgId() == orgId)
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<OrganisationMembership> getMembershipsForUser(int userId) {
        List<OrganisationMembership> out = new ArrayList<>();
        for (OrganisationMembership m : memberships) if (m.getUserId() == userId) out.add(m);
        return out;
    }

    @Override
    public List<OrganisationMembership> getMembershipsForOrg(int orgId) {
        List<OrganisationMembership> out = new ArrayList<>();
        for (OrganisationMembership m : memberships) if (m.getOrgId() == orgId) out.add(m);
        return out;
    }

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

    @Override
    public void deleteMembership(int userId, int orgId) {
        memberships.removeIf(m -> m.getUserId() == userId && m.getOrgId() == orgId);
    }
}
