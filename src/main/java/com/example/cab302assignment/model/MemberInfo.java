package com.example.cab302assignment.model;

public class MemberInfo {
    private int userId;
    private String name;
    private String email;
    private MemberStatus memberStatus;

    public MemberInfo(int userId, String name, String email, MemberStatus memberStatus) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.memberStatus = memberStatus;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public MemberStatus getMemberStatus() { return memberStatus; }
    public void setMemberStatus(MemberStatus memberStatus) { this.memberStatus = memberStatus; }
}
