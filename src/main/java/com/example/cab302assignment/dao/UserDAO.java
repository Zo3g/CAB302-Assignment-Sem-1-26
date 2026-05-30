package com.example.cab302assignment.dao;
import com.example.cab302assignment.model.User;

import java.util.List;

/**
 * This is the DAO (Data Access Object) interface for users.
 *
 * <p>Basically it lists all the things we need to be able to do with users in
 * the database, like adding them, looking them up and deleting them. We made it
 * an interface so we can have different versions behind it - a real SQLite one
 * for the actual app and a mock one for testing.</p>
 */
public interface UserDAO {
    /**
     * Adds a new user to the database.
     *
     * @param user the user we want to save
     */
    void addUser(User user);

    /**
     * Finds a single user by their ID.
     *
     * @param id the user's ID
     * @return the matching user, or null if there isn't one
     */
    User getUserById(int id);

    /**
     * Finds a user by their email address (handy for logging in).
     *
     * @param email the email to search for
     * @return the matching user, or null if no one uses that email
     */
    User getUserByEmail(String email);

    /**
     * Grabs every user in the database.
     *
     * @return a list of all users
     */
    List<User> getAllUsers();

    /**
     * Updates an existing user's details.
     *
     * @param user the user with the new info (matched by ID)
     */
    void updateUser(User user);

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to remove
     */
    void deleteUser(int id);
}
