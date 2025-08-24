package com.example.authservice.configs.security;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OpenEndpointsConfig {
  @Bean
  public List<String> permittedEndpoints() {
      List<String> endpoints =  List.of(
        "/api/v1/auth/login",
        "/api/v1/auth/register",
        "/error");

      log.trace("Creating permittedEndpoints bean: {}", endpoints);
      return endpoints;
  }
}
