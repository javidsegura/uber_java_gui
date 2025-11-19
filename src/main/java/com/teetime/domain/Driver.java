package com.teetime.domain;

public class Driver extends User {
    
    public Driver() {
        super();
    }

    public Driver(String name, String email, String passwordHash) {
        super(name, email, passwordHash, "DRIVER");
    }

    public Driver(int id, String name, String email, String passwordHash, String role) {
        super(name, email, passwordHash, role);
        this.id = id;
    }
}

