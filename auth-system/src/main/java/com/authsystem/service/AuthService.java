package com.authsystem.service;

import com.authsystem.dto.AuthResponse;
import com.authsystem.dto.LoginRequest;
import com.authsystem.dto.RegisterRequest;
import com.authsystem.entity.User;
import com.authsystem.exception.UserAlreadyExistsException;
import com.authsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user after validating uniqueness and hashing the password with BCrypt.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.debug("Registration attempt for username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt hash
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getUsername());

        return AuthResponse.builder()
                .success(true)
                .message("Registration successful")
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Authenticates user credentials via Spring Security's AuthenticationManager.
     * BCrypt comparison is handled internally — passwords are never compared in plain text.
     */
    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt for username: {}", request.getUsername());

        // Delegates to CustomUserDetailsService + BCrypt verification
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        log.info("Successful login for user: {}", user.getUsername());

        return AuthResponse.builder()
                .success(true)
                .message("Login successful")
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
