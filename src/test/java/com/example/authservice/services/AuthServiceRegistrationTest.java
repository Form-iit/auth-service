package com.example.authservice.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.authservice.configs.security.services.JwtService;
import com.example.authservice.dto.Requests.RegisterRequest;
import com.example.authservice.enums.Role;
import com.example.authservice.exceptions.FailedEmailVerification;
import com.example.authservice.exceptions.UserAlreadyExistsException;
import com.example.authservice.models.User;
import com.example.authservice.producers.MailsProducer;
import com.example.authservice.repositories.AuthRepo;
import com.example.authservice.utils.auth.TestDataUtil;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceRegistrationTest {
  @Mock private JwtService jwtService;
  @Mock private AuthRepo authRepo;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private MailsProducer mailer;
  @InjectMocks private AuthServiceImpl service;

  private User mockedUser;

  @BeforeEach
  public void setUp() {
    mockedUser =
        User.builder()
            .id("TestID")
            .firstName("fName")
            .lastName("LName")
            .email("test@gmail.com")
            .role(Role.USER)
            .build(); // Create a mock user object
  }

  @Test
  public void testRegisterWithExistingEmail() {
    // !Arrange
    // ? Get the registration request
    RegisterRequest registrationReq = TestDataUtil.registerTestRequest();

    // !Mock
    // ? Mock the existsByEmail method within the authRepo. which means whenever the existsByEmail
    // gets executed with the registrationReq.getEmail() as it's param, return true
    when(authRepo.existsByEmail(registrationReq.getEmail())).thenReturn(Boolean.TRUE);

    // ! Act and Assert
    // => This will be our assertion. We will be calling the register method, then we will be
    // checking for the exception
    UserAlreadyExistsException ex =
        assertThrows(UserAlreadyExistsException.class, () -> service.register(registrationReq));
    String ExpectedMessage = "There's already a user with the given email address";
    String ActualMessage = ex.getMessage();
    assertEquals(ExpectedMessage, ActualMessage);
    verify(authRepo, times(1)).existsByEmail(registrationReq.getEmail());
  }

  @Test
  public void testValidRegistration() {
    RegisterRequest registrationReq = TestDataUtil.registerTestRequest();

    // !Mock
    // ? Mock the return of the existByEmail method
    when(authRepo.existsByEmail(registrationReq.getEmail())).thenReturn(Boolean.FALSE);
    when(passwordEncoder.encode(registrationReq.getPassword())).thenReturn("encodedPassword");

    when(authRepo.save(any(User.class))).thenReturn(mockedUser);
    doNothing().when(mailer).sendEmail(any(String.class), any(String.class), any(String.class));
    // ? Mocking the JWT
    // ? Using the any(User,class) to avoid stubbing errors
    String mockedJWT = "testJwtMock";
    when(jwtService.generateToken(any(User.class))).thenReturn(mockedJWT);

    // ! Act
    service.register(registrationReq);

    // * here we are verifying the interactions, meaning that the mocked methods were actually
    // called or not
    verify(authRepo).existsByEmail(registrationReq.getEmail());
    verify(authRepo).save(any(User.class));
    verify(jwtService).generateToken(any(User.class));
    verify(mailer)
        .sendEmail(eq(registrationReq.getEmail()), eq("Verify your email address"), anyString());
    // * here we verify that aside from the interactions above, no more interactions were made with
    // the authRepo and the jwtService
    verifyNoMoreInteractions(authRepo, jwtService, mailer);
  }

  @Nested
  class EmailVerification {
    @Mock private Authentication authObj;
    @Mock private SecurityContext context;

    @BeforeEach
    public void init() {
      when(context.getAuthentication()).thenReturn(authObj);
      when(authObj.getPrincipal()).thenReturn(mockedUser);
      SecurityContextHolder.setContext(context);
    }

    @AfterEach
    public void resetSecurityContext() {
      SecurityContextHolder.clearContext();
    }

    @Test
    public void testValidEmailVerification() {
      // !Mock
      when(authRepo.findById(any(String.class))).thenReturn(Optional.ofNullable(mockedUser));
      when(authRepo.save(any(User.class))).thenReturn(mockedUser);

      // !Act
      service.verify();

      // !assert
      assertNotNull(mockedUser);
      assertTrue(mockedUser.isEnabled());

      // !Verify
      verify(authRepo, times(1)).findById(any(String.class));
      verify(authRepo, times(1)).save(any(User.class));
      verifyNoMoreInteractions(authRepo);
    }

    @Test
    public void testInvalidEmailRegistration() {
      // !Mock
      when(authRepo.findById(any(String.class))).thenThrow(FailedEmailVerification.class);

      // !Act
      assertThrows(FailedEmailVerification.class, () -> service.verify());

      // !Assert
      assertNotNull(mockedUser);
      assertFalse(mockedUser.isEnabled());

      // !Verify
      verify(authRepo, times(1)).findById(any(String.class));
      verify(authRepo, times(0)).save(any(User.class));
    }
  }
}
