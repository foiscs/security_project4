package com.security.test.model.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "rentals",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"rental_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_rent_user_start", columnList = "user_id, start_actual")
        }
)
public class Rental {

    @Id
    @GeneratedValue(generator = "uuid")                   // 앱에서 UUID 생성 (DB DEFAULT(UUID())와 중복되지 않게 한쪽만 사용 권장)
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "rental_id", length = 36, nullable = false)
    private String rentalId;

    // ===== FK 관계 (DB 컬럼: reservation_id / user_id / vehicle_id) =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, referencedColumnName = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, referencedColumnName = "vehicle_id")
    private Vehicle vehicle;

    // ===== 기간/계량 =====
    @Column(name = "start_actual")
    private LocalDateTime startActual;

    @Column(name = "end_actual")
    private LocalDateTime endActual;

    @Column(name = "start_meter")
    private Integer startMeter;

    @Column(name = "end_meter")
    private Integer endMeter;

    // ===== 상태 (DB ENUM: 'ongoing','returned','no_show','cancelled')와 동일한 문자열로 저장 =====
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.ongoing;

    public enum Status { ongoing, returned, no_show, cancelled }

    // ===== 기본 생성자 =====
    public Rental() {}

    public Rental(Reservation reservation, User user, Vehicle vehicle) {
        this.reservation = reservation;
        this.user = user;
        this.vehicle = vehicle;
    }

    // ===== ID 편의 접근자 (중첩 타입 사용 코드를 그대로 살리기 위함) =====
    @Transient
    public String getReservationId() { return reservation != null ? reservation.getReservationId() : null; }

    /** 필요시 id로도 세팅 가능 (영속성 컨텍스트 바깥에서 사용할 때 주의) */
    public void setReservationId(String reservationId) {
        if (this.reservation == null) this.reservation = new Reservation();
        this.reservation.setReservationId(reservationId);
    }

    @Transient
    public String getUserId() { return user != null ? user.getUserId() : null; }

    public void setUserId(String userId) {
        if (this.user == null) this.user = new User();
        this.user.setUserId(userId);
    }

    @Transient
    public String getVehicleId() { return vehicle != null ? vehicle.getVehicleId() : null; }

    public void setVehicleId(String vehicleId) {
        if (this.vehicle == null) this.vehicle = new Vehicle();
        this.vehicle.setVehicleId(vehicleId);
    }

    // ===== Getter / Setter =====
    public String getRentalId() { return rentalId; }
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public LocalDateTime getStartActual() { return startActual; }
    public void setStartActual(LocalDateTime startActual) { this.startActual = startActual; }

    public LocalDateTime getEndActual() { return endActual; }
    public void setEndActual(LocalDateTime endActual) { this.endActual = endActual; }

    public Integer getStartMeter() { return startMeter; }
    public void setStartMeter(Integer startMeter) { this.startMeter = startMeter; }

    public Integer getEndMeter() { return endMeter; }
    public void setEndMeter(Integer endMeter) { this.endMeter = endMeter; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
