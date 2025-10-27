package com.security.test.model.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class UserRequest {
    @Email
    @Size(max = 320)
    private String email;

    // 생성 시 필수, 수정 시 선택(null/blank 허용) → 서비스에서 체크
    @Size(min = 8, max = 100)
    private String password;

    @Size(max = 100)
    private String name;

    @Size(max = 30)
    private String phone;

    @Size(max = 20)
    private String status;

    // getters/setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}