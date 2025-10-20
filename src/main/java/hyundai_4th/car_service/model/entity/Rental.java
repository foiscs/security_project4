package hyundai_4th.car_service.model.entity;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"rental_id", "user_id"})
       },
       indexes = {
           @Index(name = "idx_rent_user_start", columnList = "user_id, start_actual")
       })
public class Rental {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "rental_id", length = 36, nullable = false)
    private String rentalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, referencedColumnName = "reservation_id")
    private Reservation reservation;  // 예약 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 대여한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;  // 대여한 차량

    @Column(name = "start_actual")
    private LocalDateTime startActual;  // 실제 대여 시작 시각

    @Column(name = "end_actual")
    private LocalDateTime endActual;  // 실제 반납 시각

    @Column(name = "start_meter")
    private Integer startMeter;  // 시작 주행거리 (km)

    @Column(name = "end_meter")
    private Integer endMeter;  // 종료 주행거리 (km)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalStatus status = RentalStatus.ONGOING;

    // 대여 상태 Enum
    public enum RentalStatus {
        ONGOING,     // 대여 중
        RETURNED,    // 반납 완료
        NO_SHOW,     // 노쇼 (예약했으나 대여 안함)
        CANCELLED    // 취소됨
    }

    // 기본 생성자
    public Rental() {
    }

    // 생성자
    public Rental(Reservation reservation, User user, Vehicle vehicle) {
        this.reservation = reservation;
        this.user = user;
        this.vehicle = vehicle;
    }

    // Getter & Setter
    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
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

    public LocalDateTime getStartActual() {
        return startActual;
    }

    public void setStartActual(LocalDateTime startActual) {
        this.startActual = startActual;
    }

    public LocalDateTime getEndActual() {
        return endActual;
    }

    public void setEndActual(LocalDateTime endActual) {
        this.endActual = endActual;
    }

    public Integer getStartMeter() {
        return startMeter;
    }

    public void setStartMeter(Integer startMeter) {
        this.startMeter = startMeter;
    }

    public Integer getEndMeter() {
        return endMeter;
    }

    public void setEndMeter(Integer endMeter) {
        this.endMeter = endMeter;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }
}
