package hyundai_4th.car_service.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "vehicles")
public class SearchVehicle {
    @Id
    @Column(name = "vehicle_id", length = 36)
    private String vehicleId;

    @Column(nullable = false, unique = true, length = 64)
    private String vin;

    @Column(nullable = false, unique = true, length = 32)
    private String plate;

    @Column(nullable = false, length = 80)
    private String model;

    @Column(nullable = false, length = 80)
    private String brand;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "current_location_id", nullable = false, length = 36)
    private String currentLocationId;
}
