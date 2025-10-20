package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.Reservation;
import hyundai_4th.car_service.model.entity.Reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    // 특정 사용자의 예약 목록 조회
    List<Reservation> findByUser_UserId(String userId);

    // 특정 사용자의 특정 상태 예약 조회
    List<Reservation> findByUser_UserIdAndStatus(String userId, ReservationStatus status);

    // 특정 차량의 예약 목록 조회
    List<Reservation> findByVehicle_VehicleId(String vehicleId);

    // 특정 차량의 특정 기간 예약 조회 (예약 충돌 확인용)
    @Query("SELECT r FROM Reservation r WHERE r.vehicle.vehicleId = :vehicleId " +
           "AND r.status IN ('BOOKED', 'CONVERTED') " +
           "AND ((r.startAt <= :endAt AND r.endAt >= :startAt))")
    List<Reservation> findConflictingReservations(
        @Param("vehicleId") String vehicleId,
        @Param("startAt") LocalDateTime startAt,
        @Param("endAt") LocalDateTime endAt
    );

    // 특정 기간의 예약 조회
    List<Reservation> findByStartAtBetween(LocalDateTime start, LocalDateTime end);

    // 예약 상태로 조회
    List<Reservation> findByStatus(ReservationStatus status);

    // 만료된 예약 조회 (현재 시각보다 종료 시각이 이전이고 상태가 BOOKED인 예약)
    @Query("SELECT r FROM Reservation r WHERE r.endAt < :currentTime AND r.status = 'BOOKED'")
    List<Reservation> findExpiredReservations(@Param("currentTime") LocalDateTime currentTime);
}
