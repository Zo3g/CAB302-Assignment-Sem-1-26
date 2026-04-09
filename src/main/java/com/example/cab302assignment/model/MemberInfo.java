package com.example.cab302assignment.model;

public class MemberInfo {
    private int userId;
    private String Name;
    private String Email;
    private MemberStatus memberStatus;

    public MemberInfo(int userId, String Name, String Email, MemberStatus memberStatus) {
        this.userId = userId;
        this.Name = Name;
        this.Email = Email;
        this.memberStatus = memberStatus;
    }
}
