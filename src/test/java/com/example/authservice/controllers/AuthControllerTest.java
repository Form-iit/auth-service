package com.example.authservice.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.authservice.configs.ModelMapperConfig;
import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.exceptions.FailedEmailVerification;
import com.example.authservice.exceptions.UserAlreadyExistsException;
import com.example.authservice.services.AuthService;
import com.example.authservice.utils.auth.TestDataUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

  @Mock private AuthService authService;
  @Spy private ModelMapper mapper = new ModelMapperConfig().modelMapper();
  @InjectMocks private AuthController authController;

  @Test
  @DisplayName("Valid user registration")
  public void testValidRegister() {
    // !Arrange
    RegisterRequest registerRequest = TestDataUtil.registerTestRequest();

    // !Mock
    doNothing().when(authService).register(any(RegisterRequest.class));

    // !Act
    ResponseEntity<String> response = authController.register(registerRequest);

    // !Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
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
    public void testValidLogin() throws Exception {
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
    // !Arrange
    String token = "valid.jwt.token";

    // !Mock
    doNothing().when(authService).verify();

    // !Act
    ResponseEntity<String> response = authController.verify(token);

    // ! Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Account enabled successfully", response.getBody());

    // !Verify
    verify(authService, times(1)).verify();
  }

  @Test
  public void unsuccessfulVerification() {
    // !Arrange
    String token = "invalid.jwt.token";

    // !Mock
    doThrow(FailedEmailVerification.class).when(authService).verify();

    // !Act & Assert
    assertThrows(FailedEmailVerification.class, () -> authController.verify(token));

    // !Verify
    verify(authService, times(1)).verify();
    verifyNoMoreInteractions(authService);
  }
}
