package com.security.test.repository;

import com.security.test.model.entity.VehicleTelemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleTelemetryRepository extends JpaRepository<VehicleTelemetry, Long> {

    // 최신 한 건
    Optional<VehicleTelemetry> findTopByVehicleIdOrderByTsDesc(String vehicleId);
}