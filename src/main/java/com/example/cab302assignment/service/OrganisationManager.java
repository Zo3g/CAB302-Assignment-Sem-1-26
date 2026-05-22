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

/**
 * Service class responsible for organisation membership management logic.
 *
 * <p>This class handles organisation membership management,
 * member searching, permission validation, and role-related operations.</p>
 */
public class OrganisationManager {

    /** DAO for organisation persistence operations */
    private final OrganisationDAO organisationDAO;

    /** DAO for organisation membership persistence operations */
    private final OrganisationMembershipDAO membershipDAO;

    /** DAO for user persistence operations */
    private final UserDAO userDAO;

    /**
     * Constructs an OrganisationManager with required DAO dependencies.
     *
     * @param organisationDAO DAO for organisation operations
     * @param membershipDAO DAO for membership operations
     * @param userDAO DAO for user operations
     */
    public OrganisationManager(OrganisationDAO organisationDAO,
                               OrganisationMembershipDAO membershipDAO,
                               UserDAO userDAO) {
        this.organisationDAO = organisationDAO;
        this.membershipDAO = membershipDAO;
        this.userDAO = userDAO;
    }

    /**
     * Searches for a user by email and returns their member status
     * based on whether they have a membership in the organisation.
     *
     * <p>The status indicates whether the user was not found, is not a member,
     * is an active member, or is a deactivated member.</p>
     *
     * @param managerId the manager performing the search
     * @param orgId the organisation ID
     * @param email the email address to search
     * @return the searched member information, or null if email is blank
     * @throws RuntimeException if the user does not have manager permission
     */
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

    /**
     * Creates a new organisation and assigns the creator
     * as the organisation manager.
     *
     * @param name the organisation name
     * @param creatorUserId the creator's user ID
     */
    public void createOrganisation(String name, int creatorUserId) {
        Organisation newOrg = new Organisation(name);
        organisationDAO.addOrganisation(newOrg);

        OrganisationMembership creator = new OrganisationMembership(
            creatorUserId, newOrg.getOrgId(), MemberRole.MANAGER, true, LocalDateTime.now()
        );
        membershipDAO.addMembership(creator);
    }

    /**
     * Adds or reactivates a member in the organisation.
     *
     * @param managerId the manager performing the action
     * @param userId the target user ID
     * @param orgId the organisation ID
     * @throws RuntimeException if the user does not have manager permission
     */
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

    /**
     * Deactivates a member's organisation membership.
     *
     * @param managerId the manager performing the action
     * @param userId the target user ID
     * @param orgId the organisation ID
     * @throws RuntimeException if attempting self-removal or lacking permission
     */
    public void removeMember(int managerId, int userId, int orgId) {
        if (managerId == userId) {
            throw new RuntimeException("Manager cannot remove their own membership.");
        }
        checkManagerPermission(managerId, orgId);
        OrganisationMembership m = membershipDAO.getMembership(userId, orgId);
        if (m != null && m.isActive()) {
            m.setActive(false);
            membershipDAO.updateMembership(m);
        }
    }

    /**
     * Allows a member to leave an organisation.
     *
     * <p>Only active non-manager members are allowed
     * to leave the organisation.</p>
     *
     * @param m the organisation membership to deactivate
     * @throws RuntimeException if membership is invalid or belongs to a manager
     */
    public void leaveOrganisation(OrganisationMembership m) {
        if (m == null || !m.isActive()) {
            throw new RuntimeException("User is not an active member of this organisation.");
        }
        if (m.getMemberRole() == MemberRole.MANAGER) {
            throw new RuntimeException("Manager cannot leave organisation.");
        }
        m.setActive(false);
        membershipDAO.updateMembership(m);
    }

    /**
     * Updates a member's role within the organisation.
     *
     * @param managerId the manager performing the update
     * @param userId the target user ID
     * @param orgId the organisation ID
     * @param newRole the new member role
     * @throws RuntimeException if the user does not have manager permission
     */
    public void updateMemberRole(int managerId, int userId, int orgId, MemberRole newRole) {
        checkManagerPermission(managerId, orgId);
        OrganisationMembership m = membershipDAO.getMembership(userId, orgId);
        if (m != null) {
            m.setMemberRole(newRole);
            membershipDAO.updateMembership(m);
        }
    }

    /**
     * Gets a user's organisation membership.
     *
     * @param managerId the manager requesting the membership
     * @param userId the target user ID
     * @param orgId the organisation ID
     * @return the organisation membership
     * @throws RuntimeException if the user does not have manager permission
     */
    public OrganisationMembership getMembership(int managerId, int userId, int orgId) {
        checkManagerPermission(managerId, orgId);
        return membershipDAO.getMembership(userId, orgId);
    }

    /**
     * Gets all active members within an organisation.
     *
     * @param managerId the manager requesting the list
     * @param orgId the organisation ID
     * @return a list of active organisation memberships
     * @throws RuntimeException if the user does not have manager permission
     */
    public List<OrganisationMembership> getAllActiveMembers(int managerId, int orgId) {
        checkManagerPermission(managerId, orgId);
        List<OrganisationMembership> out = new ArrayList<>();
        for (OrganisationMembership m : membershipDAO.getMembershipsForOrg(orgId)) {
            if (m.isActive()) out.add(m);
        }
        return out;
    }

    /**
     * Validates whether a user has active manager permission
     * within the specified organisation.
     *
     * @param managerId the user ID to validate
     * @param orgId the organisation ID
     * @throws RuntimeException if the user is not an active manager
     */
    public void checkManagerPermission(int managerId, int orgId) {
        OrganisationMembership manager = membershipDAO.getMembership(managerId, orgId);
        if (manager == null || manager.getMemberRole() != MemberRole.MANAGER || !manager.isActive()) {
            throw new RuntimeException("User does not have manager permission!");
        }
    }
}
