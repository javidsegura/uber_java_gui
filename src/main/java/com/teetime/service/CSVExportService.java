package com.teetime.service;

import com.teetime.domain.Ride;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExportService {

    public void exportRidesToCSV(List<Ride> rides, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write header
            writer.append("ID,Passenger ID,Driver ID,Car ID,Origin,Destination,Time,Seats Needed,Status,Price\n");
            
            // Write data
            for (Ride ride : rides) {
                writer.append(String.valueOf(ride.getId())).append(",");
                writer.append(String.valueOf(ride.getPassengerId())).append(",");
                writer.append(ride.getDriverId() != null ? String.valueOf(ride.getDriverId()) : "").append(",");
                writer.append(ride.getCarId() != null ? String.valueOf(ride.getCarId()) : "").append(",");
                writer.append(ride.getOrigin()).append(",");
                writer.append(ride.getDestination()).append(",");
                writer.append(ride.getTime().toString()).append(",");
                writer.append(String.valueOf(ride.getSeatsNeeded())).append(",");
                writer.append(ride.getStatus().toString()).append(",");
                writer.append(String.format("%.2f", ride.getPriceEstimate())).append("\n");
            }
        }
    }
}

