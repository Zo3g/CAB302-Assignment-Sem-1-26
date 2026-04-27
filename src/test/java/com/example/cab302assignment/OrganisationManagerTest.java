package com.example.cab302assignment;

import com.example.cab302assignment.dao.MockOrganisationDAO;
import com.example.cab302assignment.dao.MockOrganisationMembershipDAO;
import com.example.cab302assignment.dao.MockUserDAO;
import com.example.cab302assignment.model.*;

import com.example.cab302assignment.model.enums.MemberRole;
import com.example.cab302assignment.model.enums.MemberStatus;
import com.example.cab302assignment.service.OrganisationManager;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

public class OrganisationManagerTest {
    private OrganisationManager organisationManager;
    private MockOrganisationDAO organisationDAO;
    private MockOrganisationMembershipDAO membershipDAO;
    private MockUserDAO userDAO;
    private User manager;
    private User user1;
    private User user2;
    private Organisation org1;
    private int currentManagerId;
    private int currentOrgId;

    @BeforeEach
    public void setup() {
        organisationDAO = new MockOrganisationDAO();
        membershipDAO = new MockOrganisationMembershipDAO();
        userDAO = new MockUserDAO();

        organisationManager = new OrganisationManager(organisationDAO, membershipDAO, userDAO);

        // Data
        manager = new User(1, "manager@test.com", "John Smith", "Tester123", LocalDateTime.now());
        user1 = new User(2, "user1@test.com", "Bob James", "Tester123", LocalDateTime.now());
        user2 = new User(3, "user2@test.com", "Alice Wood", "Tester123", LocalDateTime.now());
        org1 = new Organisation(101, "Guardia", LocalDateTime.now());

        currentManagerId = manager.getUserId();
        currentOrgId = org1.getOrgId();

        // Add to DAOs
        userDAO.addUser(manager);
        userDAO.addUser(user1);
        userDAO.addUser(user2);
        organisationDAO.addOrganisation(org1);

        // Manager membership
        membershipDAO.addMembership(new OrganisationMembership(currentManagerId, currentOrgId, MemberRole.MANAGER, true, LocalDateTime.now()));
    }

    @Test
    public void testSearchActiveMember() {
        // User1 membership - active
        membershipDAO.addMembership(new OrganisationMembership(user1.getUserId(), currentOrgId, MemberRole.MEMBER, true, LocalDateTime.now()));

        MemberInfo result = organisationManager.searchMember(currentManagerId, currentOrgId, user1.getEmail());
        assertEquals(user1.getUserId(), result.getUserId());
        assertEquals(user1.getName(), result.getName());
        assertEquals(user1.getEmail(), result.getEmail());
        assertEquals(MemberStatus.ACTIVE, result.getMemberStatus());
    }

    @Test
    public void testSearchUserNotFound() {
        MemberInfo result = organisationManager.searchMember(currentManagerId, currentOrgId, "nonexist@test.com");
        assertEquals(MemberStatus.NOT_FOUND, result.getMemberStatus());
    }

    @Test
    public void testSearchNotAMember() {
        // user2 exist but no membership
        MemberInfo result = organisationManager.searchMember(currentManagerId, currentOrgId, user2.getEmail());
        assertEquals(user2.getUserId(), result.getUserId());
        assertEquals(user2.getName(), result.getName());
        assertEquals(user2.getEmail(), result.getEmail());
        assertEquals(MemberStatus.NOT_A_MEMBER, result.getMemberStatus());
    }

    @Test
    public void testSearchMemberDeactivatedMember() {
        // User1 membership - deactivated
        membershipDAO.addMembership(new OrganisationMembership(user1.getUserId(), currentOrgId, MemberRole.MEMBER, false, LocalDateTime.now()));

        MemberInfo result = organisationManager.searchMember(currentManagerId, currentOrgId, user1.getEmail());
        assertEquals(user1.getUserId(), result.getUserId());
        assertEquals(user1.getName(), result.getName());
        assertEquals(user1.getEmail(), result.getEmail());
        assertEquals(MemberStatus.DEACTIVATED, result.getMemberStatus());
    }

    @Test
    public void testSearchEmail() {
        MemberInfo result = organisationManager.searchMember(currentManagerId, currentOrgId, "user1@test.com");
        assertEquals(user1.getEmail(), result.getEmail());
    }

    @Test
    public void testSearchEmailCaseInsensitive() {
        MemberInfo result = organisationManager.searchMember(currentManagerId, currentOrgId, "uSEr1@teSt.com");
        assertEquals(user1.getEmail(), result.getEmail());
    }

    @Test
    public void testSearchNullEmail() {
        MemberInfo result = organisationManager.searchMember(currentManagerId, currentOrgId, null);
        assertNull(result);
    }

    @Test
    public void testSearchEmptyEmail() {
        MemberInfo result = organisationManager.searchMember(currentManagerId, currentOrgId, "");
        assertNull(result);
    }

    @Test
    public void testSearchPartialEmail() {
        MemberInfo result = organisationManager.searchMember(currentManagerId, currentOrgId, "user1@te");
        assertNotNull(result);
        assertEquals(MemberStatus.NOT_FOUND, result.getMemberStatus());
    }

    @Test
    public void testAddNewMember() {
        organisationManager.addMember(currentManagerId, user1.getUserId(), currentOrgId);
        OrganisationMembership newMember = membershipDAO.getMembership(user1.getUserId(), currentOrgId);
        assertNotNull(newMember);
        assertTrue(newMember.isActive());
        assertEquals(MemberRole.MEMBER, newMember.getMemberRole());
    }

    @Test
    public void testAddDeactivatedMember() {
        membershipDAO.addMembership(new OrganisationMembership(user1.getUserId(), org1.getOrgId(), MemberRole.MEMBER, false, LocalDateTime.now()));
        organisationManager.addMember(currentManagerId, user1.getUserId(), currentOrgId);
        OrganisationMembership reactivatedMember = membershipDAO.getMembership(user1.getUserId(), currentOrgId);

        assertTrue(reactivatedMember.isActive());
    }

    @Test
    public void testRemoveMember() {
        membershipDAO.addMembership(new OrganisationMembership(user1.getUserId(), currentOrgId, MemberRole.MEMBER, true, LocalDateTime.now()));
        organisationManager.removeMember(currentManagerId, user1.getUserId(), currentOrgId);
        OrganisationMembership removedMember = membershipDAO.getMembership(user1.getUserId(), currentOrgId);
        assertFalse(removedMember.isActive()); // deactivated
    }

    @Test
    public void testRemoveNonMember() {
        organisationManager.removeMember(currentManagerId, user2.getUserId(), currentOrgId);
        OrganisationMembership removedNonMember = membershipDAO.getMembership(user2.getUserId(), currentOrgId);
        assertNull(removedNonMember);
    }

    @Test
    public void testRemoveManager() {
        assertThrows(RuntimeException.class, () -> {
            organisationManager.removeMember(currentManagerId, manager.getUserId(), currentOrgId);
        });
    }

    @Test
    public void testLeaveOrganisationNullMembership() {
        assertThrows(RuntimeException.class, () -> {
            organisationManager.leaveOrganisation(null);
        });
    }

    @Test
    public void testLeaveOrganisationDeactivatedMember() {
        OrganisationMembership deactivatedMember = new OrganisationMembership(user1.getUserId(), currentOrgId, MemberRole.MEMBER, false, LocalDateTime.now());
        membershipDAO.addMembership(deactivatedMember);
        assertThrows(RuntimeException.class, () -> {
            organisationManager.leaveOrganisation(deactivatedMember);
        });
    }

    @Test
    public void testLeaveOrganisationActiveManager() {
        OrganisationMembership activeManager = new OrganisationMembership(user1.getUserId(), currentOrgId, MemberRole.MANAGER, true, LocalDateTime.now());
        membershipDAO.addMembership(activeManager);
        assertThrows(RuntimeException.class, () -> {
            organisationManager.leaveOrganisation(activeManager);
        });
    }

    @Test
    public void testLeaveOrganisationActiveMember() {
        OrganisationMembership activeMember = new OrganisationMembership(user1.getUserId(), currentOrgId, MemberRole.MEMBER, true, LocalDateTime.now());
        membershipDAO.addMembership(activeMember);
        organisationManager.leaveOrganisation(activeMember);
        OrganisationMembership leftMember = membershipDAO.getMembership(user1.getUserId(), currentOrgId);
        assertNotNull(leftMember);
        assertFalse(leftMember.isActive()); // deactivated
    }

    @Test
    public void testUpdateMemberRole() {
        membershipDAO.addMembership(new OrganisationMembership(user1.getUserId(), currentOrgId, MemberRole.MEMBER, true, LocalDateTime.now()));
        organisationManager.updateMemberRole(currentManagerId, user1.getUserId(), currentOrgId, MemberRole.MANAGER);
        OrganisationMembership updatedMember = membershipDAO.getMembership(user1.getUserId(), currentOrgId);
        assertEquals(MemberRole.MANAGER, updatedMember.getMemberRole());
    }

    @Test
    public void testGetMembership() {
        membershipDAO.addMembership(new OrganisationMembership(user1.getUserId(), currentOrgId, MemberRole.MEMBER, true, LocalDateTime.now()));
        OrganisationMembership membership = organisationManager.getMembership(currentManagerId, user1.getUserId(), currentOrgId);
        assertNotNull(membership);
        assertEquals(user1.getUserId(), membership.getUserId());
    }

    @Test
    public void testGetAllActiveMembers() {
        membershipDAO.addMembership(new OrganisationMembership(user1.getUserId(), currentOrgId, MemberRole.MEMBER, true, LocalDateTime.now()));
        membershipDAO.addMembership(new OrganisationMembership(user2.getUserId(), currentOrgId, MemberRole.MEMBER, false, LocalDateTime.now()));
        List<OrganisationMembership> memberships = organisationManager.getAllActiveMembers(currentManagerId, currentOrgId);
        assertEquals(2, memberships.size());
    }

    @Test
    public void testManagerPermissionNotAManager() {
        membershipDAO.addMembership(new OrganisationMembership(user1.getUserId(), org1.getOrgId(), MemberRole.MEMBER, true, LocalDateTime.now()));
        assertThrows(RuntimeException.class, () -> {
            organisationManager.checkManagerPermission(user1.getUserId(), currentOrgId);
        });
    }

    @Test
    public void testManagerPermissionDeactivatedManager() {
        membershipDAO.addMembership(new OrganisationMembership(user1.getUserId(), org1.getOrgId(), MemberRole.MANAGER, false, LocalDateTime.now()));
        assertThrows(RuntimeException.class, () -> {
            organisationManager.checkManagerPermission(user1.getUserId(), currentOrgId);
        });
    }

    @Test
    public void testManagerPermissionAddMember() {
        membershipDAO.addMembership(new OrganisationMembership(user1.getUserId(), org1.getOrgId(), MemberRole.MEMBER, true, LocalDateTime.now()));

        assertThrows(RuntimeException.class, () -> {
            organisationManager.addMember(user1.getUserId(), user2.getUserId(), currentOrgId);
        });
    }
}
