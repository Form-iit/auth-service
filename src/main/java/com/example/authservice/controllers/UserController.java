package com.example.authservice.controllers;

import com.example.authservice.dto.Requests.ChangePasswordRequest;
import com.example.authservice.dto.Responses.UserController.UserController_UserDto;
import com.example.authservice.dto.Responses.UserController.UserController_UserID;
import com.example.authservice.dto.UserDto;
import com.example.authservice.exceptions.CommonSwaggerApiErrorResponses.*;
import com.example.authservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@PreAuthorize("hasRole('USER')")
@Slf4j
@Tag(name = "User")
public class UserController {
  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping("/me/get")
  @Operation(description = "Get authenticated user's data")
  @ApiResponse(
      responseCode = "200",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserDto.class)))
  @AuthorizationResponses(includeAll = true)
  @MalformedJWTResponse
  public ResponseEntity<UserDto> GetUser() {
    UserDto userData = service.getUserData();
    return new ResponseEntity<>(userData, HttpStatus.OK);
  }

  @PatchMapping("/me/edit")
  @Operation(description = "Update authenticated user's data")
  @ApiResponse(
      responseCode = "200",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserController_UserDto.class)))
  @MalformedJWTResponse
  @UserAlreadyExistsResponse
  @InvalidMethodArgumentResponse
  @AuthorizationResponses(includeAll = true)
  public ResponseEntity<UserController_UserDto> EditUserData(@Valid @RequestBody UserDto userData) {
    UserDto user = service.EditUserData(userData);
    return new ResponseEntity<>(
        UserController_UserDto.builder()
            .type("UserSuccessfulEdit")
            .status(String.valueOf(HttpStatus.OK))
            .message("User modified successfully")
            .user(user)
            .build(),
        HttpStatus.OK);
  }

  @PatchMapping("/me/edit/password")
  @Operation(description = "Update authenticated user's password")
  @ApiResponse(
      responseCode = "200",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserController_UserID.class)))
  @MalformedJWTResponse
  @InvalidMethodArgumentResponse
  @BadCredentials
  @UserNotFoundResponse
  @AuthorizationResponses(includeAll = true)
  public ResponseEntity<UserController_UserID> EditUserPassword(
      @Valid @RequestBody ChangePasswordRequest request) {
    String userId =
        service.ChangeCurrentUserPassword(request.getOldPassword(), request.getNewPassword());
    return new ResponseEntity<>(
        UserController_UserID.builder()
            .type("UserSuccessfulEdit")
            .status(String.valueOf(HttpStatus.OK))
            .message("Password changed successfully for user ")
            .userId(userId)
            .build(),
        HttpStatus.OK);
  }

  @DeleteMapping("/me/delete")
  @Operation(description = "Delete authenticated user's account")
  @ApiResponse(
      responseCode = "200",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserController_UserID.class)))
  @MalformedJWTResponse
  @InvalidMethodArgumentResponse
  @BadCredentials
  @UserNotFoundResponse
  @AuthorizationResponses(includeAll = true)
  public ResponseEntity<UserController_UserID> DeleteUserAccount() {
    String userId = service.DeleteCurrentUser();
    return new ResponseEntity<>(
        UserController_UserID.builder()
            .type("UserSuccessfulDeletion")
            .status(String.valueOf(HttpStatus.OK))
            .message("User account was deleted successfully")
            .userId(userId)
            .build(),
        HttpStatus.OK);
  }
}
