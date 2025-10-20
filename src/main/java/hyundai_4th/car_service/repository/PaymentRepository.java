package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.Payment;
import hyundai_4th.car_service.model.entity.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    // PG 거래 ID로 결제 조회
    Optional<Payment> findByPgTxId(String pgTxId);

    // 특정 대여의 결제 목록 조회
    List<Payment> findByRental_RentalId(String rentalId);

    // 특정 사용자의 결제 목록 조회
    List<Payment> findByUser_UserId(String userId);

    // 특정 사용자의 특정 상태 결제 조회
    List<Payment> findByUser_UserIdAndStatus(String userId, PaymentStatus status);

    // 결제 상태로 조회
    List<Payment> findByStatus(PaymentStatus status);

    // 결제 수단으로 조회
    List<Payment> findByMethod(String method);

    // 특정 기간의 결제 조회
    List<Payment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 특정 사용자의 결제 총액 계산
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.user.userId = :userId AND p.status = 'CAPTURED'")
    Double calculateTotalAmountByUserId(@Param("userId") String userId);

    // 특정 기간의 결제 총액 계산
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.createdAt BETWEEN :start AND :end AND p.status = 'CAPTURED'")
    Double calculateTotalAmountByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 통화별 결제 조회
    List<Payment> findByCurrency(String currency);
}
