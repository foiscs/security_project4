package hyundai_4th.car_service.model.dto;

import hyundai_4th.car_service.model.entity.Rental;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RentalDTO {

    @Getter @Setter
    public static class RentalStartRequest {
        private String reservationId;
        private String userId;
        private String vehicleId;
        private Integer startMeter;
        private Long startActualEpochMs; // 그대로 유지해도 됨 (Instant 변환용)
    }

    @Getter @Setter
    public static class RentalReturnRequest {
        private String rentalId;
        private Integer endMeter;
        private Long endActualEpochMs;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RentalResponse {
        private String rentalId;
        private String reservationId;
        private String userId;
        private String vehicleId;
        private String status;
        private String startActual;
        private String endActual;
        private Integer startMeter;
        private Integer endMeter;

        /** 엔티티 -> DTO 변환 */
        public static RentalResponse of(Rental e) {
            return RentalResponse.builder()
                    .rentalId(e.getRentalId())
                    .reservationId(e.getReservationId())
                    .userId(e.getUserId())
                    .vehicleId(e.getVehicleId())
                    .status(e.getStatus().name())  // already lowercase(enum)
                    .startActual(fmt(e.getStartActual()))
                    .endActual(fmt(e.getEndActual()))
                    .startMeter(e.getStartMeter())
                    .endMeter(e.getEndMeter())
                    .build();
        }

        /** LocalDateTime → ISO-8601 문자열 */
        private static String fmt(LocalDateTime t) {
            return (t == null)
                    ? null
                    : t.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
}
