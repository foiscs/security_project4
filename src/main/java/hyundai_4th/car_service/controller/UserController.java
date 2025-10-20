package hyundai_4th.car_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/")
    public String home() {
        return "Car Service API is running!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
