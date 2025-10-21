package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.RentalEntity.Rental;
import hyundai_4th.car_service.model.entity.RentalEntity.Reservation;
import hyundai_4th.car_service.model.entity.RentalEntity.Vehicle;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class RentalRepository {

    @PersistenceContext
    private EntityManager em;

    public void saveRental(Rental r) { em.merge(r); }

    public Optional<Rental> findRental(String id) {
        return Optional.ofNullable(em.find(Rental.class, id));
    }

    public Optional<Reservation> findReservation(String id) {
        return Optional.ofNullable(em.find(Reservation.class, id));
    }

    public Optional<Vehicle> findVehicle(String id) {
        return Optional.ofNullable(em.find(Vehicle.class, id));
    }

    public void updateReservationStatus(String reservationId, String status) {
        em.createQuery("UPDATE Reservation r SET r.status = :s WHERE r.reservationId = :id")
                .setParameter("s", status)
                .setParameter("id", reservationId)
                .executeUpdate();
    }

    public void updateVehicleStatus(String vehicleId, String status) {
        em.createQuery("UPDATE Vehicle v SET v.status = :s WHERE v.vehicleId = :id")
                .setParameter("s", status)
                .setParameter("id", vehicleId)
                .executeUpdate();
    }
}
