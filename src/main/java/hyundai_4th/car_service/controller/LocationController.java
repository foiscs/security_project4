package hyundai_4th.car_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final JdbcTemplate jdbc;

    @GetMapping
    public List<Map<String, Object>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String near,
            @RequestParam(name = "radius_km", required = false) Double radiusKm
    ) {
        // 1) 지점명 LIKE 검색
        if (name != null && !name.isBlank()) {
            String sql = """
                    SELECT location_id, name, lat, lng, type
                    FROM locations
                    WHERE name LIKE ?
                    ORDER BY name
                    LIMIT 50
                    """;
            return jdbc.query(sql, ps -> ps.setString(1, "%" + name + "%"), (rs, i) -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("location_id", rs.getString("location_id"));
                m.put("name", rs.getString("name"));
                m.put("lat", rs.getBigDecimal("lat"));
                m.put("lng", rs.getBigDecimal("lng"));
                m.put("type", rs.getString("type"));
                // camelCase도 같이 제공 (프런트 호환성)
                m.put("locationId", rs.getString("location_id"));
                return m;
            });
        }

        // 2) 위도경도 근접 검색 (near=lat,lng & radius_km=숫자)
        if (near != null && radiusKm != null) {
            String[] parts = near.split(",");
            if (parts.length != 2) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "near must be 'lat,lng'");
            double lat = Double.parseDouble(parts[0].trim());
            double lng = Double.parseDouble(parts[1].trim());

            // Haversine (km)
            String sql = """
                    SELECT location_id, name, lat, lng, type,
                           (6371 * ACOS(
                              COS(RADIANS(?)) * COS(RADIANS(lat)) *
                              COS(RADIANS(lng) - RADIANS(?)) +
                              SIN(RADIANS(?)) * SIN(RADIANS(lat))
                           )) AS dist_km
                    FROM locations
                    HAVING dist_km <= ?
                    ORDER BY dist_km
                    LIMIT 50
                    """;

            return jdbc.query(sql, ps -> {
                ps.setDouble(1, lat);
                ps.setDouble(2, lng);
                ps.setDouble(3, lat);
                ps.setDouble(4, radiusKm);
            }, (rs, i) -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("location_id", rs.getString("location_id"));
                m.put("name", rs.getString("name"));
                m.put("lat", rs.getBigDecimal("lat"));
                m.put("lng", rs.getBigDecimal("lng"));
                m.put("type", rs.getString("type"));
                m.put("dist_km", rs.getDouble("dist_km"));
                m.put("locationId", rs.getString("location_id"));
                return m;
            });
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "쿼리 파라미터가 필요합니다. name=지점명 또는 near=lat,lng & radius_km=숫자");
    }
}
