package hyundai_4th.car_service.service;

import hyundai_4th.car_service.model.dto.RentalDTO;
import hyundai_4th.car_service.model.entity.RentalEntity.Rental;
import hyundai_4th.car_service.model.entity.RentalEntity.Reservation;
import hyundai_4th.car_service.model.entity.RentalEntity.Vehicle;
import hyundai_4th.car_service.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository repo;

    @Transactional
    public RentalDTO.RentalResponse start(RentalDTO.RentalStartRequest req) {
        Reservation rsv = repo.findReservation(req.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("reservation not found"));
        if (!rsv.getUserId().equals(req.getUserId()) || !rsv.getVehicleId().equals(req.getVehicleId()))
            throw new IllegalStateException("mismatched triplet");
        if (!"booked".equalsIgnoreCase(rsv.getStatus()))
            throw new IllegalStateException("invalid reservation status");

        Vehicle v = repo.findVehicle(req.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("vehicle not found"));
        if (!( "reserved".equalsIgnoreCase(v.getStatus()) || "available".equalsIgnoreCase(v.getStatus()) ))
            throw new IllegalStateException("vehicle not rentable");

        Rental rental = new Rental();
        rental.setRentalId(UUID.randomUUID().toString());
        rental.setReservationId(req.getReservationId());
        rental.setUserId(req.getUserId());
        rental.setVehicleId(req.getVehicleId());
        rental.setStartMeter(req.getStartMeter());
        rental.setStartActual(Instant.ofEpochMilli(req.getStartActualEpochMs()));
        rental.setStatus(Rental.Status.ongoing);
        repo.saveRental(rental);

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
        rental.setEndActual(Instant.ofEpochMilli(req.getEndActualEpochMs()));
        rental.setStatus(Rental.Status.returned);
        repo.saveRental(rental);

        repo.updateVehicleStatus(rental.getVehicleId(), "available");
        return RentalDTO.RentalResponse.of(rental);
    }
}
