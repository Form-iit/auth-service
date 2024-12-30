package com.example.authservice.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.authservice.dto.Requests.AuthRequest;
import com.example.authservice.dto.Requests.RegisterRequest;
import com.example.authservice.dto.Responses.AuthController.AuthResponse;
import com.example.authservice.dto.Responses.AuthController.RegistrationResponse;
import com.example.authservice.dto.Responses.AuthController.VerificationResponse;
import com.example.authservice.exceptions.FailedEmailVerification;
import com.example.authservice.exceptions.UserAlreadyExistsException;
import com.example.authservice.services.AuthService;
import com.example.authservice.utils.auth.TestDataUtil;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

  @Mock private AuthService authService;
  @InjectMocks private AuthController authController;

  @Test
  @DisplayName("Valid user registration")
  public void testValidRegister() {
    // !Arrange
    RegisterRequest registerRequest = TestDataUtil.registerTestRequest();

    // !Mock
    doNothing().when(authService).register(any(RegisterRequest.class));

    // !Act
    ResponseEntity<RegistrationResponse> response = authController.register(registerRequest);

    // !Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(
        "Please verify your email inbox to complete the registration!",
        response.getBody().getMessage());
    assertNotNull(response.getBody());

    // !Verify
    verify(authService, times(1)).register(registerRequest);
  }

  @Test
  @DisplayName("User Already existing")
  public void testExistingUserRegistration() {
    RegisterRequest registerRequest = TestDataUtil.registerTestRequest();

    // !Mock
    doThrow(new UserAlreadyExistsException("User already exists"))
        .when(authService)
        .register(any(RegisterRequest.class));

    // !Act
    assertThrows(UserAlreadyExistsException.class, () -> authController.register(registerRequest));

    // !Verify
    verify(authService, times(1)).register(registerRequest);
  }

  @Nested
  @DisplayName("Login tests")
  class LoginTest {

    @Test
    @DisplayName("Valid user login")
    public void testValidLogin() {
      // !Arrange
      AuthRequest authRequest = TestDataUtil.authTestRequest();
      String expectedToken = "valid.jwt.token";

      // !Mock
      when(authService.login(authRequest)).thenReturn(expectedToken);

      // !Act
      ResponseEntity<AuthResponse> response = authController.login(authRequest);

      // !Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(expectedToken, response.getBody().getToken());

      // !Verify
      verify(authService, times(1)).login(authRequest);
    }

    @Test
    @DisplayName("Bad credentials login")
    public void testLoginWithBadCredentials() {
      // !Arrange
      AuthRequest authRequest = TestDataUtil.authTestRequest();

      // !Mock
      when(authService.login(authRequest))
          .thenThrow(new BadCredentialsException("Bad credentials"));

      // ! Act
      ResponseEntity<AuthResponse> response = null;
      try {
        response = authController.login(authRequest);
      } catch (BadCredentialsException ex) {
        // ! Assert - Check that BadCredentialsException was thrown
        assertEquals("Bad credentials", ex.getMessage());
      }

      verify(authService, times(1)).login(authRequest);
      assertNull(response);
    }
  }

  @Test
  public void successfulVerification() {
    // !Mock
    doNothing().when(authService).verify();

    // !Act
    ResponseEntity<VerificationResponse> response = authController.verify();

    // ! Assert
    log.trace(Objects.requireNonNull(response.getBody()).toString());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Account enabled successfully!", response.getBody().getMessage());
    // !Verify
    verify(authService, times(1)).verify();
  }

  @Test
  public void unsuccessfulVerification() {
    // !Mock
    doThrow(FailedEmailVerification.class).when(authService).verify();

    // !Act & Assert
    assertThrows(FailedEmailVerification.class, () -> authController.verify());

    // !Verify
    verify(authService, times(1)).verify();
    verifyNoMoreInteractions(authService);
  }
}
