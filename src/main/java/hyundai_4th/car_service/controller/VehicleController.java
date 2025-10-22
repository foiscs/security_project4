package hyundai_4th.car_service.controller;

import hyundai_4th.car_service.model.dto.VehicleResponse;
import hyundai_4th.car_service.model.dto.SearchRequest;
import hyundai_4th.car_service.model.dto.SearchVehicle;
import hyundai_4th.car_service.service.VehicleService;
import hyundai_4th.car_service.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Validated
public class VehicleController {

    private final VehicleService vehicleService;
    private final SearchService searchService;

    /** 단건 조회 */
    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable String vehicleId) {
        return ResponseEntity.ok(vehicleService.getVehicle(vehicleId));
    }

    /** 속성 필터 + 페이징 */
    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> listVehicles(
            @RequestParam(required = false, defaultValue = "false") boolean availableOnly,
            @RequestParam(required = false) String locationId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String plate,
            @PageableDefault(size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand"),
                    @SortDefault(sort = "model")
            }) Pageable pageable
    ) {
        Page<VehicleResponse> page = vehicleService.searchVehicles(
                availableOnly, locationId, brand, model, status, plate, pageable
        );
        return ResponseEntity.ok(page);
    }

    /** 시간/픽업 기준 가용 차량 검색 (원래 SearchController 메서드) */
    @GetMapping("/available")
    public ResponseEntity<List<SearchVehicle>> searchAvailableVehicles(
            @RequestParam(name = "available_from") String availableFromStr,
            @RequestParam(name = "available_to") String availableToStr,
            @RequestParam(name = "pickup_location_id") String pickupLocationId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "20") Integer size
    ) {
        Instant availableFrom = Instant.parse(availableFromStr);
        Instant availableTo = Instant.parse(availableToStr);
        if (!availableFrom.isBefore(availableTo)) {
            return ResponseEntity.badRequest().build();
        }

        SearchRequest req = SearchRequest.builder()
                .availableFrom(availableFrom)
                .availableTo(availableTo)
                .pickupLocationId(pickupLocationId)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok(searchService.findAvailableVehicles(req));
    }
}
