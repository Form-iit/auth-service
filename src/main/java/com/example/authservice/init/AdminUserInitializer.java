package com.example.authservice.init;

import com.example.authservice.enums.Role;
import com.example.authservice.models.User;
import com.example.authservice.repositories.AuthRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {
  private final AuthRepo authRepo;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.security.default.admin.email}")
  private String email;

  @Value("${app.security.default.admin.password}")
  private String password;

  public AdminUserInitializer(AuthRepo authRepo, PasswordEncoder passwordEncoder) {
    this.authRepo = authRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) throws Exception {
    if (!authRepo.existsByEmail(email)) {
      // Create and save the admin user
      User adminUser =
          User.builder()
              .firstName("test")
              .lastName("test")
              .email(email)
              .password(passwordEncoder.encode(password))
              .role(Role.ADMIN)
              .enabled(true)
              .build();

      authRepo.save(adminUser);
      System.out.println("Admin user created");
    }
  }
}
