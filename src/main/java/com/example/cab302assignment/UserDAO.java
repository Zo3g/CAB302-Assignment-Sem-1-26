package com.example.cab302assignment;

import java.util.List;

public interface UserDAO {
    void addUser(User user);
    User getUserById(int id);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(int id);
}
