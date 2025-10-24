package com.security.test.model.dto;

import com.security.test.model.entity.Location;
import java.math.BigDecimal;

/**
 * 위치 정보 응답 DTO
 * 대여소/반납소 정보 반환
 */
public class LocationResponse {

    private String locationId;
    private String name;
    private BigDecimal lat;
    private BigDecimal lng;
    private String type;

    // 기본 생성자
    public LocationResponse() {
    }

    // Entity를 DTO로 변환하는 생성자
    public LocationResponse(Location location) {
        this.locationId = location.getLocationId();
        this.name = location.getName();
        this.lat = location.getLat();
        this.lng = location.getLng();
        this.type = location.getType();
    }

    // 전체 생성자
    public LocationResponse(String locationId, String name, BigDecimal lat,
                            BigDecimal lng, String type) {
        this.locationId = locationId;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
    }

    // Getter & Setter
    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}