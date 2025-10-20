package hyundai_4th.car_service.model.dto;

/**
 * 차량 반납 요청 DTO
 * 차량 반납 시 전송하는 데이터
 */
public class RentalReturnRequest {

    private String rentalId;    // 대여 ID
    private Integer endMeter;   // 종료 주행거리 (km)
    private String notes;       // 비고 (차량 상태, 특이사항 등)

    // 기본 생성자
    public RentalReturnRequest() {
    }

    // 전체 생성자
    public RentalReturnRequest(String rentalId, Integer endMeter, String notes) {
        this.rentalId = rentalId;
        this.endMeter = endMeter;
        this.notes = notes;
    }

    // Getter & Setter
    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public Integer getEndMeter() {
        return endMeter;
    }

    public void setEndMeter(Integer endMeter) {
        this.endMeter = endMeter;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
