package hyundai_4th.car_service.service;

import hyundai_4th.car_service.model.dto.*;
import hyundai_4th.car_service.model.entity.*;
import hyundai_4th.car_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 대여 관리 Service
 * - 대여 시작 (예약 → 대여 전환)
 * - 차량 반납
 * - 대여 이력 조회
 */
@Service
@Transactional
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PaymentService paymentService;

    /**
     * 대여 시작 (차량 픽업)
     * 1. 예약 조회 및 검증
     * 2. Rental 생성
     * 3. 예약 상태를 CONVERTED로 변경
     * 4. 차량 상태를 RENTED로 변경
     */
    public RentalResponse startRental(RentalStartRequest request) {
        // 1. 예약 조회
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다: " + request.getReservationId()));

        // 2. 예약 상태 확인
        if (reservation.getStatus() != Reservation.ReservationStatus.BOOKED) {
            throw new RuntimeException("대여로 전환할 수 없는 예약 상태입니다: " + reservation.getStatus());
        }

        // 3. 예약 시작 시간 확인 (예약 시작 시간이 되었는지)
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(reservation.getStartAt().minusHours(1))) {
            // 예약 시작 1시간 전부터 픽업 가능
            throw new RuntimeException("아직 픽업 가능 시간이 아닙니다.");
        }

        // 4. 차량 상태 확인
        Vehicle vehicle = reservation.getVehicle();
        if (!"available".equals(vehicle.getStatus()) && !"reserved".equals(vehicle.getStatus())) {
            throw new RuntimeException("현재 대여 불가능한 차량입니다: " + vehicle.getStatus());
        }

        // 5. Rental 생성
        Rental rental = new Rental(
                reservation,
                reservation.getUser(),
                vehicle
        );
        rental.setStartActual(now);
        rental.setStartMeter(request.getStartMeter());
        rental.setStatus(Rental.RentalStatus.ONGOING);

        // 6. 저장
        Rental savedRental = rentalRepository.save(rental);

        // 7. 예약 상태 변경 (BOOKED → CONVERTED)
        reservation.setStatus(Reservation.ReservationStatus.CONVERTED);
        reservationRepository.save(reservation);

        // 8. 차량 상태 변경 (AVAILABLE → RENTED)
        vehicle.setStatus("rented");
        vehicleRepository.save(vehicle);

        // 9. DTO로 변환하여 반환
        return new RentalResponse(savedRental);
    }

    /**
     * 차량 반납
     * 1. Rental 조회 및 검증
     * 2. 반납 정보 업데이트
     * 3. 차량 상태를 AVAILABLE로 변경
     * 4. 결제 처리
     */
    public RentalResponse returnRental(RentalReturnRequest request) {
        // 1. Rental 조회
        Rental rental = rentalRepository.findById(request.getRentalId())
                .orElseThrow(() -> new RuntimeException("대여 정보를 찾을 수 없습니다: " + request.getRentalId()));

        // 2. 상태 확인
        if (rental.getStatus() != Rental.RentalStatus.ONGOING) {
            throw new RuntimeException("반납할 수 없는 대여 상태입니다: " + rental.getStatus());
        }

        // 3. 반납 정보 업데이트
        LocalDateTime now = LocalDateTime.now();
        rental.setEndActual(now);
        rental.setEndMeter(request.getEndMeter());
        rental.setStatus(Rental.RentalStatus.RETURNED);

        // 4. 저장
        Rental returnedRental = rentalRepository.save(rental);

        // 5. 차량 상태 변경 (RENTED → AVAILABLE)
        Vehicle vehicle = rental.getVehicle();
        vehicle.setStatus("available");
        vehicleRepository.save(vehicle);

        // 6. 결제 처리 (자동으로 결제 생성)
        paymentService.createPaymentForRental(rental);

        // 7. DTO로 변환하여 반환
        return new RentalResponse(returnedRental);
    }

    /**
     * 대여 조회 (ID로)
     */
    @Transactional(readOnly = true)
    public RentalResponse getRental(String rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("대여 정보를 찾을 수 없습니다: " + rentalId));

        return new RentalResponse(rental);
    }

    /**
     * 사용자의 현재 진행 중인 대여 조회
     */
    @Transactional(readOnly = true)
    public List<RentalResponse> getOngoingRentals(String userId) {
        List<Rental> rentals = rentalRepository.findOngoingRentalsByUserId(userId);

        return rentals.stream()
                .map(RentalResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 대여 이력 조회 (완료된 대여만)
     */
    @Transactional(readOnly = true)
    public List<RentalResponse> getRentalHistory(String userId) {
        List<Rental> rentals = rentalRepository.findRentalHistoryByUserId(userId);

        return rentals.stream()
                .map(RentalResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 연체된 대여 조회 및 처리 (스케줄러에서 주기적으로 호출)
     */
    public List<RentalResponse> processOverdueRentals() {
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(LocalDateTime.now());

        // 연체된 대여에 대해 알림 발송, 연체료 부과 등 처리
        for (Rental rental : overdueRentals) {
            // 여기서 연체 처리 로직 추가 가능
            // 예: 이메일 발송, 연체료 계산 등
            System.out.println("연체된 대여: " + rental.getRentalId());
        }

        return overdueRentals.stream()
                .map(RentalResponse::new)
                .collect(Collectors.toList());
    }
}
