package com.example.cab302assignment.model;

import java.util.List;

public class OrganisationManager {

    private OrganisationDAO organisationDAO;
    private MembershipDAO membershipDAO;
//    private UserDAO userDAO;

    public OrganisationManager(OrganisationDAO organisationDAO, MembershipDAO membershipDAO) {
        this.organisationDAO = organisationDAO;
        this.membershipDAO = membershipDAO;
//        this.userDAO = userDAO;
    }

    // Todo: searchMember(s) exact email (for privacy)
    // Profile search bar: get userDAO.getUserById or Email => display user info
    // + membershipDAO.getMembership => display active and maybe joinedAt

    // create organisation and set creator's role as Manager
    public void createOrganisation(String name, int userId) {
        Organisation newOrg =  new Organisation(name);
        organisationDAO.addOrganisation(newOrg);

        Membership newMember = new Membership(userId, newOrg.getId(), MemberRole.MANAGER, true);
        membershipDAO.addMembership(newMember);
    }

    public void addMember(int managerId, int userId, int orgId){
        checkManagerPermission(managerId, orgId);

        // if the user is already a member but not active
        Membership user = membershipDAO.getMembership(userId, orgId);
        if (user != null){
            if (!user.isActive()) {
                membershipDAO.activateMember(userId, orgId);
            }
            return;
        }
        Membership newMember = new Membership(userId, orgId, MemberRole.MEMBER, true);
    }

    public void removeMember(int managerId, int userId, int orgId){
        checkManagerPermission(managerId, orgId);
        membershipDAO.deactivateMember(userId, orgId);
    }

    public void updateMemberRole(int managerId, int userId, int orgId, MemberRole newRole){
        checkManagerPermission(managerId, orgId);
        membershipDAO.updateMemberRole(userId, orgId, newRole);
    }

    // To be used in searchMember(s) <- getMembership (active and inactive ones)
    public Membership getMembership(int managerId, int userId, int orgId){
        checkManagerPermission(managerId, orgId);
        return membershipDAO.getMembership(userId, orgId);
    }

    public List<Membership> getAllMembers(int managerId, int orgId){
        checkManagerPermission(managerId, orgId);
        return membershipDAO.getAllMembers(orgId); // active members
    }

    public void checkManagerPermission(int managerId, int orgId) {
        Membership manager = membershipDAO.getMembership(managerId, orgId);

        if (manager == null || manager.getMemberRole() != MemberRole.MANAGER || !manager.isActive()) {
            throw new RuntimeException("User does not have manager permission!");
        }
    }
}
