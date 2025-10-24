package com.security.test.controller;

import com.security.test.model.dto.PaymentResponse;
import com.security.test.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 결제 관리 Controller
 * 결제 조회, 통계 API 제공
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

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