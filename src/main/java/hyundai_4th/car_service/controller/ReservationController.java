package hyundai_4th.car_service.controller;

import hyundai_4th.car_service.model.dto.ReservationRequest;
import hyundai_4th.car_service.model.dto.ReservationResponse;
import hyundai_4th.car_service.model.entity.Reservation;
import hyundai_4th.car_service.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 예약 관리 Controller
 * 예약 생성, 조회, 취소 API 제공
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * 예약 생성
     * POST /api/reservations
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        try {
            ReservationResponse response = reservationService.createReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            // 에러 처리 (실제로는 GlobalExceptionHandler 사용 권장)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 예약 조회 (ID로)
     * GET /api/reservations/{reservationId}
     */
    @GetMapping("/{reservationId}")
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
    public ResponseEntity<List<ReservationResponse>> getUserReservations(@PathVariable String userId) {
        List<ReservationResponse> responses = reservationService.getUserReservations(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 사용자의 특정 상태 예약 조회
     * GET /api/reservations/user/{userId}/status/{status}
     */
    @GetMapping("/user/{userId}/status/{status}")
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
     * 예약 취소
     * PUT /api/reservations/{reservationId}/cancel
     */
    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable String reservationId) {
        try {
            ReservationResponse response = reservationService.cancelReservation(reservationId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}