package com.teetime.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RideTest {

    @Test
    void constructor_setsFieldsAndDefaultStatusPending() {
        LocalDateTime time = LocalDateTime.now();
        Ride ride = new Ride(5, "Origin", "Destination", time, 3, 25.0);

        assertEquals(0, ride.getId());
        assertEquals(5, ride.getPassengerId());
        assertEquals("Origin", ride.getOrigin());
        assertEquals("Destination", ride.getDestination());
        assertEquals(time, ride.getTime());
        assertEquals(3, ride.getSeatsNeeded());
        assertEquals(25.0, ride.getPriceEstimate());
        assertEquals(RideStatus.PENDING, ride.getStatus());
        assertNull(ride.getDriverId());
        assertNull(ride.getCarId());
    }

    @Test
    void settersUpdateAllFieldsCorrectly() {
        Ride ride = new Ride();
        LocalDateTime time = LocalDateTime.now();

        ride.setId(10);
        ride.setPassengerId(7);
        ride.setDriverId(3);
        ride.setCarId(2);
        ride.setOrigin("A");
        ride.setDestination("B");
        ride.setTime(time);
        ride.setSeatsNeeded(4);
        ride.setStatus(RideStatus.CONFIRMED);
        ride.setPriceEstimate(30.5);

        assertEquals(10, ride.getId());
        assertEquals(7, ride.getPassengerId());
        assertEquals(3, ride.getDriverId());
        assertEquals(2, ride.getCarId());
        assertEquals("A", ride.getOrigin());
        assertEquals("B", ride.getDestination());
        assertEquals(time, ride.getTime());
        assertEquals(4, ride.getSeatsNeeded());
        assertEquals(RideStatus.CONFIRMED, ride.getStatus());
        assertEquals(30.5, ride.getPriceEstimate());
    }
}
