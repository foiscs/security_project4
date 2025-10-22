package hyundai_4th.car_service.model.dto;

import hyundai_4th.car_service.model.entity.Vehicle;

/**
 * 차량 정보 응답 DTO
 * Vehicle 엔티티의 주요 정보를 전달
 */
public class VehicleResponse {
    private String vehicleId;
    private String vin;
    private String plate;
    private String model;
    private String brand;
    private int year;                // Vehicle 엔티티의 year 필드 타입에 맞춤
    private String status;

    // ✅ 기존 currentLocation → currentLocationId 로 변경 (최소 수정)
    private String currentLocationId;

    public VehicleResponse() {}

    public VehicleResponse(Vehicle vehicle) {
        this.vehicleId = vehicle.getVehicleId();
        this.vin = vehicle.getVin();
        this.plate = vehicle.getPlate();
        this.model = vehicle.getModel();
        this.brand = vehicle.getBrand();
        this.year = vehicle.getYear();
        this.status = vehicle.getStatus();
        this.currentLocationId = vehicle.getCurrentLocationId(); // ✅ 수정된 부분
    }

    // ---- Getter & Setter ----
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
