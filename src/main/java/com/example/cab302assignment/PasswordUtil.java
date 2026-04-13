package com.example.cab302assignment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    private static final int SALT_LENGTH = 16;

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
