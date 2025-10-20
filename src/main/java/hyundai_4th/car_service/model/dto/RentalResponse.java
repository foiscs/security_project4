package hyundai_4th.car_service.model.dto;

import hyundai_4th.car_service.model.entity.Rental;
import java.time.LocalDateTime;

/**
 * 대여 정보 응답 DTO
 * 대여 시작, 반납, 조회 시 반환되는 데이터
 */
public class RentalResponse {

    private String rentalId;
    private ReservationResponse reservation;
    private UserResponse user;
    private VehicleResponse vehicle;
    private LocalDateTime startActual;
    private LocalDateTime endActual;
    private Integer startMeter;
    private Integer endMeter;
    private String status;

    // 기본 생성자
    public RentalResponse() {
    }

    // Entity를 DTO로 변환하는 생성자
    public RentalResponse(Rental rental) {
        this.rentalId = rental.getRentalId();
        this.reservation = new ReservationResponse(rental.getReservation());
        this.user = new UserResponse(rental.getUser());
        this.vehicle = new VehicleResponse(rental.getVehicle());
        this.startActual = rental.getStartActual();
        this.endActual = rental.getEndActual();
        this.startMeter = rental.getStartMeter();
        this.endMeter = rental.getEndMeter();
        this.status = rental.getStatus().name();
    }

    // Getter & Setter
    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public ReservationResponse getReservation() {
        return reservation;
    }

    public void setReservation(ReservationResponse reservation) {
        this.reservation = reservation;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public VehicleResponse getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleResponse vehicle) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
