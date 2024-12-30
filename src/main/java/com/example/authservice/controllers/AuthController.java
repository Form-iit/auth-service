package com.example.authservice.controllers;

import static com.example.authservice.exceptions.CommonSwaggerApiErrorResponses.AuthorizationResponses.ExampleType.EXPIRED_JWT;
import static com.example.authservice.exceptions.CommonSwaggerApiErrorResponses.AuthorizationResponses.ExampleType.INSUFFICIENT_AUTHENTICATION;

import com.example.authservice.dto.Requests.AuthRequest;
import com.example.authservice.dto.Requests.RegisterRequest;
import com.example.authservice.dto.Responses.AuthController.AuthResponse;
import com.example.authservice.dto.Responses.AuthController.RegistrationResponse;
import com.example.authservice.dto.Responses.AuthController.VerificationResponse;
import com.example.authservice.exceptions.CommonSwaggerApiErrorResponses.*;
import com.example.authservice.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@Tag(name = "Auth")
public class AuthController {

  private final AuthService service;

  public AuthController(AuthService authService) {
    this.service = authService;
  }

  @PostMapping("/register")
  @Operation(description = "Creates user's account")
  @ApiResponse(
      responseCode = "200",
      description = "The response of successful account registration",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = RegistrationResponse.class),
            examples =
                @ExampleObject(
                    name = "RegistrationResponse",
                    description =
                        "This is the returned response when an account gets registered"
                            + " successfully",
                    value =
                        """
                          {
                              "type": "RegistrationResponse",
                        "message": "Please verify your email address :D",
                        "status": "CREATED"
                          }
                        """))
      })
  @UserAlreadyExistsResponse
  @InvalidMethodArgumentResponse
  public ResponseEntity<RegistrationResponse> register(
      @Valid @RequestBody RegisterRequest request) {
    log.info("Register controller reached");
    service.register(request);
    return new ResponseEntity<>(RegistrationResponse.builder().build(), HttpStatus.CREATED);
  }

  @GetMapping("/verify")
  @Operation(description = "Activates user's account")
  @ApiResponse(
      responseCode = "200",
      description = "The response of successful account activation",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = VerificationResponse.class),
            examples =
                @ExampleObject(
                    name = "VerificationResponse",
                    description =
                        "This is the returned response when an account gets verified successfully",
                    value =
                        """
                          {
                              "type": "VerificationResponse",
                        "message": "Account enabled successfully!",
                        "status": "OK"
                          }
                        """))
      })
  @AuthorizationResponses(example = {INSUFFICIENT_AUTHENTICATION, EXPIRED_JWT})
  @MalformedJWTResponse
  @FailedEmailVerificationResponse
  public ResponseEntity<VerificationResponse> verify() {
    log.info("Verifying email address ...");
    service.verify();
    return new ResponseEntity<>(VerificationResponse.builder().build(), HttpStatus.OK);
  }

  @PostMapping("/login")
  @Operation(
      description = "Log users in",
      responses = {
        @ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class)))
      })
  @UserNotFoundResponse
  @BadCredentials
  @InvalidMethodArgumentResponse
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    log.info("Login controller reached");
    final String token = service.login(request);
    return new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK);
  }
}
