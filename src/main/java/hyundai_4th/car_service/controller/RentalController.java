package hyundai_4th.car_service.controller;

import hyundai_4th.car_service.model.dto.RentalResponse;
import hyundai_4th.car_service.model.dto.RentalReturnRequest;
import hyundai_4th.car_service.model.dto.RentalStartRequest;
import hyundai_4th.car_service.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 대여 관리 Controller
 * 대여 시작, 반납, 이력 조회 API 제공
 */
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    /**
     * 대여 시작 (차량 픽업)
     * POST /api/rentals/start
     */
    @PostMapping("/start")
    public ResponseEntity<RentalResponse> startRental(@RequestBody RentalStartRequest request) {
        try {
            RentalResponse response = rentalService.startRental(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 차량 반납
     * POST /api/rentals/return
     */
    @PostMapping("/return")
    public ResponseEntity<RentalResponse> returnRental(@RequestBody RentalReturnRequest request) {
        try {
            RentalResponse response = rentalService.returnRental(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 대여 조회 (ID로)
     * GET /api/rentals/{rentalId}
     */
    @GetMapping("/{rentalId}")
    public ResponseEntity<RentalResponse> getRental(@PathVariable String rentalId) {
        try {
            RentalResponse response = rentalService.getRental(rentalId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * 사용자의 현재 진행 중인 대여 조회
     * GET /api/rentals/user/{userId}/ongoing
     */
    @GetMapping("/user/{userId}/ongoing")
    public ResponseEntity<List<RentalResponse>> getOngoingRentals(@PathVariable String userId) {
        List<RentalResponse> responses = rentalService.getOngoingRentals(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 사용자의 대여 이력 조회 (완료된 대여만)
     * GET /api/rentals/user/{userId}/history
     */
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<RentalResponse>> getRentalHistory(@PathVariable String userId) {
        List<RentalResponse> responses = rentalService.getRentalHistory(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 연체된 대여 조회 (관리자용)
     * GET /api/rentals/overdue
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<RentalResponse>> getOverdueRentals() {
        List<RentalResponse> responses = rentalService.processOverdueRentals();
        return ResponseEntity.ok(responses);
    }
}
