package com.security.test.service;

import com.security.test.model.dto.PaymentResponse;
import com.security.test.model.entity.Payment;
import com.security.test.model.entity.Rental;
import com.security.test.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 결제 관리 Service
 * - 대여료 자동 계산 및 결제 생성
 * - 결제 내역 조회
 */
@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // 1일 기본 대여료 (실제로는 차량마다 다를 수 있음)
    private static final BigDecimal DAILY_RATE = new BigDecimal("50000");

    // 시간당 대여료
    private static final BigDecimal HOURLY_RATE = new BigDecimal("5000");

    // 연체료 (1일당)
    private static final BigDecimal LATE_FEE_PER_DAY = new BigDecimal("10000");

    /**
     * 대여에 대한 결제 생성 (차량 반납 시 자동 호출)
     * 1. 대여 기간 계산
     * 2. 기본 요금 계산
     * 3. 연체료 계산 (있는 경우)
     * 4. Payment 생성 및 저장
     */
    public PaymentResponse createPaymentForRental(Rental rental) {
        // 1. 대여 기간 계산
        LocalDateTime startTime = rental.getStartActual();
        LocalDateTime endTime = rental.getEndActual();

        if (startTime == null || endTime == null) {
            throw new RuntimeException("대여 시작 또는 종료 시각이 없습니다.");
        }

        Duration duration = Duration.between(startTime, endTime);
        long hours = duration.toHours();
        long days = hours / 24;
        long remainingHours = hours % 24;

        // 2. 기본 요금 계산
        BigDecimal baseAmount = DAILY_RATE.multiply(BigDecimal.valueOf(days))
                .add(HOURLY_RATE.multiply(BigDecimal.valueOf(remainingHours)));

        // 3. 연체료 계산
        BigDecimal lateFee = BigDecimal.ZERO;
        LocalDateTime expectedEndTime = rental.getReservation().getEndAt();
        if (endTime.isAfter(expectedEndTime)) {
            // 연체 발생
            Duration lateDuration = Duration.between(expectedEndTime, endTime);
            long lateDays = lateDuration.toDays() + 1; // 하루라도 넘으면 1일 연체료 부과
            lateFee = LATE_FEE_PER_DAY.multiply(BigDecimal.valueOf(lateDays));
        }

        // 4. 총 금액
        BigDecimal totalAmount = baseAmount.add(lateFee);

        // 5. Payment 생성
        Payment payment = new Payment(
                rental,
                rental.getUser(),
                totalAmount,
                "KRW",
                "card"  // 기본값: 카드 결제 (실제로는 사용자 선택 받아야 함)
        );

        // 6. 결제 상태를 CAPTURED로 설정 (실제로는 PG사 연동 후 설정)
        payment.setStatus(Payment.PaymentStatus.CAPTURED);

        // 7. 저장
        Payment savedPayment = paymentRepository.save(payment);

        // 8. DTO로 변환하여 반환
        return new PaymentResponse(savedPayment);
    }

    /**
     * 결제 조회 (ID로)
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다: " + paymentId));

        return new PaymentResponse(payment);
    }

    /**
     * 사용자의 모든 결제 내역 조회
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getUserPayments(String userId) {
        List<Payment> payments = paymentRepository.findByUser_UserId(userId);

        return payments.stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 특정 대여의 결제 내역 조회
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getRentalPayments(String rentalId) {
        List<Payment> payments = paymentRepository.findByRental_RentalId(rentalId);

        return payments.stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 총 결제 금액 조회
     */
    @Transactional(readOnly = true)
    public BigDecimal getUserTotalAmount(String userId) {
        Double total = paymentRepository.calculateTotalAmountByUserId(userId);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    /**
     * 특정 기간의 총 매출 조회
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByPeriod(LocalDateTime start, LocalDateTime end) {
        Double total = paymentRepository.calculateTotalAmountByPeriod(start, end);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }
}