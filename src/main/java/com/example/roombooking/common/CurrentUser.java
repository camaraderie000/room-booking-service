package com.example.roombooking.common;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;

public record CurrentUser(UUID id, boolean admin) {
    public static CurrentUser from(Jwt jwt) {
        return new CurrentUser(UUID.fromString(jwt.getSubject()),
                "ADMIN".equals(jwt.getClaimAsString("role")));
    }
}
