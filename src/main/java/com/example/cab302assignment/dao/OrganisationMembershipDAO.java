package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.OrganisationMembership;

import java.util.List;

public interface OrganisationMembershipDAO {
    void addMembership(OrganisationMembership m);
    OrganisationMembership getMembership(int userId, int orgId);
    List<OrganisationMembership> getMembershipsForUser(int userId);
    List<OrganisationMembership> getMembershipsForOrg(int orgId);
    void updateMembership(OrganisationMembership m);
    void deleteMembership(int userId, int orgId);
}
