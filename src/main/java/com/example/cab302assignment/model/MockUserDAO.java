package com.example.cab302assignment.model;

import java.util.ArrayList;
import java.util.List;

public class MockUserDAO implements UserDAO {
    private final List<User> users = new ArrayList<>();
    private int nextId = 1;

    @Override
    public void addUser(User user) {
        user.setId(nextId++);
        users.add(user);
    }

    @Override
    public User getUserById(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                return;
            }
        }
    }

    @Override
    public void deleteUser(int id) {
        users.removeIf(user -> user.getId() == id);
    }
}
