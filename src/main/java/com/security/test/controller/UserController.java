package com.security.test.controller;

import com.security.test.model.dto.UserRequest;
import com.security.test.model.dto.UserResponse;
import com.security.test.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * ⚠️ VULNERABLE: Spring4Shell (CVE-2022-22965) 취약점 존재
 * @ModelAttribute를 사용하는 엔드포인트에서 취약점 발생
 */
@Controller
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    /**
     * VULNERABLE: 사용자 등록 폼 페이지
     * @ModelAttribute 사용 - Spring4Shell 공격 가능
     */
    @GetMapping("/register")
    public String registerForm(@ModelAttribute("user") UserRequest req, Model model) {
        model.addAttribute("user", req);
        return "user-register";
    }

    /**
     * VULNERABLE: Form 기반 사용자 등록
     * @ModelAttribute 사용 - Spring4Shell 공격 가능
     */
    @PostMapping(value = "/register", consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerUser(@ModelAttribute UserRequest req) {
        return userService.create(req);
    }

    /**
     * VULNERABLE: 사용자 정보 수정 폼 페이지
     * @ModelAttribute 사용 - Spring4Shell 공격 가능
     */
    @GetMapping("/{userId}/edit")
    public String editForm(@PathVariable String userId,
                          @ModelAttribute("user") UserRequest req,
                          Model model) {
        UserResponse user = userService.getById(userId);
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        return "user-edit";
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody UserRequest req) {
        return userService.create(req);
    }

    @GetMapping("/{userId}")
    @ResponseBody
    public UserResponse getById(@PathVariable String userId) {
        return userService.getById(userId);
    }

    @GetMapping
    @ResponseBody
    public UserResponse getByEmail(@RequestParam String email) {
        return userService.getByEmail(email);
    }

    /**
     * VULNERABLE: Form 기반 사용자 정보 수정
     * @ModelAttribute 사용 - Spring4Shell 공격 가능
     */
    @PatchMapping(value = "/{userId}", consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public UserResponse updateForm(@PathVariable String userId,
                                   @ModelAttribute UserRequest req) {
        return userService.update(userId, req);
    }

    @PatchMapping(value = "/{userId}", consumes = "application/json")
    @ResponseBody
    public UserResponse update(@PathVariable String userId,
                               @RequestBody UserRequest req) {
        return userService.update(userId, req);
    }
}