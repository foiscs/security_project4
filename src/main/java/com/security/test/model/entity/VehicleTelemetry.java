package com.security.test.model.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_telemetry")
public class VehicleTelemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "telemetry_id")
    private Long telemetryId;

    @Column(name = "vehicle_id", length = 36, nullable = false)
    private String vehicleId;

    @Column(name = "ts", nullable = false)
    private LocalDateTime ts;

    @Column(name = "lat", precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(name = "lng", precision = 9, scale = 6)
    private BigDecimal lng;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "ignition")
    private Boolean ignition;

    @Column(name = "door_open", nullable = false)
    private Boolean doorOpen = false;

    @Column(name = "raw_payload", columnDefinition = "json")
    private String rawPayload;

    public VehicleTelemetry() {}

    // 편의 생성자
    public VehicleTelemetry(String vehicleId, LocalDateTime ts, Boolean doorOpen) {
        this.vehicleId = vehicleId;
        this.ts = ts;
        this.doorOpen = doorOpen;
    }

    // getters & setters
    public Long getTelemetryId() { return telemetryId; }
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public LocalDateTime getTs() { return ts; }
    public void setTs(LocalDateTime ts) { this.ts = ts; }
    public BigDecimal getLat() { return lat; }
    public void setLat(BigDecimal lat) { this.lat = lat; }
    public BigDecimal getLng() { return lng; }
    public void setLng(BigDecimal lng) { this.lng = lng; }
    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }
    public Boolean getIgnition() { return ignition; }
    public void setIgnition(Boolean ignition) { this.ignition = ignition; }
    public Boolean getDoorOpen() { return doorOpen; }
    public void setDoorOpen(Boolean doorOpen) { this.doorOpen = doorOpen; }
    public String getRawPayload() { return rawPayload; }
    public void setRawPayload(String rawPayload) { this.rawPayload = rawPayload; }
}
