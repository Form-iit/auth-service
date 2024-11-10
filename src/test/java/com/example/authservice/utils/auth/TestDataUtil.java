package com.example.authservice.utils.auth;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.RegisterRequest;

public class TestDataUtil {

  public static AuthRequest authTestRequest() {
    AuthRequest authRequest = new AuthRequest();
    authRequest.setEmail("test@test.com");
    authRequest.setPassword("Test@1234");
    return authRequest;
  }

  public static RegisterRequest registerTestRequest() {
    return RegisterRequest.builder()
        .firstname("test")
        .lastname("user")
        .email("test@test.com")
        .password("test")
        .build();
  }

  private TestDataUtil() {}
}
