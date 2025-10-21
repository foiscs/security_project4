package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    Optional<Vehicle> findByPlate(String plate);
    Optional<Vehicle> findByVin(String vin);
    List<Vehicle> findByStatus(String status);
    List<Vehicle> findByBrand(String brand);
    List<Vehicle> findByModel(String model);
    List<Vehicle> findByBrandAndModel(String brand, String model);
    List<Vehicle> findByYearBetween(Integer startYear, Integer endYear);

    // 🔁 여기로 교체 (프로퍼티 경로 기반 파생 쿼리)
    List<Vehicle> findByCurrentLocationId(String locationId);
    List<Vehicle> findByCurrentLocationIdAndStatus(String locationId, String status);
}
