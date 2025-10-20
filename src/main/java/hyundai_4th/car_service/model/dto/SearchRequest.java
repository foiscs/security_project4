package hyundai_4th.car_service.model.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;

@Getter
@Builder
public class SearchRequest {
    private final Instant availableFrom;
    private final Instant availableTo;
    private final String pickupLocationId;
    private final Integer page;
    private final Integer size;
}
