package hyundai_4th.car_service.model.entity;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
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

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "available";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_location_id", nullable = false)
    private Location currentLocation;

    // 기본 생성자
    public Vehicle() {
    }

    // 생성자
    public Vehicle(String vin, String plate, String model, String brand,
                   Integer year, Location currentLocation) {
        this.vin = vin;
        this.plate = plate;
        this.model = model;
        this.brand = brand;
        this.year = year;
        this.currentLocation = currentLocation;
    }

    // Getter & Setter
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}