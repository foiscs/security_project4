package com.security.test.repository;

import com.security.test.model.entity.Rental;
import com.security.test.model.entity.Reservation;
import com.security.test.model.entity.Vehicle;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class RentalRepository {

    @PersistenceContext
    private EntityManager em;

    /** 신규/수정 저장 */
    public void saveRental(Rental r) {
        em.merge(r);
    }

    /** 대여 단건 조회 */
    public Optional<Rental> findRental(String id) {
        return Optional.ofNullable(em.find(Rental.class, id));
    }

    /** 예약 단건 조회 */
    public Optional<Reservation> findReservation(String id) {
        return Optional.ofNullable(em.find(Reservation.class, id));
    }

    /** 차량 단건 조회 */
    public Optional<Vehicle> findVehicle(String id) {
        return Optional.ofNullable(em.find(Vehicle.class, id));
    }

    /** 예약 상태 변경 */
    public void updateReservationStatus(String reservationId, String status) {
        em.createQuery("UPDATE Reservation r SET r.status = :s WHERE r.reservationId = :id")
                .setParameter("s", status)
                .setParameter("id", reservationId)
                .executeUpdate();
    }

    /** 차량 상태 변경 */
    public void updateVehicleStatus(String vehicleId, String status) {
        em.createQuery("UPDATE Vehicle v SET v.status = :s WHERE v.vehicleId = :id")
                .setParameter("s", status)
                .setParameter("id", vehicleId)
                .executeUpdate();
    }
}
