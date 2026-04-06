package com.example.cab302assignment.model;

import java.util.List;

public interface OrganisationMembershipDAO {
    public void addMembership(OrganisationMembership orgMembership);
    public void deactivateMember(int userId, int orgId);
    public void activateMember(int userId, int orgId);
    public void updateMemberRole(int userId, int orgId, MemberRole memberRole);
    public OrganisationMembership getMembership(int userId, int orgId); // individual user
    public List<OrganisationMembership> getAllMembers(int orgId);

//    public void removeMembership(int userId, int orgId); // remove or just deactivateMember??
//    public void updateMembership(int userId, int orgId, MemberRole memberRole); // memberRole & active!?
}