package com.example.cab302assignment.model;

import com.example.cab302assignment.service.PasswordUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Represents a system user.
 *
 * <p>A user contains authentication information, profile details,
 * organisation memberships, and prompt-related activity data.</p>
 */
public class User {
    /** Unique identifier of the user */
    private int userId;

    /** User email address */
    private String email;

    /** User display name */
    private String name;

    /** Securely hashed user password */
    private String passwordHash;

    /** Timestamp recording when the account was created */
    private LocalDateTime createdAt;

    /**
     * Constructs an empty User object.
     */
    public User() {
    }

    /**
     * Constructs a User with required registration information.
     *
     * @param email the user's email address
     * @param name the user's display name
     * @param passwordHash the hashed password
     */
    public User(String email, String name, String passwordHash) {
        this.email = email;
        this.name = name;
        this.passwordHash = passwordHash;
    }

    /**
     * Constructs a fully initialized User object.
     *
     * @param userId the user ID
     * @param email the user's email address
     * @param name the user's display name
     * @param passwordHash the hashed password
     * @param createdAt the account creation timestamp
     */
    public User(int userId, String email, String name, String passwordHash, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public int getUserId() { return userId; }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID to set
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Gets the user's email address.
     *
     * @return the email address
     */
    public String getEmail() { return email; }

    /**
     * Sets the user's email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Gets the user's display name.
     *
     * @return the user's name
     */
    public String getName() { return name; }

    /**
     * Sets the user's display name.
     *
     * @param name the name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the user's hashed password.
     *
     * @return the password hash
     */
    public String getPasswordHash() { return passwordHash; }

    /**
     * Sets the user's hashed password.
     *
     * @param passwordHash the password hash to set
     */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /**
     * Gets the account creation timestamp.
     *
     * @return the account creation timestamp
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Sets the account creation timestamp.
     *
     * @param createdAt the timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Authenticates the user using the provided password.
     *
     * @param password the plain text password to verify
     * @return true if the password matches the stored hash, otherwise false
     */
    public boolean authenticate(String password) {
        return PasswordUtil.verifyPassword(password, this.passwordHash);
    }

    /**
     * Gets the user's prompt history.
     *
     * @return a list of prompts associated with the user
     */
    public List<Prompt> getPromptHistory() {
        return Collections.emptyList();
    }

    /**
     * Removes the user from their organisation membership.
     */
    public void leaveOrganisation() {
    }
}
