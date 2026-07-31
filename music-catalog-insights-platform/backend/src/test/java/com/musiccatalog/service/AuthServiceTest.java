package com.musiccatalog.service;

import com.musiccatalog.dto.request.LoginRequest;
import com.musiccatalog.dto.request.RegisterRequest;
import com.musiccatalog.dto.response.AuthResponse;
import com.musiccatalog.entity.User;
import com.musiccatalog.exception.DuplicateResourceException;
import com.musiccatalog.exception.InvalidCredentialsException;
import com.musiccatalog.mapper.UserMapper;
import com.musiccatalog.repository.UserRepository;
import com.musiccatalog.security.JwtService;
import com.musiccatalog.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = User.builder()
                .id(1L)
                .name("Ada Lovelace")
                .email("ada@example.com")
                .password("hashed-password")
                .build();
    }

    @Test
    void register_shouldCreateUser_whenEmailNotTaken() {
        RegisterRequest request = new RegisterRequest("Ada Lovelace", "ada@example.com", "SecurePass123");

        when(userRepository.existsByEmail("ada@example.com")).thenReturn(false);
        when(userMapper.toEntity(request, passwordEncoder)).thenReturn(existingUser);
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(jwtService.generateToken(any(UserPrincipal.class), eq(1L))).thenReturn("jwt-token");
        when(jwtService.getExpirationMillis()).thenReturn(86400000L);

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo("ada@example.com");
        verify(userRepository).save(existingUser);
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("Ada", "ada@example.com", "SecurePass123");
        when(userRepository.existsByEmail("ada@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("ada@example.com", "SecurePass123");

        when(userRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("SecurePass123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(any(UserPrincipal.class), eq(1L))).thenReturn("jwt-token");
        when(jwtService.getExpirationMillis()).thenReturn(86400000L);

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt-token");
    }

    @Test
    void login_shouldThrow_whenPasswordIsWrong() {
        LoginRequest request = new LoginRequest("ada@example.com", "WrongPassword");

        when(userRepository.findByEmail("ada@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_shouldThrow_whenUserDoesNotExist() {
        LoginRequest request = new LoginRequest("unknown@example.com", "Password123");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    private static Long eq(Long value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
}
