package com.security.test.controller;

import com.security.test.model.dto.VehicleResponse;
import com.security.test.model.dto.SearchRequest;
import com.security.test.model.dto.SearchVehicle;
import com.security.test.model.dto.VehicleSearchDTO;
import com.security.test.service.VehicleService;
import com.security.test.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * ⚠️ VULNERABLE: Spring4Shell (CVE-2022-22965) 취약점 존재
 * 일부 엔드포인트에서 @ModelAttribute를 사용하여 취약점 발생
 */
@Controller
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Validated
public class VehicleController {

    private final VehicleService vehicleService;
    private final SearchService searchService;

    /** 단건 조회 */
    @GetMapping("/{vehicleId}")
    @ResponseBody
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable String vehicleId) {
        return ResponseEntity.ok(vehicleService.getVehicle(vehicleId));
    }

    /**
     * VULNERABLE: 차량 검색 폼 페이지
     * @ModelAttribute 사용 - Spring4Shell 공격 가능
     */
    @GetMapping("/search")
    public String searchForm(@ModelAttribute("searchRequest") VehicleSearchDTO searchDto, Model model) {
        model.addAttribute("searchRequest", searchDto);
        return "vehicle-search";
    }

    /**
     * VULNERABLE: Form 기반 차량 검색
     * @ModelAttribute 사용 - Spring4Shell 공격 가능
     */
    @PostMapping("/search")
    @ResponseBody
    public ResponseEntity<Page<VehicleResponse>> searchVehiclesForm(
            @ModelAttribute VehicleSearchDTO searchDto,
            @PageableDefault(size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "brand"),
                    @SortDefault(sort = "model")
            }) Pageable pageable
    ) {
        Page<VehicleResponse> page = vehicleService.searchVehicles(
                searchDto.isAvailableOnly(),
                searchDto.getLocationId(),
                searchDto.getBrand(),
                searchDto.getModel(),
                searchDto.getStatus(),
                searchDto.getPlate(),
                pageable
        );
        return ResponseEntity.ok(page);
    }

    /** 속성 필터 + 페이징 (기존 엔드포인트 유지) */
    @GetMapping
    @ResponseBody
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
    @ResponseBody
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
