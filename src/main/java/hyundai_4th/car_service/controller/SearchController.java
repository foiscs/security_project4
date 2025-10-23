package hyundai_4th.car_service.controller;

import hyundai_4th.car_service.model.dto.SearchRequest;
import hyundai_4th.car_service.model.dto.SearchVehicle;
import hyundai_4th.car_service.model.dto.VehicleSearchDTO;
import hyundai_4th.car_service.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.List;

/**
 * 차량 검색 컨트롤러
 */
@RestController
@RequestMapping("/api/v1")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchVehicle>> searchVehicles(
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

        SearchRequest request = SearchRequest.builder()
                .availableFrom(availableFrom)
                .availableTo(availableTo)
                .pickupLocationId(pickupLocationId)
                .page(page)
                .size(size)
                .build();

        List<SearchVehicle> vehicles = searchService.findAvailableVehicles(request);
        return ResponseEntity.ok(vehicles);
    }

    /**
     * 차량 검색 (POST 방식)
     *
     * 복잡한 검색 조건을 처리하기 위한 POST 엔드포인트
     */
    @PostMapping("/search")
    public ResponseEntity<String> searchVehiclesPost(VehicleSearchDTO searchDTO) {
        try {
            // 정상적인 비즈니스 로직
            if (searchDTO.getAvailableFrom() != null && searchDTO.getAvailableTo() != null) {
                Instant availableFrom = Instant.parse(searchDTO.getAvailableFrom());
                Instant availableTo = Instant.parse(searchDTO.getAvailableTo());

                if (!availableFrom.isBefore(availableTo)) {
                    return ResponseEntity.badRequest().body("Invalid date range");
                }

                SearchRequest request = SearchRequest.builder()
                        .availableFrom(availableFrom)
                        .availableTo(availableTo)
                        .pickupLocationId(searchDTO.getPickupLocationId())
                        .page(searchDTO.getPage())
                        .size(searchDTO.getSize())
                        .build();

                List<SearchVehicle> vehicles = searchService.findAvailableVehicles(request);
                return ResponseEntity.ok("Found " + vehicles.size() + " vehicles");
            }
            return ResponseEntity.ok("Search completed");
        } catch (Exception e) {
            return ResponseEntity.ok("Search processed");
        }
    }
}