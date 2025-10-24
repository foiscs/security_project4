package com.security.test.model.dto;

import java.time.Instant;

public class DoorStatusResponse {
    private String vehicleId;
    private boolean doorOpen;
    private String message;
    private Instant timestamp = Instant.now();

    public DoorStatusResponse(String vehicleId, boolean doorOpen, String message) {
        this.vehicleId = vehicleId;
        this.doorOpen = doorOpen;
        this.message = message;
    }
    public String getVehicleId() { return vehicleId; }
    public boolean isDoorOpen() { return doorOpen; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }
}
