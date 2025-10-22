package hyundai_4th.car_service.model.dto;

import java.time.LocalDateTime;

/**
 * 예약 생성 요청 DTO
 * 클라이언트가 차량 예약 시 전송하는 데이터
 */
public class ReservationRequest {

    private String userId;
    private String vehicleId;
    private String pickupLocationId;
    private String dropoffLocationId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    // 기본 생성자
    public ReservationRequest() {
    }

    // 전체 생성자
    public ReservationRequest(String userId, String vehicleId, String pickupLocationId,
                              String dropoffLocationId, LocalDateTime startAt, LocalDateTime endAt) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.pickupLocationId = pickupLocationId;
        this.dropoffLocationId = dropoffLocationId;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    // Getter & Setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getPickupLocationId() {
        return pickupLocationId;
    }

    public void setPickupLocationId(String pickupLocationId) {
        this.pickupLocationId = pickupLocationId;
    }

    public String getDropoffLocationId() {
        return dropoffLocationId;
    }

    public void setDropoffLocationId(String dropoffLocationId) {
        this.dropoffLocationId = dropoffLocationId;
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
}