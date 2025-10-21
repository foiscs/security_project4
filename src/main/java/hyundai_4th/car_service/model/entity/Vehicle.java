package hyundai_4th.car_service.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @Column(name = "vehicle_id", length = 36)
    private String vehicleId;

    @Column(name = "vin", nullable = false, unique = true, length = 64)
    private String vin;

    @Column(name = "plate", nullable = false, unique = true, length = 32)
    private String plate;

    @Column(name = "model", nullable = false, length = 80)
    private String model;

    @Column(name = "brand", nullable = false, length = 80)
    private String brand;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "current_location_id", nullable = false, length = 36)
    private String currentLocationId;

    // getter/setter
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
