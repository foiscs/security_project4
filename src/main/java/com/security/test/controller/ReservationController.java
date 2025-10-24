package com.security.test.controller;

import com.security.test.model.dto.ReservationRequest;
import com.security.test.model.dto.ReservationResponse;
import com.security.test.model.entity.Reservation;
import com.security.test.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ⚠️ VULNERABLE: Spring4Shell (CVE-2022-22965) 취약점 존재
 * 예약 관리 Controller
 * @ModelAttribute를 사용하는 엔드포인트에서 취약점 발생
 */
@Controller
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * VULNERABLE: 예약 생성 폼 페이지
     * @ModelAttribute 사용 - Spring4Shell 공격 가능
     */
    @GetMapping("/new")
    public String reservationForm(@ModelAttribute("reservation") ReservationRequest request, Model model) {
        model.addAttribute("reservation", request);
        return "reservation-form";
    }

    /**
     * VULNERABLE: Form 기반 예약 생성
     * @ModelAttribute 사용 - Spring4Shell 공격 가능
     */
    @PostMapping(value = "/new", consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public ResponseEntity<ReservationResponse> createReservationForm(@ModelAttribute ReservationRequest request) {
        try {
            ReservationResponse response = reservationService.createReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 예약 생성 (JSON)
     * POST /api/reservations
     */
    @PostMapping(consumes = "application/json")
    @ResponseBody
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        try {
            ReservationResponse response = reservationService.createReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 예약 조회 (ID로)
     * GET /api/reservations/{reservationId}
     */
    @GetMapping("/{reservationId}")
    @ResponseBody
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable String reservationId) {
        try {
            ReservationResponse response = reservationService.getReservation(reservationId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * 사용자의 모든 예약 조회
     * GET /api/reservations/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @ResponseBody
    public ResponseEntity<List<ReservationResponse>> getUserReservations(@PathVariable String userId) {
        List<ReservationResponse> responses = reservationService.getUserReservations(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 사용자의 특정 상태 예약 조회
     * GET /api/reservations/user/{userId}/status/{status}
     */
    @GetMapping("/user/{userId}/status/{status}")
    @ResponseBody
    public ResponseEntity<List<ReservationResponse>> getUserReservationsByStatus(
            @PathVariable String userId,
            @PathVariable String status) {
        try {
            Reservation.ReservationStatus reservationStatus = Reservation.ReservationStatus.valueOf(status.toUpperCase());
            List<ReservationResponse> responses = reservationService.getUserReservationsByStatus(userId, reservationStatus);
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * VULNERABLE: 예약 취소 (Form 기반)
     * @ModelAttribute 사용 - Spring4Shell 공격 가능
     */
    @PutMapping(value = "/{reservationId}/cancel", consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public ResponseEntity<ReservationResponse> cancelReservationForm(
            @PathVariable String reservationId,
            @ModelAttribute ReservationRequest request) {
        try {
            ReservationResponse response = reservationService.cancelReservation(reservationId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 예약 취소 (JSON)
     * PUT /api/reservations/{reservationId}/cancel
     */
    @PutMapping(value = "/{reservationId}/cancel", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable String reservationId) {
        try {
            ReservationResponse response = reservationService.cancelReservation(reservationId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}