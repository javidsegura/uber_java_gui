package com.teetime.database;

import com.teetime.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    private DatabaseManager db;

    @BeforeEach
    void setUp() throws Exception {
        // Reset the singleton so each test has a fresh DB
        Field instanceField = DatabaseManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        db = DatabaseManager.getInstance();
    }

    @Test
    void defaultAdminUser_isCreatedWithHashedPassword() {
        User admin = db.getUserByEmail("admin@ie.edu");
        assertNotNull(admin, "Admin user should exist on initialization");
        assertEquals("Admin User", admin.getName());
        assertEquals("BOTH", admin.getRole());
        assertNotEquals("admin", admin.getPasswordHash(), "Password should not be stored in plain text");
        assertNotNull(admin.getPasswordHash());
        assertFalse(admin.getPasswordHash().isEmpty());
    }

    @Test
    void createUser_withUniqueEmail_returnsPositiveIdAndUserIsRetrievable() {
        int id = db.createUser("John Doe", "john@example.com", "hash123", "PASSENGER");
        assertTrue(id > 0);

        User user = db.getUserByEmail("john@example.com");
        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("hash123", user.getPasswordHash());
        assertEquals("PASSENGER", user.getRole());
        assertTrue(user instanceof Passenger, "PASSENGER role should create a Passenger instance");
    }

    @Test
    void createUser_withDuplicateEmail_returnsMinusOneAndDoesNotOverwriteExistingUser() {
        int firstId = db.createUser("Original", "same@example.com", "hash1", "PASSENGER");
        assertTrue(firstId > 0);

        int secondId = db.createUser("Duplicate", "same@example.com", "hash2", "DRIVER");
        assertEquals(-1, secondId, "Duplicate email should return -1");

        User user = db.getUserByEmail("same@example.com");
        assertNotNull(user);
        assertEquals("Original", user.getName(), "Existing user should not be overwritten");
        assertEquals("hash1", user.getPasswordHash());
    }

    @Test
    void createUser_withDriverRole_createsDriverInstance() {
        int id = db.createUser("Driver Dude", "driver@example.com", "hashD", "DRIVER");
        assertTrue(id > 0);

        User user = db.getUserByEmail("driver@example.com");
        assertNotNull(user);
        assertEquals("Driver Dude", user.getName());
        assertTrue(user instanceof Driver, "DRIVER role should create a Driver instance");
    }

    @Test
    void getUserByEmail_unknownEmail_returnsNull() {
        User user = db.getUserByEmail("unknown@example.com");
        assertNull(user);
    }

    @Test
    void createCar_assignsIdAndCarIsReturnedByGetCarsByDriverId() {
        int driverId = db.createUser("Driver", "d@example.com", "hash", "DRIVER");
        Car car = new Car(driverId, "1234ABC", "Toyota", 4);

        int carId = db.createCar(car);
        assertTrue(carId > 0);
        assertEquals(carId, car.getId());

        List<Car> cars = db.getCarsByDriverId(driverId);
        assertEquals(1, cars.size());
        Car storedCar = cars.get(0);
        assertEquals("1234ABC", storedCar.getPlate());
        assertEquals("Toyota", storedCar.getBrand());
        assertEquals(4, storedCar.getSeats());
    }

    @Test
    void deleteCar_removesCarFromStorage() {
        int driverId = db.createUser("Driver", "d@example.com", "hash", "DRIVER");
        Car car = new Car(driverId, "9999XYZ", "BMW", 2);

        int carId = db.createCar(car);
        assertEquals(carId, car.getId());

        assertEquals(1, db.getCarsByDriverId(driverId).size());

        db.deleteCar(carId);

        assertTrue(db.getCarsByDriverId(driverId).isEmpty(), "Car list should be empty after deletion");
    }

    @Test
    void createRide_assignsIdAndIsPendingByDefault() {
        int passengerId = db.createUser("Passenger", "p@example.com", "hash", "PASSENGER");

        Ride ride = new Ride(
                passengerId,
                "Origin",
                "Destination",
                LocalDateTime.now(),
                2,
                15.5
        );

        int rideId = db.createRide(ride);
        assertTrue(rideId > 0);
        assertEquals(rideId, ride.getId());
        assertEquals(RideStatus.PENDING, ride.getStatus());

        List<Ride> pending = db.getPendingRides();
        assertEquals(1, pending.size());
        assertEquals(rideId, pending.get(0).getId());
    }

    @Test
    void getPendingRides_returnsOnlyRidesWithPendingStatus() {
        int passengerId = db.createUser("Passenger", "p@example.com", "hash", "PASSENGER");
        int driverId = db.createUser("Driver", "d@example.com", "hash", "DRIVER");

        Ride ride1 = new Ride(passengerId, "A", "B", LocalDateTime.now(), 1, 10.0);
        Ride ride2 = new Ride(passengerId, "C", "D", LocalDateTime.now(), 2, 20.0);

        db.createRide(ride1);
        db.createRide(ride2);

        // Confirm one ride
        ride2.setDriverId(driverId);
        ride2.setStatus(RideStatus.CONFIRMED);
        db.updateRide(ride2);

        List<Ride> pending = db.getPendingRides();
        assertEquals(1, pending.size());
        assertEquals(ride1.getId(), pending.get(0).getId());
        assertEquals(RideStatus.PENDING, pending.get(0).getStatus());
    }

    @Test
    void getRidesByPassengerId_returnsOnlyThatPassengersRides() {
        int passenger1Id = db.createUser("P1", "p1@example.com", "hash", "PASSENGER");
        int passenger2Id = db.createUser("P2", "p2@example.com", "hash", "PASSENGER");

        Ride r1 = new Ride(passenger1Id, "A", "B", LocalDateTime.now(), 1, 10.0);
        Ride r2 = new Ride(passenger2Id, "C", "D", LocalDateTime.now(), 1, 12.0);
        Ride r3 = new Ride(passenger1Id, "E", "F", LocalDateTime.now(), 2, 20.0);

        db.createRide(r1);
        db.createRide(r2);
        db.createRide(r3);

        List<Ride> p1Rides = db.getRidesByPassengerId(passenger1Id);
        assertEquals(2, p1Rides.size());
        assertTrue(p1Rides.stream().allMatch(r -> r.getPassengerId() == passenger1Id));
    }

    @Test
    void getRidesByDriverId_returnsOnlyThatDriversRides() {
        int passengerId = db.createUser("P", "p@example.com", "hash", "PASSENGER");
        int driver1Id = db.createUser("D1", "d1@example.com", "hash", "DRIVER");
        int driver2Id = db.createUser("D2", "d2@example.com", "hash", "DRIVER");

        Ride r1 = new Ride(passengerId, "A", "B", LocalDateTime.now(), 1, 10.0);
        Ride r2 = new Ride(passengerId, "C", "D", LocalDateTime.now(), 1, 12.0);
        Ride r3 = new Ride(passengerId, "E", "F", LocalDateTime.now(), 2, 20.0);

        db.createRide(r1);
        db.createRide(r2);
        db.createRide(r3);

        r1.setDriverId(driver1Id);
        r2.setDriverId(driver2Id);
        r3.setDriverId(driver1Id);

        db.updateRide(r1);
        db.updateRide(r2);
        db.updateRide(r3);

        List<Ride> d1Rides = db.getRidesByDriverId(driver1Id);
        assertEquals(2, d1Rides.size());
        assertTrue(d1Rides.stream().allMatch(r -> driver1Id == r.getDriverId()));

        List<Ride> d2Rides = db.getRidesByDriverId(driver2Id);
        assertEquals(1, d2Rides.size());
        assertEquals(driver2Id, d2Rides.get(0).getDriverId());
    }

    @Test
    void updateRide_persistsChangesToExistingRide() {
        int passengerId = db.createUser("P", "p@example.com", "hash", "PASSENGER");
        int driverId = db.createUser("D", "d@example.com", "hash", "DRIVER");

        Ride ride = new Ride(passengerId, "A", "B", LocalDateTime.now(), 1, 10.0);
        db.createRide(ride);

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.CONFIRMED);
        db.updateRide(ride);

        // Should no longer be pending
        List<Ride> pending = db.getPendingRides();
        assertTrue(pending.isEmpty());

        // Should appear in driver's rides
        List<Ride> driverRides = db.getRidesByDriverId(driverId);
        assertEquals(1, driverRides.size());
        assertEquals(RideStatus.CONFIRMED, driverRides.get(0).getStatus());
    }
}
