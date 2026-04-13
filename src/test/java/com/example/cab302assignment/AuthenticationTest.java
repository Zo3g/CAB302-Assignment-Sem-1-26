package com.example.cab302assignment;

import com.example.cab302assignment.model.MockUserDAO;
import com.example.cab302assignment.model.PasswordUtil;
import com.example.cab302assignment.model.SessionManager;
import com.example.cab302assignment.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationTest {
    private MockUserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new MockUserDAO();
        String hash = PasswordUtil.hashPassword("Password1");
        User user = new User("alice@test.com", "Alice", hash);
        userDAO.addUser(user);
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
        User user = userDAO.getUserByEmail("nobody@test.com");
        assertNull(user);
    }

    @Test
    void testSignUpThenSignIn() {
        String hash = PasswordUtil.hashPassword("NewPass123");
        User newUser = new User("bob@test.com", "Bob", hash);
        userDAO.addUser(newUser);

        User retrieved = userDAO.getUserByEmail("bob@test.com");
        assertNotNull(retrieved);
        assertTrue(PasswordUtil.verifyPassword("NewPass123", retrieved.getPasswordHash()));
    }

    @Test
    void testDuplicateEmailPrevented() {
        String hash = PasswordUtil.hashPassword("Other123");
        User duplicate = new User("alice@test.com", "Alice2", hash);
        userDAO.addUser(duplicate);
        assertEquals(2, userDAO.getAllUsers().size());
    }

    @Test
    void testSessionManagerSetAndClear() {
        User user = userDAO.getUserByEmail("alice@test.com");
        SessionManager.setCurrentUser(user);
        assertTrue(SessionManager.isLoggedIn());
        assertEquals("Alice", SessionManager.getCurrentUser().getFullName());

        SessionManager.logout();
        assertFalse(SessionManager.isLoggedIn());
        assertNull(SessionManager.getCurrentUser());
    }
}
