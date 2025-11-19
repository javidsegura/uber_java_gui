package com.teetime.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    // Tiny concrete class just for testing the abstract User
    private static class TestUser extends User {
        public TestUser(String name, String email, String passwordHash, String role) {
            super(name, email, passwordHash, role);
        }
    }

    @Test
    void constructor_setsFieldsCorrectly() {
        User user = new TestUser("Name", "email@example.com", "hash", "ROLE");

        assertEquals("Name", user.getName());
        assertEquals("email@example.com", user.getEmail());
        assertEquals("hash", user.getPasswordHash());
        assertEquals("ROLE", user.getRole());
        assertEquals(0, user.getId());
    }

    @Test
    void settersUpdateFields() {
        User user = new TestUser("Old", "old@example.com", "oldHash", "OLD_ROLE");

        user.setId(5);
        user.setName("New");
        user.setEmail("new@example.com");
        user.setPasswordHash("newHash");
        user.setRole("NEW_ROLE");

        assertEquals(5, user.getId());
        assertEquals("New", user.getName());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("newHash", user.getPasswordHash());
        assertEquals("NEW_ROLE", user.getRole());
    }
}
