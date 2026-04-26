package com.example.cab302assignment.service;

import com.example.cab302assignment.model.User;

public class SessionManager {
    private static User currentUser = null;
    private static int currentUserId = 0;
    private static int currentOrgId = 0;

    public static void setCurrentUser(User user) {
        currentUser = user;
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
        currentOrgId = 0;
    }
}
