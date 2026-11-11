package com.example.roombooking.identity.internal;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/auth")
class AuthController {
    record RegisterRequest(@NotBlank String email, @NotBlank @Size(max = 72) String password) {}
    record UserResponse(UUID id, String email, Role role) {}
    record LoginRequest(@NotBlank String email, @NotBlank String password) {}
    record TokenResponse(String accessToken, String tokenType, long expiresIn) {}

    private final UserService users;
    private final JwtService jwt;
    AuthController(UserService users, JwtService jwt) { this.users = users; this.jwt = jwt; }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse register(@Valid @RequestBody RegisterRequest r) {
        UserEntity u = users.register(r.email(), r.password(), Role.USER);
        return new UserResponse(u.getId(), u.getEmail(), u.getRole());
    }

     @PostMapping("/login")
    TokenResponse login(@Valid @RequestBody LoginRequest r) {
        UserEntity u = users.authenticate(r.email(), r.password());
        return new TokenResponse(jwt.issue(u), "Bearer", jwt.ttlSeconds());
    }
}
