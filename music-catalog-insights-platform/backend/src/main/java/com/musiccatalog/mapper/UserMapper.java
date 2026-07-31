package com.musiccatalog.mapper;

import com.musiccatalog.dto.request.RegisterRequest;
import com.musiccatalog.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request, PasswordEncoder passwordEncoder) {
        return User.builder()
                .name(request.name())
                .email(request.email().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .build();
    }
}
