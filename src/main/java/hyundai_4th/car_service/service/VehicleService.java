package hyundai_4th.car_service.service;

import hyundai_4th.car_service.model.dto.VehicleResponse;
import hyundai_4th.car_service.model.entity.Vehicle;
import hyundai_4th.car_service.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 차량 관리 Service
 * - 대여 가능한 차량 조회
 * - 차량 상세 정보 조회
 * - 위치별 차량 조회
 */
@Service
@Transactional(readOnly = true)
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * 차량 조회 (ID로)
     */
    public VehicleResponse getVehicle(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("차량을 찾을 수 없습니다: " + vehicleId));

        return new VehicleResponse(vehicle);
    }

    /**
     * 대여 가능한 모든 차량 조회
     */
    public List<VehicleResponse> getAvailableVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findByStatus("available");

        return vehicles.stream()
                .map(VehicleResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 특정 위치의 대여 가능한 차량 조회
     */
    public List<VehicleResponse> getAvailableVehiclesByLocation(String locationId) {
        List<Vehicle> vehicles = vehicleRepository.findByLocationIdAndStatus(locationId, "available");

        return vehicles.stream()
                .map(VehicleResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 브랜드와 모델로 차량 검색
     */
    public List<VehicleResponse> searchVehiclesByBrandAndModel(String brand, String model) {
        List<Vehicle> vehicles = vehicleRepository.findByBrandAndModel(brand, model);

        return vehicles.stream()
                .map(VehicleResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 특정 위치의 모든 차량 조회
     */
    public List<VehicleResponse> getVehiclesByLocation(String locationId) {
        List<Vehicle> vehicles = vehicleRepository.findByLocationId(locationId);

        return vehicles.stream()
                .map(VehicleResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 번호판으로 차량 조회
     */
    public VehicleResponse getVehicleByPlate(String plate) {
        Vehicle vehicle = vehicleRepository.findByPlate(plate)
                .orElseThrow(() -> new RuntimeException("차량을 찾을 수 없습니다: " + plate));

        return new VehicleResponse(vehicle);
    }

    /**
     * 브랜드로 차량 검색
     */
    public List<VehicleResponse> getVehiclesByBrand(String brand) {
        List<Vehicle> vehicles = vehicleRepository.findByBrand(brand);

        return vehicles.stream()
                .map(VehicleResponse::new)
                .collect(Collectors.toList());
    }
}
