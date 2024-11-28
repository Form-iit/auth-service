package com.example.authservice.dto.Requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegisterRequest {
  @NotNull(message = "The first name should absolutely be provided")
  @Size(min = 2, message = "The first name should be at least 2 characters long")
  @Schema(description = "User's first name", example = "John")
  private String firstname;

  @NotNull(message = "The last name should absolutely be provided")
  @Size(min = 2, message = "The last name should be at least 2 characters long")
  @Schema(description = "User's last name", example = "Doe")
  private String lastname;

  @NotNull(message = "The user's email should be provided")
  @Email(message = "A valid email should be provided")
  @Schema(description = "User's email", example = "John.doe@example.com")
  private String email;

  @NotNull(message = "The user's password should absolutely be provided")
  @Size(min = 8, message = "The password should be at least 8 characters long")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=*!()_/])(?=\\S+$).{8,}$",
      message =
          "The password should contain a least one number, one uppercase letter, one lowercase"
              + " letter and one of the symbols @ # $ % ^ & + = * ! ( ) _ /")
  @Schema(description = "User's password", example = "DummyPassword_123")
  private String password;
}
