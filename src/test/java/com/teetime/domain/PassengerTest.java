package com.teetime.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PassengerTest {

    @Test
    void constructorWithoutId_setsRoleToPassenger() {
        Passenger passenger = new Passenger("Alice", "alice@example.com", "hash");

        assertEquals("Alice", passenger.getName());
        assertEquals("alice@example.com", passenger.getEmail());
        assertEquals("hash", passenger.getPasswordHash());
        assertEquals("PASSENGER", passenger.getRole());
        assertEquals(0, passenger.getId());
    }

    @Test
    void constructorWithId_setsAllFields() {
        Passenger passenger = new Passenger(3, "Bob", "bob@example.com", "hash2", "PASSENGER");

        assertEquals(3, passenger.getId());
        assertEquals("Bob", passenger.getName());
        assertEquals("bob@example.com", passenger.getEmail());
        assertEquals("hash2", passenger.getPasswordHash());
        assertEquals("PASSENGER", passenger.getRole());
    }
}
