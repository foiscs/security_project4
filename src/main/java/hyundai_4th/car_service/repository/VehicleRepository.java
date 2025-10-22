package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // ✅ 추가
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// ✅ 동적 조건 + 페이징 지원을 위해 JpaSpecificationExecutor 추가
public interface VehicleRepository extends JpaRepository<Vehicle, String>, JpaSpecificationExecutor<Vehicle> {

    // 번호판으로 차량 조회
    Optional<Vehicle> findByPlate(String plate);

    // 차대번호(VIN)로 차량 조회
    Optional<Vehicle> findByVin(String vin);

    // 상태로 차량 목록 조회 (대여 가능한 차량 찾기)
    List<Vehicle> findByStatus(String status);

    // 브랜드로 차량 검색
    List<Vehicle> findByBrand(String brand);

    // 모델로 차량 검색
    List<Vehicle> findByModel(String model);

    // 브랜드와 모델로 차량 검색
    List<Vehicle> findByBrandAndModel(String brand, String model);

    // ✅ 엔티티가 연관관계일 때(@ManyToOne) 사용하는 JPQL
    // Vehicle.currentLocation (Location 엔티티) 안에 locationId 필드가 있다고 가정
    @Query("SELECT v FROM Vehicle v WHERE v.currentLocation.locationId = :locationId")
    List<Vehicle> findByLocationId(@Param("locationId") String locationId);

    @Query("SELECT v FROM Vehicle v WHERE v.currentLocation.locationId = :locationId AND v.status = :status")
    List<Vehicle> findByLocationIdAndStatus(@Param("locationId") String locationId, @Param("status") String status);

    // 연식 범위로 차량 검색
    List<Vehicle> findByYearBetween(Integer startYear, Integer endYear);
}
