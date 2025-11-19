package com.teetime.service;

import com.teetime.domain.Ride;
import com.teetime.exception.CapacityExceededException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class RideServiceTest {

    @Test
    public void testPriceCalculation() {
        RideService service = new RideService();
        try {
            // Create a ride and verify price is calculated
            LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
            Ride ride = service.createRideRequest(1, "Campus", "City Center", futureTime, 2);
            
            assertNotNull(ride);
            assertTrue(ride.getPriceEstimate() > 0, "Price should be greater than 0");
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidOrigin() {
        RideService service = new RideService();
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        
        Exception exception = assertThrows(Exception.class, () -> {
            service.createRideRequest(1, "", "City Center", futureTime, 2);
        });
        
        assertTrue(exception.getMessage().contains("Origin"));
    }

    @Test
    public void testInvalidSeats() {
        RideService service = new RideService();
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        
        Exception exception = assertThrows(Exception.class, () -> {
            service.createRideRequest(1, "Campus", "City Center", futureTime, 0);
        });
        
        assertTrue(exception.getMessage().contains("Seats"));
    }

    @Test
    public void testPastTime() {
        RideService service = new RideService();
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        
        Exception exception = assertThrows(Exception.class, () -> {
            service.createRideRequest(1, "Campus", "City Center", pastTime, 2);
        });
        
        assertTrue(exception.getMessage().contains("future"));
    }
}

