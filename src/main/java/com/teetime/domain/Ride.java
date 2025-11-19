package com.teetime.domain;

import java.time.LocalDateTime;

public class Ride {
    private int id;
    private int passengerId;
    private Integer driverId; // nullable
    private Integer carId; // nullable
    private String origin;
    private String destination;
    private LocalDateTime time;
    private int seatsNeeded;
    private RideStatus status;
    private double priceEstimate;

    public Ride() {}

    public Ride(int passengerId, String origin, String destination, 
                LocalDateTime time, int seatsNeeded, double priceEstimate) {
        this.passengerId = passengerId;
        this.origin = origin;
        this.destination = destination;
        this.time = time;
        this.seatsNeeded = seatsNeeded;
        this.status = RideStatus.PENDING;
        this.priceEstimate = priceEstimate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPassengerId() { return passengerId; }
    public void setPassengerId(int passengerId) { this.passengerId = passengerId; }

    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }

    public Integer getCarId() { return carId; }
    public void setCarId(Integer carId) { this.carId = carId; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }

    public int getSeatsNeeded() { return seatsNeeded; }
    public void setSeatsNeeded(int seatsNeeded) { this.seatsNeeded = seatsNeeded; }

    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }

    public double getPriceEstimate() { return priceEstimate; }
    public void setPriceEstimate(double priceEstimate) { this.priceEstimate = priceEstimate; }
}

