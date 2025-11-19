package com.teetime.database;

import com.teetime.domain.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {
    private static DatabaseManager instance;
    
    // In-memory storage
    private Map<Integer, User> users;
    private Map<Integer, Car> cars;
    private Map<Integer, Ride> rides;
    
    // Auto-increment IDs
    private AtomicInteger userIdCounter;
    private AtomicInteger carIdCounter;
    private AtomicInteger rideIdCounter;

    private DatabaseManager() {
        initDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initDatabase() {
        users = new HashMap<>();
        cars = new HashMap<>();
        rides = new HashMap<>();
        
        userIdCounter = new AtomicInteger(1);
        carIdCounter = new AtomicInteger(1);
        rideIdCounter = new AtomicInteger(1);
        
        // Create default admin user
        createDefaultAdmin();
    }
    
    private void createDefaultAdmin() {
        try {
            String passwordHash = hashPassword("admin");
            createUser("Admin User", "admin@ie.edu", passwordHash, "BOTH");
        } catch (Exception e) {
            System.err.println("Failed to create admin user: " + e.getMessage());
        }
    }
    
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return password;
        }
    }

    // User operations
    public int createUser(String name, String email, String passwordHash, String role) {
        // Check if email already exists
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return -1; // Email already exists
            }
        }
        
        int id = userIdCounter.getAndIncrement();
        User user;
        
        if (role.contains("DRIVER")) {
            user = new com.teetime.domain.Driver(id, name, email, passwordHash, role);
        } else {
            user = new Passenger(id, name, email, passwordHash, role);
        }
        
        users.put(id, user);
        return id;
    }

    public User getUserByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    // Car operations
    public int createCar(Car car) {
        int id = carIdCounter.getAndIncrement();
        car.setId(id);
        cars.put(id, car);
        return id;
    }

    public List<Car> getCarsByDriverId(int driverId) {
        List<Car> driverCars = new ArrayList<>();
        for (Car car : cars.values()) {
            if (car.getDriverId() == driverId) {
                driverCars.add(car);
            }
        }
        return driverCars;
    }

    public void deleteCar(int carId) {
        cars.remove(carId);
    }

    // Ride operations
    public int createRide(Ride ride) {
        int id = rideIdCounter.getAndIncrement();
        ride.setId(id);
        rides.put(id, ride);
        return id;
    }

    public List<Ride> getPendingRides() {
        List<Ride> pendingRides = new ArrayList<>();
        for (Ride ride : rides.values()) {
            if (ride.getStatus() == RideStatus.PENDING) {
                pendingRides.add(ride);
            }
        }
        return pendingRides;
    }

    public List<Ride> getRidesByPassengerId(int passengerId) {
        List<Ride> passengerRides = new ArrayList<>();
        for (Ride ride : rides.values()) {
            if (ride.getPassengerId() == passengerId) {
                passengerRides.add(ride);
            }
        }
        return passengerRides;
    }

    public List<Ride> getRidesByDriverId(int driverId) {
        List<Ride> driverRides = new ArrayList<>();
        for (Ride ride : rides.values()) {
            if (ride.getDriverId() != null && ride.getDriverId() == driverId) {
                driverRides.add(ride);
            }
        }
        return driverRides;
    }

    public void updateRide(Ride ride) {
        rides.put(ride.getId(), ride);
    }
}

