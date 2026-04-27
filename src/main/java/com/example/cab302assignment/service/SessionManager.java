package com.example.cab302assignment.service;

import com.example.cab302assignment.model.User;

public class SessionManager {
    public static final long DEFAULT_INACTIVITY_TIMEOUT_MS = 1 * 60 * 1000L;

    private static User currentUser = null;
    private static int currentUserId = 0;
    private static int currentOrgId = 0;
    private static long lastActivityAt = 0L;
    private static long inactivityTimeoutMs = DEFAULT_INACTIVITY_TIMEOUT_MS;
    private static boolean timedOut = false;

    public static void setCurrentUser(User user) {
        currentUser = user;
        if (user != null) {
            recordActivity();
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUserId(int userId) {currentUserId = userId;}

    public static int getCurrentUserId() {return currentUserId;}

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void setCurrentOrgId(int orgId) {
        currentOrgId = orgId;
    }

    public static int getCurrentOrgId() { return currentOrgId; }

    public static void logout() {
        currentUser = null;
        currentUserId = 0;
        currentOrgId = 0;
        lastActivityAt = 0L;
    }

    public static void recordActivity() {
        lastActivityAt = System.currentTimeMillis();
    }

    public static long getLastActivityAt() {
        return lastActivityAt;
    }

    public static long getInactivityTimeoutMs() {
        return inactivityTimeoutMs;
    }

    public static void setInactivityTimeoutMs(long timeoutMs) {
        inactivityTimeoutMs = timeoutMs;
    }

    public static void setTimedOut(boolean value) {
        timedOut = value;
    }

    public static boolean consumeTimedOut() {
        boolean wasTimedOut = timedOut;
        timedOut = false;
        return wasTimedOut;
    }

    public static boolean isExpired() {
        if (!isLoggedIn() || lastActivityAt == 0L) {
            return false;
        }
        return System.currentTimeMillis() - lastActivityAt >= inactivityTimeoutMs;
    }
}
