package com.security.test.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class DoorCommandRequest {
    @NotBlank
    @Pattern(regexp = "OPEN|CLOSE", message = "action must be OPEN or CLOSE")
    private String action;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
