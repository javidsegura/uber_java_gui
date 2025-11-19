package com.teetime.service;

import com.teetime.domain.Ride;
import com.teetime.domain.RideStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CSVExportServiceTest {

    private Ride createRide(int id, int passengerId, Integer driverId, Integer carId,
                            String origin, String dest, LocalDateTime time,
                            int seats, RideStatus status, double price) {
        Ride ride = new Ride(passengerId, origin, dest, time, seats, price);
        ride.setId(id);
        ride.setDriverId(driverId);
        ride.setCarId(carId);
        ride.setStatus(status);
        return ride;
    }

    @Test
    void exportRidesToCSV_writesHeaderAndRows() throws IOException {
        CSVExportService service = new CSVExportService();

        LocalDateTime now = LocalDateTime.now();
        Ride r1 = createRide(1, 10, 20, 30, "Campus", "City", now, 2, RideStatus.PENDING, 12.34);
        Ride r2 = createRide(2, 11, null, null, "Home", "Campus", now.plusHours(1), 1, RideStatus.CONFIRMED, 8.50);

        List<Ride> rides = List.of(r1, r2);

        Path tempFile = Files.createTempFile("rides", ".csv");
        service.exportRidesToCSV(rides, tempFile.toString());

        assertTrue(Files.exists(tempFile));

        String content = Files.readString(tempFile);
        String[] lines = content.split("\\R");

        // Header + 2 rides
        assertTrue(lines.length >= 3);

        String header = lines[0];
        assertTrue(header.contains("ID"));
        assertTrue(header.contains("Passenger ID"));
        assertTrue(header.contains("Driver ID"));
        assertTrue(header.contains("Car ID"));
        assertTrue(header.contains("Origin"));
        assertTrue(header.contains("Destination"));
        assertTrue(header.contains("Time"));
        assertTrue(header.contains("Seats Needed"));
        assertTrue(header.contains("Status"));
        assertTrue(header.contains("Price"));

        String firstDataRow = lines[1];
        assertTrue(firstDataRow.startsWith("1,"), "First row should start with ride ID 1");
        assertTrue(firstDataRow.contains("Campus"));
        assertTrue(firstDataRow.contains("City"));
        assertTrue(firstDataRow.contains("PENDING"));

        String secondDataRow = lines[2];
        assertTrue(secondDataRow.startsWith("2,"));
        assertTrue(secondDataRow.contains("Home"));
        assertTrue(secondDataRow.contains("CONFIRMED"));
    }

    @Test
    void exportRidesToCSV_withEmptyList_writesOnlyHeader() throws IOException {
        CSVExportService service = new CSVExportService();

        List<Ride> rides = List.of();
        Path tempFile = Files.createTempFile("rides_empty", ".csv");

        service.exportRidesToCSV(rides, tempFile.toString());

        String content = Files.readString(tempFile);
        String[] lines = content.split("\\R");

        // Should at least have the header
        assertTrue(lines.length >= 1);
        assertTrue(lines[0].contains("ID"));
        if (lines.length > 1) {
            // all extra lines should be empty
            for (int i = 1; i < lines.length; i++) {
                assertTrue(lines[i].isEmpty());
            }
        }
    }
}
