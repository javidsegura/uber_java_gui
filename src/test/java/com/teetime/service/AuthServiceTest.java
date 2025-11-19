package com.teetime.service;

import com.teetime.database.DatabaseManager;
import com.teetime.domain.User;
import com.teetime.exception.InvalidLoginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private DatabaseManager db;
    private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        // Reset singleton
        Field instanceField = DatabaseManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        db = DatabaseManager.getInstance();
        authService = new AuthService();
    }

    @Test
    void register_withValidData_createsUserAndHashesPassword() throws Exception {
        String name = "John Doe";
        String email = "john@student.ie.edu";
        String password = "1234";
        String role = "PASSENGER";

        User user = authService.register(name, email, password, role);

        assertNotNull(user);
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(role, user.getRole());
        assertNotEquals(password, user.getPasswordHash(), "Password must be stored as hash");

        User fromDb = db.getUserByEmail(email);
        assertNotNull(fromDb);
        assertEquals(user.getId(), fromDb.getId());
    }

    @Test
    void register_withInvalidEmailDomain_throwsException() {
        Exception ex = assertThrows(Exception.class, () ->
                authService.register("John", "john@gmail.com", "1234", "PASSENGER")
        );
        assertEquals("Email must be a valid IE University email", ex.getMessage());
    }

    @Test
    void register_withEmptyName_throwsException() {
        Exception ex = assertThrows(Exception.class, () ->
                authService.register("   ", "john@student.ie.edu", "1234", "PASSENGER")
        );
        assertEquals("Name cannot be empty", ex.getMessage());
    }

    @Test
    void register_withShortPassword_throwsException() {
        Exception ex = assertThrows(Exception.class, () ->
                authService.register("John", "john@student.ie.edu", "123", "PASSENGER")
        );
        assertEquals("Password must be at least 4 characters", ex.getMessage());
    }

    @Test
    void register_withExistingEmail_throwsException() throws Exception {
        String email = "john@student.ie.edu";
        authService.register("John", email, "1234", "PASSENGER");

        Exception ex = assertThrows(Exception.class, () ->
                authService.register("Other", email, "abcd", "DRIVER")
        );
        assertEquals("Email already registered", ex.getMessage());
    }

    @Test
    void login_withCorrectCredentials_returnsUser() throws Exception {
        String email = "john@student.ie.edu";
        String password = "abcd";
        authService.register("John", email, password, "PASSENGER");

        User user = authService.login(email, password);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
    }

    @Test
    void login_withWrongPassword_throwsInvalidLoginException() throws Exception {
        String email = "john@student.ie.edu";
        authService.register("John", email, "correct", "PASSENGER");

        InvalidLoginException ex = assertThrows(InvalidLoginException.class, () ->
                authService.login(email, "wrong")
        );
        assertEquals("Invalid password", ex.getMessage());
    }

    @Test
    void login_withUnknownEmail_throwsInvalidLoginException() {
        InvalidLoginException ex = assertThrows(InvalidLoginException.class, () ->
                authService.login("noone@student.ie.edu", "1234")
        );
        assertEquals("User not found", ex.getMessage());
    }
}
