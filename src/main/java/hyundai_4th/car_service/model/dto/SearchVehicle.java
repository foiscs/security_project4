package hyundai_4th.car_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchVehicle {

    @JsonProperty("vehicle_id")
    private String vehicleId;

    @JsonProperty("vin")
    private String vin;

    @JsonProperty("plate")
    private String plate;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("model")
    private String model;

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("status")
    private String status;

    @JsonProperty("current_location_id")
    private String currentLocationId;
}