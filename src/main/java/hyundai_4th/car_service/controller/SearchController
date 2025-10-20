package hyundai_4th.car_service.controller;

import hyundai_4th.car_service.model.dto.SearchRequest;
import hyundai_4th.car_service.model.dto.SearchVehicle;
import hyundai_4th.car_service.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
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
}
