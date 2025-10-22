package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.SearchVehicle;
import hyundai_4th.car_service.repository.projection.SearchVehicleProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchRepository extends JpaRepository<SearchVehicle, String> {

    @Query(value =
            "SELECT " +
                    "  v.vehicle_id AS vehicleId, " +
                    "  v.vin AS vin, " +
                    "  v.plate AS plate, " +
                    "  v.brand AS brand, " +
                    "  v.model AS model, " +
                    "  v.year AS yearValue, " +
                    "  v.status AS status, " +
                    "  v.current_location_id AS currentLocationId " +
                    "FROM vehicles v " +
                    "WHERE v.status = 'available' " +
                    "  AND v.current_location_id = :pickupLocationId " +
                    "  AND NOT EXISTS ( " +
                    "        SELECT 1 FROM reservations r " +
                    "        WHERE r.vehicle_id = v.vehicle_id " +
                    "          AND r.status IN ('booked','converted') " +
                    "          AND r.start_at < CAST(:availableTo AS DATETIME) " +
                    "          AND r.end_at > CAST(:availableFrom AS DATETIME) " +
                    "      ) " +
                    "ORDER BY v.year DESC, v.brand ASC, v.model ASC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<SearchVehicleProjection> findAvailableVehicles(
            @Param("pickupLocationId") String pickupLocationId,
            @Param("availableFrom") String availableFromUtc,
            @Param("availableTo") String availableToUtc,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
}