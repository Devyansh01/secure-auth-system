package com.authsystem.controller;

import com.authsystem.dto.AuthResponse;
import com.authsystem.dto.LoginRequest;
import com.authsystem.dto.RegisterRequest;
import com.authsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Registers a new user. Returns 201 Created on success.
     * Returns 400 Bad Request if validation fails.
     * Returns 409 Conflict if username/email already exists.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register — username: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login
     * Authenticates user credentials. Returns 200 OK on success.
     * Returns 401 Unauthorized if credentials are invalid.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login — username: {}", request.getUsername());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/health
     * Simple health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }
}
