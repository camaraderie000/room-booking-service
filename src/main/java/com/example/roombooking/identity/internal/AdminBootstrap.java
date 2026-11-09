package com.example.roombooking.identity.internal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
class AdminBootstrap implements ApplicationRunner {
    private final UserRepository repo;
    private final UserService users;
    private final String email;
    private final String password;

    AdminBootstrap(UserRepository repo, UserService users,
                   @Value("${app.admin.email}") String email,
                   @Value("${app.admin.password}") String password) {
        this.repo = repo; this.users = users; this.email = email; this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!repo.existsByEmail(email.toLowerCase())) {
            users.register(email, password, Role.ADMIN);
        }
    }
}
