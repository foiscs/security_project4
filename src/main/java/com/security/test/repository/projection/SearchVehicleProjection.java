package com.security.test.repository.projection;

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