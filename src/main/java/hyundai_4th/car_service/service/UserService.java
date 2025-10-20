package hyundai_4th.car_service.service;

import hyundai_4th.car_service.model.dto.UserRequest;
import hyundai_4th.car_service.model.dto.UserResponse;
import hyundai_4th.car_service.model.entity.User_Signup;
import hyundai_4th.car_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* 회원가입 */
    @Transactional
    public UserResponse create(UserRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank())
            throw new IllegalArgumentException("email is required");
        if (req.getPassword() == null || req.getPassword().isBlank())
            throw new IllegalArgumentException("password is required");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("email already exists");

        User_Signup u = new User_Signup();
        u.setEmail(req.getEmail().trim());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setName(req.getName());
        u.setPhone(req.getPhone());
        u.setStatus(req.getStatus() != null ? req.getStatus() : "active");

        User_Signup saved = userRepository.save(u);
        return toDto(saved);
    }

    /* 단건 조회 */
    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        User_Signup u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        return toDto(u);
    }

    /* 이메일로 조회 */
    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        User_Signup u = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        return toDto(u);
    }

    /* 부분 수정 (name/phone/status/password만 반영) */
    @Transactional
    public UserResponse update(UUID id, UserRequest req) {
        User_Signup u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        if (req.getName() != null)   u.setName(req.getName());
        if (req.getPhone() != null)  u.setPhone(req.getPhone());
        if (req.getStatus() != null) u.setStatus(req.getStatus());
        if (req.getPassword() != null && !req.getPassword().isBlank())
            u.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        return toDto(u); // dirty checking
    }

    private UserResponse toDto(User_Signup u) {
        return new UserResponse(
                u.getUserId().toString(),
                u.getEmail(),
                u.getName(),
                u.getPhone(),
                u.getStatus(),
                LocalDateTime.ofInstant(u.getCreatedAt(), ZoneId.systemDefault())
        );
    }
}
