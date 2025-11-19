package com.teetime.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RideStatusTest {

    @Test
    void enumContainsExpectedValues() {
        assertNotNull(RideStatus.valueOf("PENDING"));
        assertNotNull(RideStatus.valueOf("CONFIRMED"));
        assertNotNull(RideStatus.valueOf("COMPLETED"));
        assertNotNull(RideStatus.valueOf("CANCELLED"));

        RideStatus[] values = RideStatus.values();
        assertEquals(4, values.length);
    }
}
