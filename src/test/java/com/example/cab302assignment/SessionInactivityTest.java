package com.example.cab302assignment;

import com.example.cab302assignment.model.User;
import com.example.cab302assignment.service.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SessionInactivityTest {

    @BeforeEach
    void setUp() {
        SessionManager.logout();
        SessionManager.setInactivityTimeoutMs(SessionManager.DEFAULT_INACTIVITY_TIMEOUT_MS);
    }

    @AfterEach
    void tearDown() {
        SessionManager.logout();
        SessionManager.setInactivityTimeoutMs(SessionManager.DEFAULT_INACTIVITY_TIMEOUT_MS);
    }

    @Test
    void defaultTimeoutIs15Minutes() {
        assertEquals(15 * 60 * 1000L, SessionManager.DEFAULT_INACTIVITY_TIMEOUT_MS);
    }

    @Test
    void signInRecordsActivity() {
        SessionManager.setCurrentUser(new User("a@b.com", "A", "hash"));
        assertTrue(SessionManager.getLastActivityAt() > 0);
        assertFalse(SessionManager.isExpired());
    }

    @Test
    void recordActivityUpdatesTimestamp() throws InterruptedException {
        SessionManager.setCurrentUser(new User("a@b.com", "A", "hash"));
        long first = SessionManager.getLastActivityAt();
        Thread.sleep(5);
        SessionManager.recordActivity();
        assertTrue(SessionManager.getLastActivityAt() >= first);
    }

    @Test
    void sessionExpiresAfterInactivityWindow() throws InterruptedException {
        SessionManager.setInactivityTimeoutMs(50);
        SessionManager.setCurrentUser(new User("a@b.com", "A", "hash"));
        assertFalse(SessionManager.isExpired());
        Thread.sleep(80);
        assertTrue(SessionManager.isExpired());
    }

    @Test
    void activityResetsExpirationCountdown() throws InterruptedException {
        SessionManager.setInactivityTimeoutMs(80);
        SessionManager.setCurrentUser(new User("a@b.com", "A", "hash"));
        Thread.sleep(50);
        SessionManager.recordActivity();
        Thread.sleep(50);
        assertFalse(SessionManager.isExpired());
    }

    @Test
    void notExpiredWhenLoggedOut() throws InterruptedException {
        SessionManager.setInactivityTimeoutMs(20);
        SessionManager.setCurrentUser(new User("a@b.com", "A", "hash"));
        Thread.sleep(40);
        assertTrue(SessionManager.isExpired());
        SessionManager.logout();
        assertFalse(SessionManager.isExpired());
        assertFalse(SessionManager.isLoggedIn());
    }

    @Test
    void logoutClearsActivityTimestamp() {
        SessionManager.setCurrentUser(new User("a@b.com", "A", "hash"));
        assertTrue(SessionManager.getLastActivityAt() > 0);
        SessionManager.logout();
        assertEquals(0L, SessionManager.getLastActivityAt());
    }
}
