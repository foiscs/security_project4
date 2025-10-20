package hyundai_4th.car_service.model.dto;

/**
 * 대여 시작 요청 DTO
 * 예약을 실제 대여로 전환할 때 사용
 */
public class RentalStartRequest {

    private String reservationId;  // 예약 ID
    private Integer startMeter;    // 시작 주행거리 (km)

    // 기본 생성자
    public RentalStartRequest() {
    }

    // 전체 생성자
    public RentalStartRequest(String reservationId, Integer startMeter) {
        this.reservationId = reservationId;
        this.startMeter = startMeter;
    }

    // Getter & Setter
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getStartMeter() {
        return startMeter;
    }

    public void setStartMeter(Integer startMeter) {
        this.startMeter = startMeter;
    }
}
