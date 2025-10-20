package hyundai_4th.car_service.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "location_id", length = 36, nullable = false)
    private String locationId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "lat", precision = 9, scale = 6, nullable = false)
    private BigDecimal lat;  // 위도

    @Column(name = "lng", precision = 9, scale = 6, nullable = false)
    private BigDecimal lng;  // 경도

    @Column(name = "type", length = 30, nullable = false)
    private String type;  // 위치 타입 (예: "rental_station", "parking" 등)

    // 기본 생성자
    public Location() {
    }

    // 생성자
    public Location(String name, BigDecimal lat, BigDecimal lng, String type) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
    }

    // Getter & Setter
    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
