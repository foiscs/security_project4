package hyundai_4th.car_service.model.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.Instant;

public class RentalEntity {

    @Entity
    @Table(name = "rentals")
    @Getter @Setter
    public static class Rental {
        @Id
        @Column(name = "rental_id", length = 36)
        private String rentalId;

        @Column(name = "reservation_id", length = 36, nullable = false)
        private String reservationId;

        @Column(name = "user_id", length = 36, nullable = false)
        private String userId;

        @Column(name = "vehicle_id", length = 36, nullable = false)
        private String vehicleId;

        @Column(name = "start_actual")
        private Instant startActual;

        @Column(name = "end_actual")
        private Instant endActual;

        @Column(name = "start_meter")
        private Integer startMeter;

        @Column(name = "end_meter")
        private Integer endMeter;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", length = 20, nullable = false)
        private Status status = Status.ongoing;

        public enum Status { ongoing, returned, no_show, cancelled }
    }

    @Entity
    @Table(name = "reservations")
    @Getter @Setter
    public static class Reservation {
        @Id
        @Column(name = "reservation_id", length = 36)
        private String reservationId;

        @Column(name = "user_id", length = 36)
        private String userId;

        @Column(name = "vehicle_id", length = 36)
        private String vehicleId;

        @Column(name = "status", length = 20)
        private String status;
    }

    @Entity
    @Table(name = "vehicles")
    @Getter @Setter
    public static class Vehicle {
        @Id
        @Column(name = "vehicle_id", length = 36)
        private String vehicleId;

        @Column(name = "vin", length = 64)
        private String vin;

        @Column(name = "plate", length = 32)
        private String plate;

        @Column(name = "status", length = 20)
        private String status;
    }
}