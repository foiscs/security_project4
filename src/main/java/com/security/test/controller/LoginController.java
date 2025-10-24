package com.security.test.controller;

import com.security.test.model.entity.User;
import com.security.test.repository.UserRepository;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<LoginRes> login(@RequestBody LoginReq req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return ResponseEntity.ok(
                new LoginRes(
                        user.getUserId(),
                        user.getEmail(),
                        user.getName(),
                        user.getPhone(),
                        user.getStatus(),
                        user.getCreatedAt(),
                        "Login successful"
                )
        );
    }

    // ===== 내부 DTO =====
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    static class LoginReq {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    static class LoginRes {
        private String userId;
        private String email;
        private String name;
        private String phone;
        private String status;
        private Instant createdAt;
        private String message;
    }
}
