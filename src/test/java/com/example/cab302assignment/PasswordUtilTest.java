package com.example.cab302assignment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test
    void testHashAndVerifyCorrectPassword() {
        String password = "SecurePass123!";
        String hashed = PasswordUtil.hashPassword(password);
        assertTrue(PasswordUtil.verifyPassword(password, hashed));
    }

    @Test
    void testVerifyWrongPassword() {
        String hashed = PasswordUtil.hashPassword("CorrectPassword");
        assertFalse(PasswordUtil.verifyPassword("WrongPassword", hashed));
    }

    @Test
    void testHashProducesDifferentSalts() {
        String password = "SamePassword";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);
        assertNotEquals(hash1, hash2);
    }

    @Test
    void testBothHashesStillVerify() {
        String password = "SamePassword";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);
        assertTrue(PasswordUtil.verifyPassword(password, hash1));
        assertTrue(PasswordUtil.verifyPassword(password, hash2));
    }

    @Test
    void testVerifyWithMalformedStoredHash() {
        assertFalse(PasswordUtil.verifyPassword("password", "notavalidhash"));
    }

    @Test
    void testHashContainsSaltSeparator() {
        String hashed = PasswordUtil.hashPassword("test");
        assertTrue(hashed.contains(":"));
        assertEquals(2, hashed.split(":").length);
    }
}
