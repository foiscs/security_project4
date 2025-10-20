package hyundai_4th.car_service.controller;

import hyundai_4th.car_service.model.dto.VehicleResponse;
import hyundai_4th.car_service.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 차량 관리 Controller
 * 차량 조회, 검색 API 제공
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    /**
     * 차량 조회 (ID로)
     * GET /api/vehicles/{vehicleId}
     */
    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable String vehicleId) {
        try {
            VehicleResponse response = vehicleService.getVehicle(vehicleId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * 대여 가능한 모든 차량 조회
     * GET /api/vehicles/available
     */
    @GetMapping("/available")
    public ResponseEntity<List<VehicleResponse>> getAvailableVehicles() {
        List<VehicleResponse> responses = vehicleService.getAvailableVehicles();
        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 위치의 대여 가능한 차량 조회
     * GET /api/vehicles/available/location/{locationId}
     */
    @GetMapping("/available/location/{locationId}")
    public ResponseEntity<List<VehicleResponse>> getAvailableVehiclesByLocation(@PathVariable String locationId) {
        List<VehicleResponse> responses = vehicleService.getAvailableVehiclesByLocation(locationId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 브랜드와 모델로 차량 검색
     * GET /api/vehicles/search?brand={brand}&model={model}
     */
    @GetMapping("/search")
    public ResponseEntity<List<VehicleResponse>> searchVehicles(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model) {

        if (brand != null && model != null) {
            // 브랜드와 모델 모두 제공된 경우
            List<VehicleResponse> responses = vehicleService.searchVehiclesByBrandAndModel(brand, model);
            return ResponseEntity.ok(responses);
        } else if (brand != null) {
            // 브랜드만 제공된 경우
            List<VehicleResponse> responses = vehicleService.getVehiclesByBrand(brand);
            return ResponseEntity.ok(responses);
        } else {
            // 검색 조건이 없는 경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 번호판으로 차량 조회
     * GET /api/vehicles/plate/{plate}
     */
    @GetMapping("/plate/{plate}")
    public ResponseEntity<VehicleResponse> getVehicleByPlate(@PathVariable String plate) {
        try {
            VehicleResponse response = vehicleService.getVehicleByPlate(plate);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * 특정 위치의 모든 차량 조회
     * GET /api/vehicles/location/{locationId}
     */
    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByLocation(@PathVariable String locationId) {
        List<VehicleResponse> responses = vehicleService.getVehiclesByLocation(locationId);
        return ResponseEntity.ok(responses);
    }
}
