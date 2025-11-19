package com.teetime.service;

import com.teetime.database.DatabaseManager;
import com.teetime.domain.User;
import com.teetime.exception.InvalidLoginException;

import java.security.MessageDigest;
import java.sql.SQLException;

public class AuthService {
    private DatabaseManager db;

    public AuthService() {
        this.db = DatabaseManager.getInstance();
    }

    public User login(String email, String password) throws InvalidLoginException {
        User user = db.getUserByEmail(email);
        if (user == null) {
            throw new InvalidLoginException("User not found");
        }
        
        String passwordHash = hashPassword(password);
        if (!user.getPasswordHash().equals(passwordHash)) {
            throw new InvalidLoginException("Invalid password");
        }
        
        return user;
    }

    public User register(String name, String email, String password, String role) throws Exception {
        // Validate email domain
        if (!email.contains("@student.ie.edu") && !email.contains("@ie.edu")) {
            throw new Exception("Email must be a valid IE University email");
        }

        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Name cannot be empty");
        }
        if (password == null || password.length() < 4) {
            throw new Exception("Password must be at least 4 characters");
        }

        // Check if user already exists
        User existing = db.getUserByEmail(email);
        if (existing != null) {
            throw new Exception("Email already registered");
        }

        String passwordHash = hashPassword(password);
        int userId = db.createUser(name, email, passwordHash, role);
        
        if (userId > 0) {
            return db.getUserByEmail(email);
        } else {
            throw new Exception("Failed to create user");
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return password; // Fallback (not secure, but simple for demo)
        }
    }
}

