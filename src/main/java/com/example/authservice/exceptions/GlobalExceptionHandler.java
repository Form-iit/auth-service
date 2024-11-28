package com.example.authservice.exceptions;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler({
    UserNotFoundException.class,
    UserAlreadyExistsException.class,
    AuthenticationException.class,
    JwtException.class,
    RoleNotFoundException.class,
    AccessDeniedException.class,
    NoResourceFoundException.class,
    FailedEmailVerification.class
  })
  public ResponseEntity<Object> handleCustomExceptions(Exception ex) {
    // ? Unwrap the exception if it's an InternalAuthenticationServiceEx to expose the underlying
    // cause which is EmailNotFoundEx
    Throwable cause = ex;
    while (cause.getCause() != null) {
      cause = cause.getCause();
    }

    String exceptionClassName = cause.getClass().getSimpleName();

    HttpStatus status =
        switch (exceptionClassName) {
          case "UserNotFoundException", "NoResourceFoundException", "FailedEmailVerification" -> HttpStatus.NOT_FOUND;
          case "UserAlreadyExistsException" -> HttpStatus.CONFLICT;
          case "BadCredentialsException",
                  "ExpiredJwtException",
                  "SignatureException",
                  "InsufficientAuthenticationException",
                  "AccessDeniedException",
                  "AuthorizationDeniedException"->
              HttpStatus.UNAUTHORIZED;
          default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
      GlobalErrorResponse responseBody = GlobalErrorResponse.builder().type(exceptionClassName).message(ex.getMessage()).status(status).build();
    return new ResponseEntity<>(responseBody, status);
  }

  // ? handling the @Valid exceptions
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleTypesMismatch(MethodArgumentNotValidException ex) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("status", status);
    responseBody.put("type", "MethodArgumentNotValidException");
    responseBody.put("message", "Validation failed for one or more fields.");

    // ? Creates a map to list the validation errors
    Map<String, List<String>> errors = new HashMap<>();
    /*
        ? For each validation error we extract the fieldName 'the name of the concerned field', then the error message.
        ? After that, we take the errors map and check if the fieldName is absent, we create it as a new ArrayList
        ? but if the fieldName is already present, since it is an Array list we just add the currentError msg to its errors list
    */
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            fieldError -> {
              String fieldName = fieldError.getField();
              String errorMessage = fieldError.getDefaultMessage();
              errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
            });

    responseBody.put("errors", errors);

    return new ResponseEntity<>(responseBody, status);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Object> handleEmptyBody(HttpMessageNotReadableException ex) {
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("type", ex.getClass().getSimpleName());
    responseBody.put("message", "Required request body is missing");
    HttpStatus status = HttpStatus.BAD_REQUEST;
    responseBody.put("status", status);
    return new ResponseEntity<>(responseBody, status);
  }

  @ExceptionHandler({Exception.class, RuntimeException.class})
  public ResponseEntity<Object> handleAnyException(Exception ex) {
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("type", ex.getClass().getSimpleName());
    responseBody.put("message", "An unexpected error occurred: " + ex.getMessage());
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    responseBody.put("status", status);
    return new ResponseEntity<>(responseBody, status);
  }
}
