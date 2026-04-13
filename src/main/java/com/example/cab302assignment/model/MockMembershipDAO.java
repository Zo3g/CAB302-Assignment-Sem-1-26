package com.example.cab302assignment.model;

import java.util.ArrayList;
import java.util.List;

public class MockMembershipDAO implements MembershipDAO {
    private List<Membership> memberships =  new ArrayList<>();

    public MockMembershipDAO() {
        memberships.add(new Membership(1, 101, MemberRole.MANAGER, true));
        memberships.add(new Membership(2, 101, MemberRole.MEMBER, true));
    }

    @Override
    public void addMembership(Membership membership) {
        memberships.add(membership);
    }

    @Override
    public void deactivateMember(int userId, int orgId) {
        Membership membership = getMembership(userId, orgId);
        if (membership != null){
            membership.setActive(false);
        }
    }

    @Override
    public void activateMember(int userId, int orgId) {
        Membership membership = getMembership(userId, orgId);
        if (membership != null){
            membership.setActive(true);
        }
    }

    @Override
    public void updateMemberRole(int userId, int orgId, MemberRole memberRole) {
        Membership membership = getMembership(userId, orgId);
        if (membership != null){
            membership.setMemberRole(memberRole);
        }
    }

    @Override
    public Membership getMembership(int userId, int orgId) {
        return memberships.stream()
            .filter(member -> member.getUserId() == userId && member.getOrganisationId() == orgId)
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<Membership> getAllMembers(int orgId) {
        return memberships.stream()
            .filter(member -> member.getOrganisationId() == orgId)
            .toList();
    }
}
