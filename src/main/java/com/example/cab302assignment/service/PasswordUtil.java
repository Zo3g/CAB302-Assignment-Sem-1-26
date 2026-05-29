package com.example.cab302assignment.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * A utility class for securely hashing and verifying user passwords.
 * This implementation uses the SHA-256 cryptographic hash function along with
 * a randomly generated salt.
 */
public class PasswordUtil {

    /**
     * The length of the cryptographic salt in bytes.
     */
    private static final int SALT_LENGTH = 16;

    /**
     * Generates a salted cryptographic hash of a plaintext password.
     * A new, cryptographically secure random salt is generated for every password,
     * ensuring that identical plaintext passwords do not result in identical hashes.
     *
     * @param password The plaintext password to be hashed.
     * @return A Base64 encoded string containing both the salt and the hash,
     * separated by a colon (format: "salt:hash").
     * @throws RuntimeException If the SHA-256 hashing algorithm is not available on the system.
     */
    public static String hashPassword(String password) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);
            String saltBase64 = Base64.getEncoder().encodeToString(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return saltBase64 + ":" + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies a plaintext password attempt against a stored, salted hash.
     * It extracts the original salt from the stored string, hashes the attempted
     * password with that same salt, and securely compares the results.
     *
     * @param password The plaintext password attempt to verify.
     * @param stored   The stored string from the database containing the original salt
     * and expected hash (format: "salt:hash").
     * @return true if the password attempt matches the stored hash; false if it is
     * incorrect or if the stored string is malformed.
     */
    public static boolean verifyPassword(String password, String stored) {
        try {
            String[] parts = stored.split(":");
            if (parts.length != 2) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String storedHash = parts[1];

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return hashBase64.equals(storedHash);
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            return false;
        }
    }
}