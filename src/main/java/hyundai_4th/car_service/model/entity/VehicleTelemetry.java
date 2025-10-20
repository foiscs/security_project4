package hyundai_4th.car_service.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_telemetry",
       indexes = {
           @Index(name = "idx_tel_vehicle_time", columnList = "vehicle_id, ts")
       })
public class VehicleTelemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "telemetry_id")
    private Long telemetryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "ts", nullable = false)
    private LocalDateTime ts;  // 타임스탬프

    @Column(name = "lat", precision = 9, scale = 6)
    private BigDecimal lat;  // 위도

    @Column(name = "lng", precision = 9, scale = 6)
    private BigDecimal lng;  // 경도

    @Column(name = "speed")
    private Double speed;  // 속도 (km/h)

    @Column(name = "ignition")
    private Boolean ignition;  // 시동 상태

    @Column(name = "raw_payload", columnDefinition = "JSON")
    private String rawPayload;  // JSON 원시 데이터

    // 기본 생성자
    public VehicleTelemetry() {
    }

    // 생성자
    public VehicleTelemetry(Vehicle vehicle, LocalDateTime ts) {
        this.vehicle = vehicle;
        this.ts = ts;
    }

    // Getter & Setter
    public Long getTelemetryId() {
        return telemetryId;
    }

    public void setTelemetryId(Long telemetryId) {
        this.telemetryId = telemetryId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDateTime getTs() {
        return ts;
    }

    public void setTs(LocalDateTime ts) {
        this.ts = ts;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Boolean getIgnition() {
        return ignition;
    }

    public void setIgnition(Boolean ignition) {
        this.ignition = ignition;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }
}
