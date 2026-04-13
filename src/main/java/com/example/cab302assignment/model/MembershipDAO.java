package com.example.cab302assignment.model;

import java.util.List;

public interface MembershipDAO {
    public void addMembership(Membership membership);
    public void deactivateMember(int userId, int orgId);
    public void activateMember(int userId, int orgId);
    public void updateMemberRole(int userId, int orgId, MemberRole memberRole);
    public Membership getMembership(int userId, int orgId); // individual user
    public List<Membership> getAllMembers(int orgId);

//    public void removeMembership(int userId, int orgId); // remove or just deactivateMember??
//    public void updateMembership(int userId, int orgId, MemberRole memberRole); // memberRole & active!?
}