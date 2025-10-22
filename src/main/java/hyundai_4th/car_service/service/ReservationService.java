package hyundai_4th.car_service.service;

import hyundai_4th.car_service.model.dto.ReservationRequest;
import hyundai_4th.car_service.model.dto.ReservationResponse;
import hyundai_4th.car_service.model.entity.Reservation;
import hyundai_4th.car_service.model.entity.Location;
import hyundai_4th.car_service.model.entity.User;

import hyundai_4th.car_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import hyundai_4th.car_service.model.entity.Vehicle;


/**
 * 예약 관리 Service
 * - 예약 생성, 조회, 취소
 * - 예약 충돌 검증
 */
@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private LocationRepository locationRepository;

    /**
     * 예약 생성
     * 1. 사용자, 차량, 위치 존재 확인
     * 2. 예약 충돌 확인
     * 3. 차량 상태 확인
     * 4. 예약 생성 및 저장
     */
    public ReservationResponse createReservation(ReservationRequest request) {
        // 1. 사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        // 2. 차량 조회
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("차량을 찾을 수 없습니다: " + request.getVehicleId()));

        // 3. 픽업 위치 조회
        Location pickupLocation = locationRepository.findById(request.getPickupLocationId())
                .orElseThrow(() -> new RuntimeException("픽업 위치를 찾을 수 없습니다: " + request.getPickupLocationId()));

        // 4. 반납 위치 조회
        Location dropoffLocation = locationRepository.findById(request.getDropoffLocationId())
                .orElseThrow(() -> new RuntimeException("반납 위치를 찾을 수 없습니다: " + request.getDropoffLocationId()));

        // 5. 차량 상태 확인 (대여 가능한 상태인지)
        if (!"available".equals(vehicle.getStatus())) {
            throw new RuntimeException("현재 대여 불가능한 차량입니다. 상태: " + vehicle.getStatus());
        }

        // 6. 예약 시간 유효성 검증
        if (request.getStartAt().isAfter(request.getEndAt())) {
            throw new RuntimeException("시작 시각이 종료 시각보다 늦을 수 없습니다.");
        }

        if (request.getStartAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("과거 시각으로 예약할 수 없습니다.");
        }

        // 7. 예약 충돌 확인
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                vehicle.getVehicleId(),
                request.getStartAt(),
                request.getEndAt()
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("해당 기간에 이미 예약이 존재합니다.");
        }

        // 8. 예약 생성
        Reservation reservation = new Reservation(
                user,
                vehicle,
                pickupLocation,
                dropoffLocation,
                request.getStartAt(),
                request.getEndAt()
        );

        // 9. 저장
        Reservation savedReservation = reservationRepository.save(reservation);

        // 10. 차량 상태를 RESERVED로 변경 (선택사항)
        // vehicle.setStatus("reserved");
        // vehicleRepository.save(vehicle);

        // 11. DTO로 변환하여 반환
        return new ReservationResponse(savedReservation);
    }

    /**
     * 예약 조회 (ID로)
     */
    @Transactional(readOnly = true)
    public ReservationResponse getReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다: " + reservationId));

        return new ReservationResponse(reservation);
    }

    /**
     * 사용자의 모든 예약 조회
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getUserReservations(String userId) {
        List<Reservation> reservations = reservationRepository.findByUser_UserId(userId);

        return reservations.stream()
                .map(ReservationResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 특정 상태 예약 조회
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getUserReservationsByStatus(String userId, Reservation.ReservationStatus status) {
        List<Reservation> reservations = reservationRepository.findByUser_UserIdAndStatus(userId, status);

        return reservations.stream()
                .map(ReservationResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 예약 취소
     */
    public ReservationResponse cancelReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다: " + reservationId));

        // 이미 취소되었거나 전환된 예약은 취소 불가
        if (reservation.getStatus() != Reservation.ReservationStatus.BOOKED) {
            throw new RuntimeException("취소할 수 없는 예약 상태입니다: " + reservation.getStatus());
        }

        // 상태를 CANCELLED로 변경
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        Reservation cancelledReservation = reservationRepository.save(reservation);

        // 차량 상태를 AVAILABLE로 변경 (선택사항)
        // Vehicle vehicle = reservation.getVehicle();
        // vehicle.setStatus("available");
        // vehicleRepository.save(vehicle);

        return new ReservationResponse(cancelledReservation);
    }

    /**
     * 만료된 예약 정리 (스케줄러에서 주기적으로 호출)
     */
    public void cleanupExpiredReservations() {
        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations(LocalDateTime.now());

        for (Reservation reservation : expiredReservations) {
            reservation.setStatus(Reservation.ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
        }
    }
}