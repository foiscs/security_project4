package hyundai_4th.car_service.service;

import hyundai_4th.car_service.exception.NotFoundException;
import hyundai_4th.car_service.repository.VehicleRepository;
import hyundai_4th.car_service.model.dto.DoorStatusResponse;
import org.springframework.stereotype.Service;

//import java.util.concurrent.ConcurrentHashMap;

import hyundai_4th.car_service.model.entity.VehicleTelemetry;
import hyundai_4th.car_service.repository.VehicleTelemetryRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


//@Service
//public class DoorService {
//
//    private final VehicleRepository vehicleRepository;
//    // 메모리 상태 저장: vehicleId -> doorOpen
//    private final ConcurrentHashMap<String, Boolean> doorState = new ConcurrentHashMap<>();
//
//    public DoorService(VehicleRepository vehicleRepository) {
//        this.vehicleRepository = vehicleRepository;
//    }
//
//    private void ensureVehicleExists(String vehicleId) {
//        vehicleRepository.findById(vehicleId)
//                .orElseThrow(() -> new NotFoundException("Vehicle not found: " + vehicleId));
//    }
//
//    public DoorStatusResponse getStatus(String vehicleId) {
//        ensureVehicleExists(vehicleId);
//        boolean open = doorState.getOrDefault(vehicleId, false);
//        return new DoorStatusResponse(vehicleId, open, open ? "열림 상태" : "닫힘 상태");
//    }
//
//    public DoorStatusResponse setOpen(String vehicleId, boolean open) {
//        ensureVehicleExists(vehicleId);
//        doorState.put(vehicleId, open);
//        return new DoorStatusResponse(vehicleId, open, open ? "문을 열었습니다." : "문을 닫았습니다.");
//    }
//}





@Service
public class DoorService {

    private final VehicleRepository vehicleRepository;
    private final VehicleTelemetryRepository telemetryRepository;

    public DoorService(VehicleRepository vehicleRepository,
                       VehicleTelemetryRepository telemetryRepository) {
        this.vehicleRepository = vehicleRepository;
        this.telemetryRepository = telemetryRepository;
    }

    private void ensureVehicleExists(String vehicleId) {
        vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found: " + vehicleId));
    }

    // 최신 텔레메트리 한 건을 읽어 상태 반환 (없으면 기본 false)
    public DoorStatusResponse getStatus(String vehicleId) {
        ensureVehicleExists(vehicleId);
        boolean open = telemetryRepository
                .findTopByVehicleIdOrderByTsDesc(vehicleId)
                .map(VehicleTelemetry::getDoorOpen)
                .orElse(Boolean.FALSE);
        return new DoorStatusResponse(vehicleId, open, open ? "열림 상태" : "닫힘 상태");
    }

    @org.springframework.transaction.annotation.Transactional
    // 문 열기/닫기 요청 시 텔레메트리 한 건 INSERT (X -> UPDATE) (히스토리 증가 없음)
    public DoorStatusResponse setOpen(String vehicleId, boolean open) {
        ensureVehicleExists(vehicleId);
        var latestOpt = telemetryRepository.findTopByVehicleIdOrderByTsDesc(vehicleId);
        if (!latestOpt.isPresent()) {
            // ✅ 텔레메트리 행이 없으면 제어 불가 → 404
            throw new NotFoundException("해당 차량은 없습니다: " + vehicleId);
        }
        // 차량 등록 등 기능에서 Vehicle_telemetry table에도 row 생성됨을 전제로
        var row = latestOpt.get();
        row.setTs(java.time.LocalDateTime.now());
        row.setDoorOpen(open);
        // 필요 시 점화/좌표/속도/raw_payload도 세팅 가능
        telemetryRepository.save(row);

        return new DoorStatusResponse(vehicleId, open, open ? "문을 열었습니다." : "문을 닫았습니다.");
    }
}
