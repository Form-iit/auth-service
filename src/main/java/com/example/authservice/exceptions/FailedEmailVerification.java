package com.example.authservice.exceptions;

public class FailedEmailVerification extends RuntimeException {
  public FailedEmailVerification(String message) {
    super(message);
  }
}
