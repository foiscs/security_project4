package hyundai_4th.car_service.model.dto;

import hyundai_4th.car_service.model.entity.Vehicle;

/**
 * 차량 정보 응답 DTO
 * 차량 검색, 대여 가능 차량 조회 시 사용
 */
public class VehicleResponse {

    private String vehicleId;
    private String vin;
    private String plate;
    private String model;
    private String brand;
    private Integer year;
    private String status;
    private LocationResponse currentLocation;

    // 기본 생성자
    public VehicleResponse() {
    }

    // Entity를 DTO로 변환하는 생성자
    public VehicleResponse(Vehicle vehicle) {
        this.vehicleId = vehicle.getVehicleId();
        this.vin = vehicle.getVin();
        this.plate = vehicle.getPlate();
        this.model = vehicle.getModel();
        this.brand = vehicle.getBrand();
        this.year = vehicle.getYear();
        this.status = vehicle.getStatus();
        // 위치 정보도 DTO로 변환
        if (vehicle.getCurrentLocation() != null) {
            this.currentLocation = new LocationResponse(vehicle.getCurrentLocation());
        }
    }

    // 전체 생성자
    public VehicleResponse(String vehicleId, String vin, String plate, String model,
                           String brand, Integer year, String status, LocationResponse currentLocation) {
        this.vehicleId = vehicleId;
        this.vin = vin;
        this.plate = plate;
        this.model = model;
        this.brand = brand;
        this.year = year;
        this.status = status;
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

    public LocationResponse getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationResponse currentLocation) {
        this.currentLocation = currentLocation;
    }
}