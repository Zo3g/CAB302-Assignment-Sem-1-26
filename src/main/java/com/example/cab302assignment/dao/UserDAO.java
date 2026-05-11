package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.MemberRiskSummary;
import com.example.cab302assignment.model.User;

import java.util.List;

public interface UserDAO {
    void addUser(User user);
    User getUserById(int id);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(int id);
    List<MemberRiskSummary> getMemberRiskSummary(int orgId);
}
