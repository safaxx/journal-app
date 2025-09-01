package com.portfolio.journalApp.service;

import com.portfolio.journalApp.entity.User;
import com.portfolio.journalApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${app.admin.username}")
    private String defaultAdminUsername;

    @Value("${app.admin.password}")
    private String defaultAdminPassword;

    @Value("${app.admin.email}")
    private String defaultAdminEmail;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdminIfNotExists();
    }

    private void createDefaultAdminIfNotExists() {
        List<User> allUsers = userRepository.findAll();
        boolean adminExists = allUsers.stream()
                .anyMatch(user -> user.getRoles() != null && user.getRoles().contains("ADMIN"));

        if (!adminExists) {
            log.info("No admin user found. Creating default admin user...");

            if (userRepository.findByUsername(defaultAdminUsername) != null) {
                log.warn("Default admin username '{}' already exists but user is not admin. Skipping admin creation.", defaultAdminUsername);
                return;
            }

            User adminUser = new User();
            adminUser.setUsername(defaultAdminUsername);
            adminUser.setPassword(defaultAdminPassword);
            adminUser.setEmail(defaultAdminEmail);

            User createdAdmin = userService.saveAdminUser(adminUser);
            if (createdAdmin != null) {
                log.info("Default admin user created successfully with username: {}", defaultAdminUsername);
                log.info("Please change the default password after first login!");
            } else {
                log.error("Failed to create default admin user");
            }
        } else {
            log.info("Admin user already exists. Skipping admin creation.");
        }
    }
}