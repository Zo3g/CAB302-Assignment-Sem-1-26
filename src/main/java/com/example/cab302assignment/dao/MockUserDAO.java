package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.MemberRiskSummary;
import com.example.cab302assignment.model.User;

import java.util.ArrayList;
import java.util.List;

public class MockUserDAO implements UserDAO {
    private final List<User> users = new ArrayList<>();
    private int nextId = 1;

    @Override
    public void addUser(User user) {
        for (User existing : users) {
            if (existing.getEmail().equals(user.getEmail())) return;
        }
        user.setUserId(nextId++);
        users.add(user);
    }

    @Override
    public User getUserById(int id) {
        for (User user : users) if (user.getUserId() == id) return user;
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        for (User user : users) if (user.getEmail().equals(email)) return user;
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId() == user.getUserId()) {
                users.set(i, user);
                return;
            }
        }
    }

    @Override
    public void deleteUser(int id) {
        users.removeIf(user -> user.getUserId() == id);
    }

    @Override
    public List<MemberRiskSummary> getMemberRiskSummary(int orgId) {
        List<MemberRiskSummary> mockSummaries = new ArrayList<>();

        // Return dummy data so the UI still populates during mock testing
        mockSummaries.add(new MemberRiskSummary(1, "Mock Alice", 12.5));
        mockSummaries.add(new MemberRiskSummary(2, "Mock Bob", 0.0));

        return mockSummaries;
    }
}
