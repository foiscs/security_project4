package com.security.test.service;

import com.security.test.model.dto.SearchRequest;
import com.security.test.model.dto.SearchVehicle;
import com.security.test.repository.SearchRepository;
import com.security.test.repository.projection.SearchVehicleProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final SearchRepository searchRepository;

    public SearchService(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Transactional(readOnly = true)
    public List<SearchVehicle> findAvailableVehicles(SearchRequest request) {
        int offset = Math.max(0, request.getPage() * request.getSize());

        List<SearchVehicleProjection> rows = searchRepository.findAvailableVehicles(
                request.getPickupLocationId(),
                request.getAvailableFrom().toString(),
                request.getAvailableTo().toString(),
                request.getSize(),
                offset
        );

        return rows.stream()
                .map(r -> new SearchVehicle(
                        r.getVehicleId(),
                        r.getVin(),
                        r.getPlate(),
                        r.getBrand(),
                        r.getModel(),
                        r.getYearValue(),
                        r.getStatus(),
                        r.getCurrentLocationId()
                ))
                .collect(Collectors.toList());
    }
}