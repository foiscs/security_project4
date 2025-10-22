package hyundai_4th.car_service.controller;

import hyundai_4th.car_service.model.dto.UserRequest;
import hyundai_4th.car_service.model.dto.UserResponse;
import hyundai_4th.car_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody UserRequest req) {
        return userService.create(req);
    }

    @GetMapping("/{userId}")
    public UserResponse getById(@PathVariable String userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public UserResponse getByEmail(@RequestParam String email) {
        return userService.getByEmail(email);
    }

    @PatchMapping("/{userId}")
    public UserResponse update(@PathVariable String userId,
                               @RequestBody UserRequest req) {
        return userService.update((userId), req);
    }
}