package com.security.test.service;

import com.security.test.model.dto.RentalDTO;
import com.security.test.model.entity.Rental;
import com.security.test.model.entity.Reservation;
import com.security.test.model.entity.User;
import com.security.test.model.entity.Vehicle;
import com.security.test.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository repo;
    private final PaymentService paymentService;

    @Transactional
    public RentalDTO.RentalResponse start(RentalDTO.RentalStartRequest req) {
        // 예약 조회/검증
        Reservation rsv = repo.findReservation(req.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("reservation not found"));

        // ✅ Reservation은 userId/vehicleId 필드가 아니라 연관 객체를 가짐
        if (!rsv.getUser().getUserId().equals(req.getUserId()) ||
                !rsv.getVehicle().getVehicleId().equals(req.getVehicleId())) {
            throw new IllegalStateException("mismatched triplet");
        }

        // ✅ status는 Enum이므로 문자열 비교가 아닌 Enum 비교
        if (rsv.getStatus() != Reservation.ReservationStatus.BOOKED) {
            throw new IllegalStateException("invalid reservation status");
        }

        // 차량 검증
        Vehicle v = repo.findVehicle(req.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("vehicle not found"));
        if (!( "reserved".equalsIgnoreCase(v.getStatus()) || "available".equalsIgnoreCase(v.getStatus()) )) {
            throw new IllegalStateException("vehicle not rentable");
        }

        // 대여 생성
        Rental rental = new Rental();
        rental.setRentalId(UUID.randomUUID().toString()); // @GeneratedValue(uuid2)를 쓰는 쪽으로 통일해도 됨
        rental.setReservation(rsv);
        rental.setVehicle(v);

        // user는 조회 없이 PK만 세팅해 연관 연결(지연로딩)
        User u = new User();
        u.setUserId(req.getUserId());
        rental.setUser(u);

        rental.setStartMeter(req.getStartMeter());
        rental.setStartActual(toLdt(req.getStartActualEpochMs()));
        rental.setStatus(Rental.Status.ongoing);

        repo.saveRental(rental);

        // 상태 전이 (주의: Reservation.status가 Enum이면 레포도 Enum 파라미터로 받는게 정석)
        repo.updateReservationStatus(req.getReservationId(), "converted");
        repo.updateVehicleStatus(req.getVehicleId(), "rented");

        return RentalDTO.RentalResponse.of(rental);
    }

    @Transactional
    public RentalDTO.RentalResponse finish(String rentalId, RentalDTO.RentalReturnRequest req) {
        Rental rental = repo.findRental(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("rental not found"));

        if (rental.getStatus() != Rental.Status.ongoing)
            throw new IllegalStateException("already finished");
        if (req.getEndMeter() < rental.getStartMeter())
            throw new IllegalArgumentException("endMeter must be >= startMeter");

        rental.setEndMeter(req.getEndMeter());
        rental.setEndActual(toLdt(req.getEndActualEpochMs()));
        rental.setStatus(Rental.Status.returned);
        repo.saveRental(rental);

        // 차량 상태 복구
        String vehicleId = (rental.getVehicle() != null)
                ? rental.getVehicle().getVehicleId()
                : rental.getVehicleId(); // 편의 getter가 있으면 사용
        repo.updateVehicleStatus(vehicleId, "available");

        // 반납 완료 후 자동 결제
        paymentService.createPaymentForRental(rental);

        return RentalDTO.RentalResponse.of(rental);
    }

    private static LocalDateTime toLdt(Long epochMs) {
        if (epochMs == null) return null;
        Instant ins = Instant.ofEpochMilli(epochMs);
        return LocalDateTime.ofInstant(ins, ZoneId.systemDefault());
    }
}
