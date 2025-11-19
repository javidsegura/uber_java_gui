package com.teetime.service;

import com.teetime.database.DatabaseManager;
import com.teetime.domain.*;
import com.teetime.exception.CapacityExceededException;

import java.time.LocalDateTime;
import java.util.List;

public class RideService {
    private DatabaseManager db;

    public RideService() {
        this.db = DatabaseManager.getInstance();
    }

    public Ride createRideRequest(int passengerId, String origin, String destination, 
                                  LocalDateTime time, int seatsNeeded) throws Exception {
        // Validation
        if (origin == null || origin.trim().isEmpty()) {
            throw new Exception("Origin cannot be empty");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new Exception("Destination cannot be empty");
        }
        if (seatsNeeded <= 0) {
            throw new Exception("Seats needed must be greater than 0");
        }
        if (time.isBefore(LocalDateTime.now())) {
            throw new Exception("Time must be in the future");
        }

        // Simple price estimation (distance * base rate)
        double priceEstimate = calculatePrice(origin, destination, seatsNeeded);

        Ride ride = new Ride(passengerId, origin, destination, time, seatsNeeded, priceEstimate);
        
        int rideId = db.createRide(ride);
        if (rideId > 0) {
            ride.setId(rideId);
            return ride;
        } else {
            throw new Exception("Failed to create ride");
        }
    }

    public void acceptRide(Ride ride, int driverId, int carId) throws Exception {
        // Get car to check capacity
        List<Car> cars = db.getCarsByDriverId(driverId);
        Car selectedCar = null;
        for (Car car : cars) {
            if (car.getId() == carId) {
                selectedCar = car;
                break;
            }
        }

        if (selectedCar == null) {
            throw new Exception("Car not found");
        }

        // Check capacity
        if (ride.getSeatsNeeded() > selectedCar.getSeats()) {
            throw new CapacityExceededException(
                "Car capacity (" + selectedCar.getSeats() + ") is less than seats needed (" + ride.getSeatsNeeded() + ")"
            );
        }

        // Update ride
        ride.setDriverId(driverId);
        ride.setCarId(carId);
        ride.setStatus(RideStatus.CONFIRMED);
        db.updateRide(ride);
    }

    public void completeRide(Ride ride) throws Exception {
        ride.setStatus(RideStatus.COMPLETED);
        db.updateRide(ride);
    }

    public List<Ride> getPendingRides() {
        return db.getPendingRides();
    }

    public List<Ride> getRidesByPassengerId(int passengerId) {
        return db.getRidesByPassengerId(passengerId);
    }

    public List<Ride> getRidesByDriverId(int driverId) {
        return db.getRidesByDriverId(driverId);
    }

    private double calculatePrice(String origin, String destination, int seats) {
        // Simple price calculation: base rate * estimated distance factor * seats
        double baseRate = 5.0;
        double distanceFactor = 1.0 + (Math.random() * 2.0); // Simulate distance
        return baseRate * distanceFactor * seats;
    }

    public Car addCar(int driverId, String plate, String brand, int seats) throws Exception {
        if (plate == null || plate.trim().isEmpty()) {
            throw new Exception("Plate cannot be empty");
        }
        if (brand == null || brand.trim().isEmpty()) {
            throw new Exception("Brand cannot be empty");
        }
        if (seats <= 0 || seats > 8) {
            throw new Exception("Seats must be between 1 and 8");
        }

        Car car = new Car(driverId, plate, brand, seats);
        int carId = db.createCar(car);
        if (carId > 0) {
            car.setId(carId);
            return car;
        } else {
            throw new Exception("Failed to add car");
        }
    }

    public List<Car> getCarsByDriverId(int driverId) {
        return db.getCarsByDriverId(driverId);
    }

    public void deleteCar(int carId) {
        db.deleteCar(carId);
    }
}

