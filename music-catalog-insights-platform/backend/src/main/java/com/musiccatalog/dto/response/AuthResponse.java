package com.musiccatalog.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String name,
        String email,
        long expiresInMillis
) {
    public static AuthResponse of(String token, Long userId, String name, String email, long expiresInMillis) {
        return new AuthResponse(token, "Bearer", userId, name, email, expiresInMillis);
    }
}
