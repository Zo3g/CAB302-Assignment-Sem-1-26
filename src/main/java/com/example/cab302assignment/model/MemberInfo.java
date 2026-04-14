package com.example.cab302assignment.model;

import com.example.cab302assignment.model.enums.MemberStatus;

public class MemberInfo {
    private int userId;
    private String email;
    private MemberStatus memberStatus;

    public MemberInfo(int userId, String email, MemberStatus memberStatus) {
        this.userId = userId;
        this.email = email;
        this.memberStatus = memberStatus;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public MemberStatus getMemberStatus() { return memberStatus; }
    public void setMemberStatus(MemberStatus memberStatus) { this.memberStatus = memberStatus; }
}
