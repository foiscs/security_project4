package com.security.test.model.dto;

import com.security.test.model.entity.Reservation;
import java.time.LocalDateTime;

/**
 * 예약 정보 응답 DTO
 * 예약 생성, 조회 시 반환되는 데이터
 */
public class ReservationResponse {

    private String reservationId;
    private UserResponse user;
    private VehicleResponse vehicle;
    private LocationResponse pickupLocation;
    private LocationResponse dropoffLocation;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String status;
    private LocalDateTime createdAt;

    // 기본 생성자
    public ReservationResponse() {
    }

    // Entity를 DTO로 변환하는 생성자
    public ReservationResponse(Reservation reservation) {
        this.reservationId = reservation.getReservationId();
        this.user = new UserResponse(reservation.getUser());
        this.vehicle = new VehicleResponse(reservation.getVehicle());
        this.pickupLocation = new LocationResponse(reservation.getPickupLocation());
        this.dropoffLocation = new LocationResponse(reservation.getDropoffLocation());
        this.startAt = reservation.getStartAt();
        this.endAt = reservation.getEndAt();
        this.status = reservation.getStatus().name();
        this.createdAt = reservation.getCreatedAt();
    }

    // Getter & Setter
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public VehicleResponse getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleResponse vehicle) {
        this.vehicle = vehicle;
    }

    public LocationResponse getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(LocationResponse pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public LocationResponse getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(LocationResponse dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}