package com.example.cab302assignment.service;

import com.example.cab302assignment.dao.OrganisationDAO;
import com.example.cab302assignment.dao.OrganisationMembershipDAO;
import com.example.cab302assignment.dao.UserDAO;
import com.example.cab302assignment.model.MemberInfo;
import com.example.cab302assignment.model.Organisation;
import com.example.cab302assignment.model.OrganisationMembership;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.model.enums.MemberRole;
import com.example.cab302assignment.model.enums.MemberStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrganisationManager {

    private final OrganisationDAO organisationDAO;
    private final OrganisationMembershipDAO membershipDAO;
    private final UserDAO userDAO;

    public OrganisationManager(OrganisationDAO organisationDAO,
                               OrganisationMembershipDAO membershipDAO,
                               UserDAO userDAO) {
        this.organisationDAO = organisationDAO;
        this.membershipDAO = membershipDAO;
        this.userDAO = userDAO;
    }

    public MemberInfo searchMember(int managerId, int orgId, String email) {
        checkManagerPermission(managerId, orgId);

        if (email == null || email.isEmpty()) return null;

        User user = userDAO.getUserByEmail(email.toLowerCase());
        if (user == null) {
            return new MemberInfo(0, "", email, MemberStatus.NOT_FOUND);
        }

        OrganisationMembership membership = membershipDAO.getMembership(user.getUserId(), orgId);
        if (membership == null) {
            return new MemberInfo(user.getUserId(), user.getName(), user.getEmail(), MemberStatus.NOT_A_MEMBER);
        }
        if (membership.isActive()) {
            return new MemberInfo(user.getUserId(), user.getName(), user.getEmail(), MemberStatus.ACTIVE);
        } else {
            return new MemberInfo(user.getUserId(), user.getName(), user.getEmail(), MemberStatus.DEACTIVATED);
        }
    }

    public void createOrganisation(String name, int creatorUserId) {
        Organisation newOrg = new Organisation(name);
        organisationDAO.addOrganisation(newOrg);

        OrganisationMembership creator = new OrganisationMembership(
            creatorUserId, newOrg.getOrgId(), MemberRole.MANAGER, true, LocalDateTime.now()
        );
        membershipDAO.addMembership(creator);
    }

    public void addMember(int managerId, int userId, int orgId) {
        checkManagerPermission(managerId, orgId);

        OrganisationMembership existing = membershipDAO.getMembership(userId, orgId);
        if (existing != null) {
            if (!existing.isActive()) {
                existing.setActive(true);
                membershipDAO.updateMembership(existing);
            }
            return;
        }
        OrganisationMembership m = new OrganisationMembership(
            userId, orgId, MemberRole.MEMBER, true, LocalDateTime.now()
        );
        membershipDAO.addMembership(m);
    }

    public void removeMember(int managerId, int userId, int orgId) {
        checkManagerPermission(managerId, orgId);
        OrganisationMembership m = membershipDAO.getMembership(userId, orgId);
        if (m != null && m.isActive()) {
            m.setActive(false);
            membershipDAO.updateMembership(m);
        }
    }

    public void updateMemberRole(int managerId, int userId, int orgId, MemberRole newRole) {
        checkManagerPermission(managerId, orgId);
        OrganisationMembership m = membershipDAO.getMembership(userId, orgId);
        if (m != null) {
            m.setMemberRole(newRole);
            membershipDAO.updateMembership(m);
        }
    }

    public OrganisationMembership getMembership(int managerId, int userId, int orgId) {
        checkManagerPermission(managerId, orgId);
        return membershipDAO.getMembership(userId, orgId);
    }

    public List<OrganisationMembership> getAllActiveMembers(int managerId, int orgId) {
        checkManagerPermission(managerId, orgId);
        List<OrganisationMembership> out = new ArrayList<>();
        for (OrganisationMembership m : membershipDAO.getMembershipsForOrg(orgId)) {
            if (m.isActive()) out.add(m);
        }
        return out;
    }

    public void checkManagerPermission(int managerId, int orgId) {
        OrganisationMembership manager = membershipDAO.getMembership(managerId, orgId);
        if (manager == null || manager.getMemberRole() != MemberRole.MANAGER || !manager.isActive()) {
            throw new RuntimeException("User does not have manager permission!");
        }
    }
}
