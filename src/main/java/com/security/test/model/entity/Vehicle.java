package com.security.test.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    // ✅ 2번 스타일: 외부 주입 ID (uuid2 생성기 제거)
    @Id
    @Column(name = "vehicle_id", length = 36, nullable = false)
    private String vehicleId;

    @Column(name = "vin", length = 64, unique = true, nullable = false)
    private String vin;

    @Column(name = "plate", length = 32, unique = true, nullable = false)
    private String plate;

    @Column(name = "model", length = 80, nullable = false)
    private String model;

    @Column(name = "brand", length = 80, nullable = false)
    private String brand;

    // ✅ 2번 스타일: 원시 타입 int
    @Column(name = "year", nullable = false)
    private int year;

    // ✅ 2번 스타일: 기본값 제거 (DB/서비스 레이어에서 세팅)
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    // ✅ 2번 스타일: 연관관계 대신 ID 보관
    @Column(name = "current_location_id", nullable = false, length = 36)
    private String currentLocationId;

    // 기본 생성자
    public Vehicle() {}

    // 필요 시 생성자(편의용)
    public Vehicle(String vehicleId, String vin, String plate, String model, String brand,
                   int year, String status, String currentLocationId) {
        this.vehicleId = vehicleId;
        this.vin = vin;
        this.plate = plate;
        this.model = model;
        this.brand = brand;
        this.year = year;
        this.status = status;
        this.currentLocationId = currentLocationId;
    }

    // Getter & Setter
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentLocationId() { return currentLocationId; }
    public void setCurrentLocationId(String currentLocationId) { this.currentLocationId = currentLocationId; }
}
