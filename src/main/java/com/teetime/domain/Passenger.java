package com.teetime.domain;

public class Passenger extends User {
    
    public Passenger() {
        super();
    }

    public Passenger(String name, String email, String passwordHash) {
        super(name, email, passwordHash, "PASSENGER");
    }

    public Passenger(int id, String name, String email, String passwordHash, String role) {
        super(name, email, passwordHash, role);
        this.id = id;
    }
}

