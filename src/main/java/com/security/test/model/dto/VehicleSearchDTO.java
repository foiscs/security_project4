package com.security.test.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 차량 검색 요청 DTO
 *
 * 복잡한 검색 조건을 처리하기 위한 요청 객체
 */
@Getter
@Setter
public class VehicleSearchDTO {

    private boolean availableOnly = false;
    private String locationId;
    private String brand;
    private String model;
    private String status;
    private String plate;
    private String availableFrom;
    private String availableTo;
    private String pickupLocationId;
    private Integer page = 0;
    private Integer size = 20;
}
