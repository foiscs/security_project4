package hyundai_4th.car_service.model.dto;

import lombok.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import hyundai_4th.car_service.model.entity.RentalEntity;

public class RentalDTO {

    @Getter @Setter
    public static class RentalStartRequest {
        private String reservationId;
        private String userId;
        private String vehicleId;
        private Integer startMeter;
        private Long startActualEpochMs;
    }

    @Getter @Setter
    public static class RentalReturnRequest {
        private String rentalId;
        private Integer endMeter;
        private Long endActualEpochMs;
    }

    @Getter @Builder @AllArgsConstructor
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

        public static RentalResponse of(RentalEntity.Rental e) {
            return RentalResponse.builder()
                    .rentalId(e.getRentalId())
                    .reservationId(e.getReservationId())
                    .userId(e.getUserId())
                    .vehicleId(e.getVehicleId())
                    .status(e.getStatus().name().toLowerCase())
                    .startActual(fmt(e.getStartActual()))
                    .endActual(fmt(e.getEndActual()))
                    .startMeter(e.getStartMeter())
                    .endMeter(e.getEndMeter())
                    .build();
        }
        private static String fmt(Instant i) {
            return i == null ? null : DateTimeFormatter.ISO_INSTANT.format(i);
        }
    }
}