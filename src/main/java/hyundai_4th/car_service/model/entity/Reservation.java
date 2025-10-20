package hyundai_4th.car_service.model.entity;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"reservation_id", "user_id", "vehicle_id"})
       },
       indexes = {
           @Index(name = "idx_resv_vehicle_period", columnList = "vehicle_id, start_at, end_at, status"),
           @Index(name = "idx_resv_vehicle_start", columnList = "vehicle_id, start_at")
       })
public class Reservation {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "reservation_id", length = 36, nullable = false)
    private String reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_location_id", nullable = false)
    private Location pickupLocation;  // 픽업 위치

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dropoff_location_id", nullable = false)
    private Location dropoffLocation;  // 반납 위치

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;  // 예약 시작 시각

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;  // 예약 종료 시각

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status = ReservationStatus.BOOKED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 예약 상태 Enum
    public enum ReservationStatus {
        BOOKED,      // 예약됨
        CANCELLED,   // 취소됨
        EXPIRED,     // 만료됨
        CONVERTED    // 대여로 전환됨
    }

    // JPA 자동 시간 설정
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = ReservationStatus.BOOKED;
        }
    }

    // 기본 생성자
    public Reservation() {
    }

    // 생성자
    public Reservation(User user, Vehicle vehicle, Location pickupLocation,
                       Location dropoffLocation, LocalDateTime startAt, LocalDateTime endAt) {
        this.user = user;
        this.vehicle = vehicle;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    // Getter & Setter
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Location getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(Location dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
