package com.security.test.repository;

import com.security.test.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {

    // 위치 타입으로 조회
    List<Location> findByType(String type);

    // 위치 이름으로 조회
    Optional<Location> findByName(String name);

    // 위치 타입과 이름으로 조회
    Optional<Location> findByTypeAndName(String type, String name);
}