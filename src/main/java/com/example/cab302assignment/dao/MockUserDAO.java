package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A fake (in-memory) version of {@link UserDAO} used for testing.
 *
 * <p>Instead of talking to a real database this just keeps the users in an
 * ArrayList. That way our tests can run quickly and don't need an actual
 * SQLite file set up. It hands out IDs itself using a simple counter.</p>
 */
public class MockUserDAO implements UserDAO {
    /** The in-memory list that stands in for the users table. */
    private final List<User> users = new ArrayList<>();

    /** The next ID to hand out - goes up by one each time we add a user. */
    private int nextId = 1;

    /**
     * Adds a user to the list, but only if their email isn't already taken.
     * Also gives them a fresh ID before storing them.
     *
     * @param user the user to add
     */
    @Override
    public void addUser(User user) {
        for (User existing : users) {
            if (existing.getEmail().equals(user.getEmail())) return;
        }
        user.setUserId(nextId++);
        users.add(user);
    }

    /**
     * Looks through the list for a user with the given ID.
     *
     * @param id the user's ID
     * @return the matching user, or null if there isn't one
     */
    @Override
    public User getUserById(int id) {
        for (User user : users) if (user.getUserId() == id) return user;
        return null;
    }

    /**
     * Looks through the list for a user with the given email.
     *
     * @param email the email to match
     * @return the matching user, or null if none found
     */
    @Override
    public User getUserByEmail(String email) {
        for (User user : users) if (user.getEmail().equals(email)) return user;
        return null;
    }

    /**
     * Returns a copy of all the users (a copy so callers can't mess with our
     * internal list).
     *
     * @return a list of all users
     */
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Finds the user with the same ID and swaps them out for the updated one.
     *
     * @param user the user with the new details
     */
    @Override
    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId() == user.getUserId()) {
                users.set(i, user);
                return;
            }
        }
    }

    /**
     * Removes the user with the given ID from the list.
     *
     * @param id the ID of the user to delete
     */
    @Override
    public void deleteUser(int id) {
        users.removeIf(user -> user.getUserId() == id);
    }
}
