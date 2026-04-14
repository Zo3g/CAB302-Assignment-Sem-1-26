package com.example.cab302assignment;

import com.example.cab302assignment.dao.MockUserDAO;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.service.PasswordUtil;
import com.example.cab302assignment.service.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationTest {
    private MockUserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new MockUserDAO();
        String hash = PasswordUtil.hashPassword("Password1");
        userDAO.addUser(new User("alice@test.com", "Alice", hash));
    }

    @Test
    void testSuccessfulAuthentication() {
        User user = userDAO.getUserByEmail("alice@test.com");
        assertNotNull(user);
        assertTrue(PasswordUtil.verifyPassword("Password1", user.getPasswordHash()));
    }

    @Test
    void testWrongPassword() {
        User user = userDAO.getUserByEmail("alice@test.com");
        assertNotNull(user);
        assertFalse(PasswordUtil.verifyPassword("WrongPassword1", user.getPasswordHash()));
    }

    @Test
    void testNonexistentUser() {
        assertNull(userDAO.getUserByEmail("nobody@test.com"));
    }

    @Test
    void testSignUpThenSignIn() {
        String hash = PasswordUtil.hashPassword("NewPass123");
        userDAO.addUser(new User("bob@test.com", "Bob", hash));

        User retrieved = userDAO.getUserByEmail("bob@test.com");
        assertNotNull(retrieved);
        assertTrue(PasswordUtil.verifyPassword("NewPass123", retrieved.getPasswordHash()));
    }

    @Test
    void testDuplicateEmailPrevented() {
        String hash = PasswordUtil.hashPassword("Other123");
        userDAO.addUser(new User("alice@test.com", "Alice", hash));
        assertEquals(1, userDAO.getAllUsers().size());
    }

    @Test
    void testSessionManagerSetAndClear() {
        User user = userDAO.getUserByEmail("alice@test.com");
        SessionManager.setCurrentUser(user);
        assertTrue(SessionManager.isLoggedIn());
        assertEquals("alice@test.com", SessionManager.getCurrentUser().getEmail());

        SessionManager.logout();
        assertFalse(SessionManager.isLoggedIn());
        assertNull(SessionManager.getCurrentUser());
    }
}
