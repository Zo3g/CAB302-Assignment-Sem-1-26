package com.example.cab302assignment;

import com.example.cab302assignment.controller.SignUpController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignUpValidationTest {
    @Test
    void testValidInput() {
        assertNull(SignUpController.validate("alice@test.com", "Password1", "Password1"));
    }

    @Test
    void testEmptyEmail() {
        assertEquals("Email address is required.", SignUpController.validate("", "Password1", "Password1"));
    }

    @Test
    void testInvalidEmail() {
        assertEquals("Please enter a valid email address.", SignUpController.validate("notanemail", "Password1", "Password1"));
    }

    @Test
    void testEmailMissingDomain() {
        assertEquals("Please enter a valid email address.", SignUpController.validate("alice@", "Password1", "Password1"));
    }

    @Test
    void testPasswordTooShort() {
        assertEquals("Password must be at least 8 characters.", SignUpController.validate("a@b.com", "Pass1", "Pass1"));
    }

    @Test
    void testPasswordNoUppercase() {
        assertEquals("Password must contain at least one uppercase letter.", SignUpController.validate("a@b.com", "password1", "password1"));
    }

    @Test
    void testPasswordNoLowercase() {
        assertEquals("Password must contain at least one lowercase letter.", SignUpController.validate("a@b.com", "PASSWORD1", "PASSWORD1"));
    }

    @Test
    void testPasswordNoDigit() {
        assertEquals("Password must contain at least one digit.", SignUpController.validate("a@b.com", "Password", "Password"));
    }

    @Test
    void testPasswordsDoNotMatch() {
        assertEquals("Passwords do not match.", SignUpController.validate("a@b.com", "Password1", "Password2"));
    }
}
