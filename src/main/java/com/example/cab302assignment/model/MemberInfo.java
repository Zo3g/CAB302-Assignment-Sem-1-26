package com.example.cab302assignment.model;

import com.example.cab302assignment.model.enums.MemberStatus;

/**
 * A simple data transfer object (DTO) that represents a member's basic information
 * within an organisation.
 *
 * <p>This class is typically used to pass member data between the data layer,
 * business logic, and UI components. It contains identifying and display
 * information such as user ID, name, email, and current membership status.</p>
 */
public class MemberInfo {

    /** Unique identifier of the user */
    private int userId;
    /** Display name of the user */
    private String name;
    /** Email address of the user */
    private String email;
    /** Current membership status of the user */
    private MemberStatus memberStatus;

    /**
     * Constructs a MemberInfo object with all required fields.
     *
     * @param userId the unique ID of the user
     * @param name the name of the user
     * @param email the email address of the user
     * @param memberStatus the membership status of the user
     */
    public MemberInfo(int userId, String name, String email, MemberStatus memberStatus) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.memberStatus = memberStatus;
    }

    /**
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
     * @return the user's name
     */
    public String getName() { return name; }

    /**
     * Sets the user's name.
     *
     * @param name the name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * @return the user's email
     */
    public String getEmail() { return email; }

    /**
     * Sets the user's email.
     *
     * @param email the email to set
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * @return the member's current status
     */
    public MemberStatus getMemberStatus() { return memberStatus; }

    /**
     * Sets the member's status.
     *
     * @param memberStatus the status to set
     */
    public void setMemberStatus(MemberStatus memberStatus) { this.memberStatus = memberStatus; }
}
