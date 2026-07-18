package com.cbs.controller;

import com.cbs.common.ApiResponse;
import com.cbs.model.User;
import com.cbs.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody @Valid LoginRequest req) {
        Map<String, Object> result = authService.login(req.getUsername(), req.getPassword());
        return ApiResponse.ok(result);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> register(@RequestBody @Valid RegisterRequest req) {
        User user = authService.createUser(req.getUsername(), req.getPassword(), req.getRoles());
        return ApiResponse.ok(user, "User created");
    }

    @Data
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank private String username;
        @NotBlank private String password;
        private java.util.Set<String> roles = java.util.Set.of("USER");
    }
}
