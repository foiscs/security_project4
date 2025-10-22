package hyundai_4th.car_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hyundai_4th.car_service.model.entity.User;
import java.time.Instant;

public class UserResponse {

    @JsonProperty("user_id")
    private String user_id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("status")
    private String status;

    @JsonProperty("created_at")
    private String created_at;

    public UserResponse() {}

    public UserResponse(String id, String email, String name, String phone, String status, String createdAt) {
        this.user_id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.created_at = createdAt;
    }


    public UserResponse(User user) {
        if (user == null) return;
        this.user_id = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.status = user.getStatus();
        this.created_at = (user.getCreatedAt() != null)
                ? user.getCreatedAt().toString()
                : null;
    }


    public String getUser_id() { return user_id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public String getCreated_at() { return created_at; }
}
