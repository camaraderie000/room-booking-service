package com.example.roombooking.identity.internal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo; this.encoder = encoder;
    }

    @Transactional
    UserEntity register(String email, String rawPassword, Role role) {
        return repo.saveAndFlush(new UserEntity(
                email.trim().toLowerCase(), encoder.encode(rawPassword), role));
    }
}
