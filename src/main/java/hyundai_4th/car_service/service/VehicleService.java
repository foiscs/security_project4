package hyundai_4th.car_service.service;

import hyundai_4th.car_service.model.dto.VehicleResponse;
import hyundai_4th.car_service.model.entity.Vehicle;
import hyundai_4th.car_service.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleResponse getVehicle(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("차량을 찾을 수 없습니다: " + vehicleId));
        return new VehicleResponse(vehicle);
    }

    public List<VehicleResponse> getAvailableVehicles() {
        return vehicleRepository.findByStatus("available")
                .stream().map(VehicleResponse::new).collect(Collectors.toList());
    }

    public List<VehicleResponse> getAvailableVehiclesByLocation(String locationId) {
        return vehicleRepository.findByLocationIdAndStatus(locationId, "available")
                .stream().map(VehicleResponse::new).collect(Collectors.toList());
    }

    public List<VehicleResponse> searchVehiclesByBrandAndModel(String brand, String model) {
        return vehicleRepository.findByBrandAndModel(brand, model)
                .stream().map(VehicleResponse::new).collect(Collectors.toList());
    }

    public List<VehicleResponse> getVehiclesByLocation(String locationId) {
        return vehicleRepository.findByLocationId(locationId)
                .stream().map(VehicleResponse::new).collect(Collectors.toList());
    }

    public VehicleResponse getVehicleByPlate(String plate) {
        Vehicle vehicle = vehicleRepository.findByPlate(plate)
                .orElseThrow(() -> new RuntimeException("차량을 찾을 수 없습니다: " + plate));
        return new VehicleResponse(vehicle);
    }

    public List<VehicleResponse> getVehiclesByBrand(String brand) {
        return vehicleRepository.findByBrand(brand)
                .stream().map(VehicleResponse::new).collect(Collectors.toList());
    }

    // ---- ✅ 새로 추가: 동적 필터 + 페이징/정렬 지원 ----
    public Page<VehicleResponse> searchVehicles(
            boolean availableOnly,
            String locationId,
            String brand,
            String model,
            String status,
            String plate,
            Pageable pageable
    ) {
        Specification<Vehicle> spec = (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            if (availableOnly) {
                preds.add(cb.equal(root.get("status"), "available"));
            }
            if (status != null && !status.isBlank()) {
                preds.add(cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
            }
            if (locationId != null && !locationId.isBlank()) {
                // 엔티티 필드명이 currentLocationId라면 root.get("currentLocationId")로 변경
                preds.add(cb.equal(root.get("locationId"), locationId));
            }
            if (brand != null && !brand.isBlank()) {
                preds.add(cb.like(cb.lower(root.get("brand")), "%" + brand.toLowerCase() + "%"));
            }
            if (model != null && !model.isBlank()) {
                preds.add(cb.like(cb.lower(root.get("model")), "%" + model.toLowerCase() + "%"));
            }
            if (plate != null && !plate.isBlank()) {
                preds.add(cb.like(cb.lower(root.get("plate")), "%" + plate.toLowerCase() + "%"));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };

        return vehicleRepository.findAll(spec, pageable)
                .map(VehicleResponse::new);
    }
}
