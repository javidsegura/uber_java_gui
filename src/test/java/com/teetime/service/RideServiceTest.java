package com.teetime.service;

import com.teetime.database.DatabaseManager;
import com.teetime.domain.Car;
import com.teetime.domain.Ride;
import com.teetime.domain.RideStatus;
import com.teetime.exception.CapacityExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RideServiceTest {

    private DatabaseManager db;
    private RideService rideService;

    @BeforeEach
    void setUp() throws Exception {
        Field instanceField = DatabaseManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        db = DatabaseManager.getInstance();
        rideService = new RideService();
    }

    private int createPassenger(String email) {
        return db.createUser("Passenger", email, "hash", "PASSENGER");
    }

    private int createDriver(String email) {
        return db.createUser("Driver", email, "hash", "DRIVER");
    }

    @Test
    void createRideRequest_withValidData_createsPendingRideWithPrice() throws Exception {
        int passengerId = createPassenger("p1@student.ie.edu");
        LocalDateTime future = LocalDateTime.now().plusHours(1);

        Ride ride = rideService.createRideRequest(
                passengerId, "Campus", "Center", future, 2
        );

        assertNotNull(ride);
        assertTrue(ride.getId() > 0);
        assertEquals(passengerId, ride.getPassengerId());
        assertEquals("Campus", ride.getOrigin());
        assertEquals("Center", ride.getDestination());
        assertEquals(2, ride.getSeatsNeeded());
        assertEquals(RideStatus.PENDING, ride.getStatus());
        assertTrue(ride.getPriceEstimate() > 0, "Price estimate should be > 0");

        List<Ride> pending = rideService.getPendingRides();
        assertTrue(pending.stream().anyMatch(r -> r.getId() == ride.getId()));
    }

    @Test
    void createRideRequest_withEmptyOrigin_throwsException() {
        int passengerId = createPassenger("p1@student.ie.edu");
        LocalDateTime future = LocalDateTime.now().plusHours(1);

        Exception ex = assertThrows(Exception.class, () ->
                rideService.createRideRequest(passengerId, "   ", "Dest", future, 1)
        );
        assertEquals("Origin cannot be empty", ex.getMessage());
    }

    @Test
    void createRideRequest_withEmptyDestination_throwsException() {
        int passengerId = createPassenger("p1@student.ie.edu");
        LocalDateTime future = LocalDateTime.now().plusHours(1);

        Exception ex = assertThrows(Exception.class, () ->
                rideService.createRideRequest(passengerId, "Origin", "", future, 1)
        );
        assertEquals("Destination cannot be empty", ex.getMessage());
    }

    @Test
    void createRideRequest_withNonPositiveSeats_throwsException() {
        int passengerId = createPassenger("p1@student.ie.edu");
        LocalDateTime future = LocalDateTime.now().plusHours(1);

        Exception ex = assertThrows(Exception.class, () ->
                rideService.createRideRequest(passengerId, "Origin", "Dest", future, 0)
        );
        assertEquals("Seats needed must be greater than 0", ex.getMessage());
    }

    @Test
    void createRideRequest_withPastTime_throwsException() {
        int passengerId = createPassenger("p1@student.ie.edu");
        LocalDateTime past = LocalDateTime.now().minusHours(1);

        Exception ex = assertThrows(Exception.class, () ->
                rideService.createRideRequest(passengerId, "Origin", "Dest", past, 1)
        );
        assertEquals("Time must be in the future", ex.getMessage());
    }

    @Test
    void addCar_withValidData_createsCar() throws Exception {
        int driverId = createDriver("d1@ie.edu");

        Car car = rideService.addCar(driverId, "1234ABC", "Toyota", 4);

        assertNotNull(car);
        assertTrue(car.getId() > 0);
        assertEquals(driverId, car.getDriverId());
        assertEquals("1234ABC", car.getPlate());
        assertEquals("Toyota", car.getBrand());
        assertEquals(4, car.getSeats());

        List<Car> cars = rideService.getCarsByDriverId(driverId);
        assertEquals(1, cars.size());
    }

    @Test
    void addCar_withInvalidPlate_throwsException() {
        int driverId = createDriver("d1@ie.edu");

        Exception ex = assertThrows(Exception.class, () ->
                rideService.addCar(driverId, "   ", "Toyota", 4)
        );
        assertEquals("Plate cannot be empty", ex.getMessage());
    }

    @Test
    void addCar_withInvalidBrand_throwsException() {
        int driverId = createDriver("d1@ie.edu");

        Exception ex = assertThrows(Exception.class, () ->
                rideService.addCar(driverId, "1234ABC", "", 4)
        );
        assertEquals("Brand cannot be empty", ex.getMessage());
    }

    @Test
    void addCar_withInvalidSeats_throwsException() {
        int driverId = createDriver("d1@ie.edu");

        Exception ex = assertThrows(Exception.class, () ->
                rideService.addCar(driverId, "1234ABC", "Toyota", 0)
        );
        assertEquals("Seats must be between 1 and 8", ex.getMessage());
    }

    @Test
    void acceptRide_withCarNotFound_throwsException() throws Exception {
        int passengerId = createPassenger("p1@student.ie.edu");
        int driverId = createDriver("d1@ie.edu");

        LocalDateTime future = LocalDateTime.now().plusHours(1);
        Ride ride = rideService.createRideRequest(passengerId, "O", "D", future, 2);

        Exception ex = assertThrows(Exception.class, () ->
                rideService.acceptRide(ride, driverId, 999) // non-existing carId
        );
        assertEquals("Car not found", ex.getMessage());
    }

    @Test
    void acceptRide_withInsufficientSeats_throwsCapacityExceededException() throws Exception {
        int passengerId = createPassenger("p1@student.ie.edu");
        int driverId = createDriver("d1@ie.edu");

        // driver car with 2 seats
        Car car = rideService.addCar(driverId, "1234ABC", "Toyota", 2);

        LocalDateTime future = LocalDateTime.now().plusHours(1);
        Ride ride = rideService.createRideRequest(passengerId, "O", "D", future, 3);

        CapacityExceededException ex = assertThrows(CapacityExceededException.class, () ->
                rideService.acceptRide(ride, driverId, car.getId())
        );
        assertTrue(ex.getMessage().contains("Car capacity"));
    }

    @Test
    void acceptRide_withEnoughSeats_confirmsRideAndSetsDriverAndCar() throws Exception {
        int passengerId = createPassenger("p1@student.ie.edu");
        int driverId = createDriver("d1@ie.edu");

        Car car = rideService.addCar(driverId, "1234ABC", "Toyota", 4);
        LocalDateTime future = LocalDateTime.now().plusHours(1);
        Ride ride = rideService.createRideRequest(passengerId, "O", "D", future, 2);

        rideService.acceptRide(ride, driverId, car.getId());

        assertEquals(driverId, ride.getDriverId());
        assertEquals(car.getId(), ride.getCarId());
        assertEquals(RideStatus.CONFIRMED, ride.getStatus());

        List<Ride> driverRides = rideService.getRidesByDriverId(driverId);
        assertTrue(driverRides.stream().anyMatch(r -> r.getId() == ride.getId()));
    }

    @Test
    void completeRide_setsStatusCompletedAndPersists() throws Exception {
        int passengerId = createPassenger("p1@student.ie.edu");
        int driverId = createDriver("d1@ie.edu");
        Car car = rideService.addCar(driverId, "1234ABC", "Toyota", 4);
        LocalDateTime future = LocalDateTime.now().plusHours(1);
        Ride ride = rideService.createRideRequest(passengerId, "O", "D", future, 1);

        rideService.acceptRide(ride, driverId, car.getId());
        rideService.completeRide(ride);

        assertEquals(RideStatus.COMPLETED, ride.getStatus());

        List<Ride> driverRides = rideService.getRidesByDriverId(driverId);
        Ride stored = driverRides.stream()
                .filter(r -> r.getId() == ride.getId())
                .findFirst()
                .orElseThrow();
        assertEquals(RideStatus.COMPLETED, stored.getStatus());
    }

    @Test
    void deleteCar_removesCarFromDatabase() throws Exception {
        int driverId = createDriver("d1@ie.edu");
        Car car = rideService.addCar(driverId, "1234ABC", "Toyota", 4);

        assertEquals(1, rideService.getCarsByDriverId(driverId).size());

        rideService.deleteCar(car.getId());

        assertTrue(rideService.getCarsByDriverId(driverId).isEmpty());
    }
}
