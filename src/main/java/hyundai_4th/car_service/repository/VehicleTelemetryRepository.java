package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.VehicleTelemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleTelemetryRepository extends JpaRepository<VehicleTelemetry, Long> {

    // 특정 차량의 텔레메트리 조회
    List<VehicleTelemetry> findByVehicle_VehicleId(String vehicleId);

    // 특정 차량의 특정 기간 텔레메트리 조회
    List<VehicleTelemetry> findByVehicle_VehicleIdAndTsBetween(
        String vehicleId,
        LocalDateTime start,
        LocalDateTime end
    );

    // 특정 차량의 최신 텔레메트리 조회
    @Query("SELECT t FROM VehicleTelemetry t WHERE t.vehicle.vehicleId = :vehicleId ORDER BY t.ts DESC LIMIT 1")
    Optional<VehicleTelemetry> findLatestByVehicleId(@Param("vehicleId") String vehicleId);

    // 시동이 켜진 차량 조회
    @Query("SELECT DISTINCT t.vehicle.vehicleId FROM VehicleTelemetry t WHERE t.ignition = true AND t.ts > :since")
    List<String> findVehicleIdsWithIgnitionOn(@Param("since") LocalDateTime since);

    // 특정 속도 이상인 텔레메트리 조회 (과속 감지)
    @Query("SELECT t FROM VehicleTelemetry t WHERE t.vehicle.vehicleId = :vehicleId AND t.speed > :maxSpeed AND t.ts BETWEEN :start AND :end")
    List<VehicleTelemetry> findSpeedingRecords(
        @Param("vehicleId") String vehicleId,
        @Param("maxSpeed") Double maxSpeed,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    // 오래된 텔레메트리 데이터 삭제용 (특정 날짜 이전 데이터)
    void deleteByTsBefore(LocalDateTime cutoffDate);
}
