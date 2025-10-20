package hyundai_4th.car_service.model.dto;

import hyundai_4th.car_service.model.entity.User;
import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO
 * 민감한 정보(비밀번호, 암호화된 신분증)는 제외하고 안전한 정보만 반환
 */
public class UserResponse {

    private String userId;
    private String email;
    private String name;
    private String phone;
    private String status;
    private LocalDateTime createdAt;

    // 기본 생성자
    public UserResponse() {
    }

    // Entity를 DTO로 변환하는 생성자
    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt();
    }

    // 전체 생성자
    public UserResponse(String userId, String email, String name, String phone,
                        String status, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getter & Setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
