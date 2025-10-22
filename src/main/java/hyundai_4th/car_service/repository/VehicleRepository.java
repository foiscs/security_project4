package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String>, JpaSpecificationExecutor<Vehicle> {

    // 단건 조회
    Optional<Vehicle> findByPlate(String plate);
    Optional<Vehicle> findByVin(String vin);

    // 기본 필터
    List<Vehicle> findByStatus(String status);
    List<Vehicle> findByBrand(String brand);
    List<Vehicle> findByModel(String model);
    List<Vehicle> findByBrandAndModel(String brand, String model);

    // 연식 범위 (엔티티 year가 int라서 int 사용 권장)
    List<Vehicle> findByYearBetween(int startYear, int endYear);

    // 🔧 JPQL 수정: 연관경로 → 평면 필드(currentLocationId)
    @Query("SELECT v FROM Vehicle v WHERE v.currentLocationId = :locationId")
    List<Vehicle> findByLocationId(@Param("locationId") String locationId);

    @Query("SELECT v FROM Vehicle v WHERE v.currentLocationId = :locationId AND v.status = :status")
    List<Vehicle> findByLocationIdAndStatus(@Param("locationId") String locationId,
                                            @Param("status") String status);
}
