package hyundai_4th.car_service.model.dto;

import java.time.LocalDateTime;

public class UserResponse {
    private String user_id;
    private String email;
    private String name;
    private String phone;
    private String status;
    private LocalDateTime created_at;

    public UserResponse(String id, String email, String name, String phone, String status, LocalDateTime createdAt) {
        this.user_id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.created_at = createdAt;
    }

    public String getUser_id() { return user_id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public LocalDateTime getCreated_at() { return created_at; }
}
