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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = userMapper.toEntity(request, passwordEncoder);
        User saved = userRepository.save(user);

        log.info("New user registered: userId={}", saved.getId());

        String token = jwtService.generateToken(new UserPrincipal(saved), saved.getId());
        return AuthResponse.of(token, saved.getId(), saved.getName(), saved.getEmail(), jwtService.getExpirationMillis());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        log.info("User logged in: userId={}", user.getId());

        String token = jwtService.generateToken(new UserPrincipal(user), user.getId());
        return AuthResponse.of(token, user.getId(), user.getName(), user.getEmail(), jwtService.getExpirationMillis());
    }
}
