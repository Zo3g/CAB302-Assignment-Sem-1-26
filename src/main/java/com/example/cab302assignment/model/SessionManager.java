package com.example.cab302assignment.model;

public class SessionManager {
    private static User currentUser = null;
    private static Membership currentMembership;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentMembership(Membership membership) {
        currentMembership = membership;
    }

    public static Membership getCurrentMembership() { return currentMembership; }

    public static int getCurrentOrgId() {
        return currentMembership.getOrganisationId();
    }

    public static boolean isLoggedIn() { return currentUser != null; }

    public static void logout() {
        currentUser = null;
        currentMembership = null;
    }
}
