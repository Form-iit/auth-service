package com.example.authservice.dto.Requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
  @NotNull(message = "The Email address should absolutely be provided")
  @Email
  @Schema(
          description = "User's email",
          example = "John.doe@example.com"
  )
  private String email;

  @NotNull(message = "The password should absolutely be provided")
  @Size(min = 8, message = "The password should be at least 8 characters long")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=*!()_/])(?=\\S+$).{8,}$",
      message =
          "The password should contain a least one number, one uppercase letter, one lowercase"
              + " letter and one of the symbols @ # $ % ^ & + = * ! ( ) _ /")
  @Schema(
          description = "User's password",
          example = "DummyPassword_123"
  )
  private String password;
}
