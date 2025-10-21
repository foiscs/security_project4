package hyundai_4th.car_service.model.dto;

import java.time.Instant;
import java.util.UUID;

public class UserResponse {
    private UUID user_id;
    private String email;
    private String name;
    private String phone;
    private String status;
    private Instant created_at;

    public UserResponse(UUID id, String email, String name, String phone, String status, Instant createdAt) {
        this.user_id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.created_at = createdAt;
    }

    public UUID getUser_id() { return user_id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public Instant getCreated_at() { return created_at; }
}
