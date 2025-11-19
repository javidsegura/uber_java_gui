package com.teetime.domain;

public class Car {
    private int id;
    private int driverId;
    private String plate;
    private String brand;
    private int seats;

    public Car() {}

    public Car(int driverId, String plate, String brand, int seats) {
        this.driverId = driverId;
        this.plate = plate;
        this.brand = brand;
        this.seats = seats;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    @Override
    public String toString() {
        return brand + " (" + plate + ") - " + seats + " seats";
    }
}

