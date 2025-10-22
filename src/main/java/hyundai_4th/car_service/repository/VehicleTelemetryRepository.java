package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.VehicleTelemetry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleTelemetryRepository extends JpaRepository<VehicleTelemetry, Long> {

    // 최신 한 건
    Optional<VehicleTelemetry> findTopByVehicleIdOrderByTsDesc(String vehicleId);
}