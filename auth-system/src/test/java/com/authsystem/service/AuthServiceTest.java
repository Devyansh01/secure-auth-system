package com.authsystem.service;

import com.authsystem.dto.AuthResponse;
import com.authsystem.dto.LoginRequest;
import com.authsystem.dto.RegisterRequest;
import com.authsystem.entity.User;
import com.authsystem.exception.UserAlreadyExistsException;
import com.authsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("john_doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("Secure@123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("john_doe");
        loginRequest.setPassword("Secure@123");
    }

    @Test
    void register_ShouldSucceed_WhenUserIsNew() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getUsername()).isEqualTo("john_doe");
        assertThat(response.getMessage()).isEqualTo("Registration successful");
        verify(passwordEncoder).encode("Secure@123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrow_WhenUsernameExists() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("john_doe");
    }

    @Test
    void register_ShouldThrow_WhenEmailExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("john@example.com");
    }

    @Test
    void login_ShouldSucceed_WithValidCredentials() {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("john_doe");
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);

        User user = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("$2a$12$hashedPassword")
                .build();
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getUsername()).isEqualTo("john_doe");
    }

    @Test
    void login_ShouldThrow_WithBadCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }
}
