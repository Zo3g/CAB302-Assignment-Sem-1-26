package com.example.cab302assignment;

import com.example.cab302assignment.controller.SignUpController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignUpValidationTest {
    @Test
    void testValidInput() {
        assertNull(SignUpController.validate("Alice","alice@test.com", "Password1", "Password1"));
    }

    @Test
    void testEmptyName() {
        assertEquals("Name is required.", SignUpController.validate("", "a@b.com", "Password1", "Password1"));
    }

    @Test
    void testEmptyEmail() {
        assertEquals("Email address is required.", SignUpController.validate("Alice","", "Password1", "Password1"));
    }

    @Test
    void testInvalidEmail() {
        assertEquals("Please enter a valid email address.", SignUpController.validate("Alice","notanemail", "Password1", "Password1"));
    }

    @Test
    void testEmailMissingDomain() {
        assertEquals("Please enter a valid email address.", SignUpController.validate("Alice","alice@", "Password1", "Password1"));
    }

    @Test
    void testPasswordTooShort() {
        assertEquals("Password must be at least 8 characters.", SignUpController.validate("Alice","a@b.com", "Pass1", "Pass1"));
    }

    @Test
    void testPasswordNoUppercase() {
        assertEquals("Password must contain at least one uppercase letter.", SignUpController.validate("Alice","a@b.com", "password1", "password1"));
    }

    @Test
    void testPasswordNoLowercase() {
        assertEquals("Password must contain at least one lowercase letter.", SignUpController.validate("Alice","a@b.com", "PASSWORD1", "PASSWORD1"));
    }

    @Test
    void testPasswordNoDigit() {
        assertEquals("Password must contain at least one digit.", SignUpController.validate("Alice","a@b.com", "Password", "Password"));
    }

    @Test
    void testPasswordsDoNotMatch() {
        assertEquals("Passwords do not match.", SignUpController.validate("Alice","a@b.com", "Password1", "Password2"));
    }
}
