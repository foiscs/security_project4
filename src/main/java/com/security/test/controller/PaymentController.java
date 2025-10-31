package com.security.test.controller;

import com.security.test.model.dto.CreatePaymentRequest;
import com.security.test.model.dto.PaymentResponse;
import com.security.test.model.entity.Rental;
import com.security.test.repository.RentalRepository;
import com.security.test.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 결제 관리 Controller
 * 결제 생성, 조회, 통계 API 제공
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RentalRepository rentalRepository;

    /**
     * 결제 생성 (대여에 대한 결제 처리)
     * POST /api/payments
     *
     * 요청 예시:
     * {
     *   "rentalId": "rental-uuid-123",
     *   "amount": 100000,
     *   "currency": "KRW",
     *   "paymentMethod": "card",
     *   "customerName": "홍길동",
     *   "customerEmail": "hong@example.com"
     * }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody CreatePaymentRequest request) {
        logger.info("=== 결제 생성 API 호출 ===");
        logger.info("대여 ID: {}, 금액: {} {}", request.getRentalId(), request.getAmount(), request.getCurrency());

        try {
            // 1. 대여 정보 조회
            Rental rental = rentalRepository.findRental(request.getRentalId())
                    .orElseThrow(() -> new RuntimeException("대여 정보를 찾을 수 없습니다: " + request.getRentalId()));

            logger.info("대여 정보 조회 성공: 사용자 ID {}", rental.getUser().getUserId());

            // 2. 결제 처리 (PG API 호출 포함)
            PaymentResponse paymentResponse = paymentService.createPaymentForRental(rental);

            logger.info("결제 생성 성공: Payment ID {}", paymentResponse.getPaymentId());

            // 3. 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "결제가 성공적으로 처리되었습니다.");
            response.put("payment", paymentResponse);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            logger.error("결제 생성 실패: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "결제 처리 중 오류가 발생했습니다.");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * 결제 조회 (ID로)
     * GET /api/payments/{paymentId}
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentId) {
        try {
            PaymentResponse response = paymentService.getPayment(paymentId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * 사용자의 모든 결제 내역 조회
     * GET /api/payments/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getUserPayments(@PathVariable String userId) {
        List<PaymentResponse> responses = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 대여의 결제 내역 조회
     * GET /api/payments/rental/{rentalId}
     */
    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<PaymentResponse>> getRentalPayments(@PathVariable String rentalId) {
        List<PaymentResponse> responses = paymentService.getRentalPayments(rentalId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 사용자의 총 결제 금액 조회
     * GET /api/payments/user/{userId}/total
     */
    @GetMapping("/user/{userId}/total")
    public ResponseEntity<BigDecimal> getUserTotalAmount(@PathVariable String userId) {
        BigDecimal total = paymentService.getUserTotalAmount(userId);
        return ResponseEntity.ok(total);
    }

    /**
     * 특정 기간의 총 매출 조회 (관리자용)
     * GET /api/payments/total?start={start}&end={end}
     * 예: GET /api/payments/total?start=2025-10-01T00:00:00&end=2025-10-31T23:59:59
     */
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalAmountByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        BigDecimal total = paymentService.getTotalAmountByPeriod(start, end);
        return ResponseEntity.ok(total);
    }
}