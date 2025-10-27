package com.security.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.security.test.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
