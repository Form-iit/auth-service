package com.example.authservice.controllers;

import com.example.authservice.dto.ChangePasswordRequest;
import com.example.authservice.dto.UserDto;
import com.example.authservice.services.UserService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
  private final UserService service;
  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping("/me/get")
  public ResponseEntity<Map<String, Object>> GetUser() {
    logger.info("Reached user controller");
    UserDto userData = service.getUserData();
    return new ResponseEntity<>(MakeResponseObject("user", userData, null), HttpStatus.OK);
  }

  @PatchMapping("/me/edit")
  public ResponseEntity<Map<String, Object>> EditUserData(@Valid @RequestBody UserDto userData) {
    UserDto user = service.EditUserData(userData);
    return new ResponseEntity<>(
        MakeResponseObject("user", user, "User modified successfully"), HttpStatus.OK);
  }

  @PatchMapping("/me/edit/password")
  public ResponseEntity<Map<String, Object>> EditUserPassword(
      @Valid @RequestBody ChangePasswordRequest request) {
    String userId =
        service.ChangeCurrentUserPassword(request.getOldPassword(), request.getNewPassword());
    return new ResponseEntity<>(
        MakeResponseObject("Id", userId, "Password changed successfully"), HttpStatus.OK);
  }

  @DeleteMapping("/me/delete")
  public ResponseEntity<Map<String, Object>> DeleteUserAccount() {
    String userId = service.DeleteCurrentUser();
    return new ResponseEntity<>(
        MakeResponseObject("Id", userId, "User account was deleted successfully"), HttpStatus.OK);
  }

  private Map<String, Object> MakeResponseObject(String key, Object value, String message) {
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put(key, value);
    if (message != null) responseBody.put("message", message);
    responseBody.put("status", HttpStatus.OK);
    return responseBody;
  }
}
