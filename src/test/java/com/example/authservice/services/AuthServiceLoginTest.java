package com.example.authservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.authservice.configs.security.services.JwtService;
import com.example.authservice.dto.Requests.AuthRequest;
import com.example.authservice.enums.Role;
import com.example.authservice.exceptions.UserNotFoundException;
import com.example.authservice.models.User;
import com.example.authservice.repositories.AuthRepo;
import com.example.authservice.utils.auth.TestDataUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
public class AuthServiceLoginTest {

  @InjectMocks private AuthServiceImpl service;
  @Mock private AuthenticationManager authManager;
  @Mock private AuthRepo authRepo;
  @Mock private JwtService jwtService;

  @Test
  public void loginWithInvalidCredentials() {
    // !Arrange
    AuthRequest authRequest = TestDataUtil.authTestRequest();

    // !Mock
    when(authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(), authRequest.getPassword())))
        .thenThrow(BadCredentialsException.class);

    // ! Act & Assert
    assertThrows(BadCredentialsException.class, () -> service.login(authRequest));
  }

  @Test
  public void loginWithNonExistingEmail() {
    // !Arrange
    AuthRequest authRequest = TestDataUtil.authTestRequest();

    // !Mock
    when(authRepo.findByEmail(authRequest.getEmail())).thenThrow(UserNotFoundException.class);

    // !Act & Assert
    assertThrows(UserNotFoundException.class, () -> service.login(authRequest));
  }

  @Test
  public void successfulLogin() {
    // !Arrange
    AuthRequest authRequest = TestDataUtil.authTestRequest();
    User mockUser =
        User.builder()
            .id("test-id")
            .firstName("John")
            .lastName("Doe")
            .email(authRequest.getEmail())
            .password("encodedPassword")
            .role(Role.USER)
            .build();
    String mockToken = "test_jwt_token";

    // !Mock
    when(authRepo.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(mockUser));
    when(jwtService.generateToken(any(User.class))).thenReturn(mockToken);

    // !Act
    String token = service.login(authRequest);

    // !Assert
    assertEquals(token, mockToken);

    // !verification
    // * Here we verify that those methods were actually called
    verify(authManager)
        .authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(), authRequest.getPassword()));
    verify(authRepo).findByEmail(authRequest.getEmail());
    verify(jwtService).generateToken(mockUser);
  }
}
