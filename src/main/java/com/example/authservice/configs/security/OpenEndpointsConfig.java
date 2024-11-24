package com.example.authservice.configs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenEndpointsConfig {
  @Bean
  public List<String> permittedEndpoints() {
    return List.of("/api/v1/auth/login", "/api/v1/auth/register", "/error", "/api/v1/swagger-ui/**", "/api/v1/api-docs/**");
  }
}
