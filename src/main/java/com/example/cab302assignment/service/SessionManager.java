package com.example.cab302assignment.service;

import com.example.cab302assignment.model.User;

/**
 * Global session state manager for the application.
 *
 * Stores and manages information about the currently logged-in user,
 * their active organisation, and session activity tracking.
 *
 * Also handles:
 * - Login/logout state
 * - Organisation context
 * - Inactivity tracking and timeout detection
 * - Session expiry flags
 *
 * This class is designed as a static singleton-style utility.
 */
public class SessionManager {
    /**
     * Default session inactivity timeout (15 minutes).
     */
    public static final long DEFAULT_INACTIVITY_TIMEOUT_MS = 15 * 60 * 1000L;

    private static User currentUser = null;
    private static int currentUserId = 0;
    private static int currentOrgId = 0;
    private static String currentOrgName = "";
    private static long lastActivityAt = 0L;
    private static long inactivityTimeoutMs = DEFAULT_INACTIVITY_TIMEOUT_MS;
    private static boolean timedOut = false;

    /**
     * Sets the currently logged-in user and records session activity.
     *
     * If a non-null user is provided, the session activity timer is reset.
     *
     * @param user authenticated user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
        if (user != null) {
            recordActivity();
        }
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return current User, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the ID of the currently logged-in user.
     *
     * @param userId user identifier
     */
    public static void setCurrentUserId(int userId) {currentUserId = userId;}

    /**
     * Returns the ID of the currently logged-in user.
     *
     * @return user ID
     */
    public static int getCurrentUserId() {return currentUserId;}

    /**
     * Checks whether a user is currently logged into the session.
     *
     * @return true if a user session exists, false otherwise
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Sets the currently active organisation ID for the session.
     *
     * @param orgId organisation identifier
     */
    public static void setCurrentOrgId(int orgId) {
        currentOrgId = orgId;
    }

    /**
     * Returns the currently active organisation ID.
     *
     * @return organisation ID
     */
    public static int getCurrentOrgId() { return currentOrgId; }

    /**
     * Sets the name of the currently active organisation.
     *
     * @param orgName organisation name
     */
    public static void setCurrentOrgName(String orgName) {
        currentOrgName = orgName;
    }

    /**
     * Returns the name of the currently active organisation.
     *
     * @return organisation name
     */
    public static String getCurrentOrgName() { return currentOrgName; }

    /**
     * Logs out the current user and clears session state.
     *
     * Resets:
     * - Current user
     * - User ID
     * - Organisation ID
     * - Last activity timestamp
     */
    public static void logout() {
        currentUser = null;
        currentUserId = 0;
        currentOrgId = 0;
        lastActivityAt = 0L;
    }

    /**
     * Records user activity by updating the last activity timestamp.
     *
     * Used for inactivity timeout tracking.
     */
    public static void recordActivity() {
        lastActivityAt = System.currentTimeMillis();
    }

    /**
     * Returns the timestamp of the last recorded user activity.
     *
     * @return last activity time in milliseconds since epoch
     */
    public static long getLastActivityAt() {
        return lastActivityAt;
    }

    /**
     * Returns the configured inactivity timeout duration.
     *
     * @return timeout in milliseconds
     */
    public static long getInactivityTimeoutMs() {
        return inactivityTimeoutMs;
    }

    /**
     * Updates the inactivity timeout duration.
     *
     * @param timeoutMs timeout in milliseconds
     */
    public static void setInactivityTimeoutMs(long timeoutMs) {
        inactivityTimeoutMs = timeoutMs;
    }

    /**
     * Sets whether the session has timed out due to inactivity.
     *
     * @param value true if session timed out, false otherwise
     */
    public static void setTimedOut(boolean value) {
        timedOut = value;
    }

    /**
     * Checks and consumes the session timeout flag.
     *
     * If the session was previously marked as timed out,
     * this method returns true and resets the flag.
     *
     * @return true if the session had timed out, false otherwise
     */
    public static boolean consumeTimedOut() {
        boolean wasTimedOut = timedOut;
        timedOut = false;
        return wasTimedOut;
    }

    /**
     * Determines whether the current session has expired due to inactivity.
     *
     * A session is considered expired if:
     * - A user is logged in
     * - Activity has been recorded
     * - Inactivity duration exceeds the configured timeout
     *
     * @return true if session is expired, false otherwise
     */
    public static boolean isExpired() {
        if (!isLoggedIn() || lastActivityAt == 0L) {
            return false;
        }
        return System.currentTimeMillis() - lastActivityAt >= inactivityTimeoutMs;
    }
}
