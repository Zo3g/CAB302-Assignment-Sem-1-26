package com.example.cab302assignment.model;

import java.util.List;

public class OrganisationManager {

    private OrganisationDAO organisationDAO;
    private MembershipDAO membershipDAO;
    private UserDAO userDAO;

    public OrganisationManager(OrganisationDAO organisationDAO, MembershipDAO membershipDAO, UserDAO userDAO) {
        this.organisationDAO = organisationDAO;
        this.membershipDAO = membershipDAO;
        this.userDAO = userDAO;
    }

    // searchMember (exact email for privacy)
    // Profile searchField: get userDAO.getUserByEmail + membershipDAO.getMembership
    // => display mapped memberInfo => determine management buttons and checkBox
    public MemberInfo searchMember(int managerId, int orgId, String email) {
        checkManagerPermission(managerId, orgId);

        if (email == null || email.isEmpty()) return null;

        User user = userDAO.getUserByEmail(email);
        // User not exists => error message
        if (user == null) {
            return new MemberInfo(0, "", email, MemberStatus.NOT_FOUND);
        }

        Membership membership = membershipDAO.getMembership(user.getId(), orgId);
        // User not a member yet => Add user button enable
        if (membership == null) {
            return new MemberInfo(user.getId(), user.getName(), user.getEmail(), MemberStatus.NOT_A_MEMBER);
        }
        // User is an active member => Remove user button enable
        if (membership.isActive()) {
            return new MemberInfo(user.getId(), user.getName(), user.getEmail(), MemberStatus.ACTIVE);
        }
        // deactivated user? Manager active?
        reture null;
    }

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
        membershipDAO.addMembership(newMember);
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
