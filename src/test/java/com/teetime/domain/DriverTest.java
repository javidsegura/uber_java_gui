package com.teetime.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DriverTest {

    @Test
    void constructorWithoutId_setsRoleToDriver() {
        Driver driver = new Driver("John", "john@example.com", "hash");

        assertEquals("John", driver.getName());
        assertEquals("john@example.com", driver.getEmail());
        assertEquals("hash", driver.getPasswordHash());
        assertEquals("DRIVER", driver.getRole());
        assertEquals(0, driver.getId(), "Id should default to 0 when not provided");
    }

    @Test
    void constructorWithId_setsAllFields() {
        Driver driver = new Driver(7, "Jane", "jane@example.com", "hash2", "DRIVER");

        assertEquals(7, driver.getId());
        assertEquals("Jane", driver.getName());
        assertEquals("jane@example.com", driver.getEmail());
        assertEquals("hash2", driver.getPasswordHash());
        assertEquals("DRIVER", driver.getRole());
    }
}
