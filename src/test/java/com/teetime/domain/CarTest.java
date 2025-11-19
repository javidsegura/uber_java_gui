package com.teetime.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    void constructorAndGettersWorkCorrectly() {
        Car car = new Car(42, "1234ABC", "Toyota", 4);

        assertEquals(0, car.getId(), "Default id should be 0 until set");
        assertEquals(42, car.getDriverId());
        assertEquals("1234ABC", car.getPlate());
        assertEquals("Toyota", car.getBrand());
        assertEquals(4, car.getSeats());
    }

    @Test
    void settersUpdateFieldsProperly() {
        Car car = new Car();

        car.setId(10);
        car.setDriverId(5);
        car.setPlate("9999XYZ");
        car.setBrand("BMW");
        car.setSeats(2);

        assertEquals(10, car.getId());
        assertEquals(5, car.getDriverId());
        assertEquals("9999XYZ", car.getPlate());
        assertEquals("BMW", car.getBrand());
        assertEquals(2, car.getSeats());
    }

    @Test
    void toString_hasExpectedFormat() {
        Car car = new Car(1, "1234ABC", "Toyota", 4);
        String s = car.toString();

        assertTrue(s.contains("Toyota"));
        assertTrue(s.contains("1234ABC"));
        assertTrue(s.contains("4"), "Seats count should be in toString");
    }
}
