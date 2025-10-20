package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.Rental;
import hyundai_4th.car_service.model.entity.Rental.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {

    // 특정 사용자의 대여 목록 조회
    List<Rental> findByUser_UserId(String userId);

    // 특정 사용자의 특정 상태 대여 조회
    List<Rental> findByUser_UserIdAndStatus(String userId, RentalStatus status);

    // 특정 사용자의 현재 진행 중인 대여 조회
    @Query("SELECT r FROM Rental r WHERE r.user.userId = :userId AND r.status = 'ONGOING'")
    List<Rental> findOngoingRentalsByUserId(@Param("userId") String userId);

    // 특정 차량의 대여 목록 조회
    List<Rental> findByVehicle_VehicleId(String vehicleId);

    // 특정 차량의 현재 진행 중인 대여 조회
    Optional<Rental> findByVehicle_VehicleIdAndStatus(String vehicleId, RentalStatus status);

    // 특정 예약의 대여 조회
    Optional<Rental> findByReservation_ReservationId(String reservationId);

    // 대여 상태로 조회
    List<Rental> findByStatus(RentalStatus status);

    // 특정 기간 동안의 대여 조회
    List<Rental> findByStartActualBetween(LocalDateTime start, LocalDateTime end);

    // 특정 사용자의 대여 이력 조회 (완료된 대여만)
    @Query("SELECT r FROM Rental r WHERE r.user.userId = :userId AND r.status = 'RETURNED' ORDER BY r.startActual DESC")
    List<Rental> findRentalHistoryByUserId(@Param("userId") String userId);

    // 반납 예정일이 지났는데 아직 반납 안 된 대여 조회 (연체)
    @Query("SELECT r FROM Rental r WHERE r.reservation.endAt < :currentTime AND r.status = 'ONGOING'")
    List<Rental> findOverdueRentals(@Param("currentTime") LocalDateTime currentTime);
}
