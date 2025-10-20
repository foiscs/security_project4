package hyundai_4th.car_service.repository.projection;

public interface SearchVehicleProjection {
    String getVehicleId();
    String getVin();
    String getPlate();
    String getBrand();
    String getModel();
    Integer getYearValue();
    String getStatus();
    String getCurrentLocationId();
}
