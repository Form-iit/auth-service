package com.example.authservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordRequest {
  @NotNull(message = "The user's old password should absolutely be provided")
  @Size(min = 8, message = "The password should be at least 8 characters long")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=*!()_/])(?=\\S+$).{8,}$",
      message =
          "The password should contain a least one number, one uppercase letter, one lowercase"
              + " letter and one of the symbols @ # $ % ^ & + = * ! ( ) _ /")
  String oldPassword;

  @NotNull(message = "The user's new password should absolutely be provided")
  @Size(min = 8, message = "The password should be at least 8 characters long")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=*!()_/])(?=\\S+$).{8,}$",
      message =
          "The password should contain a least one number, one uppercase letter, one lowercase"
              + " letter and one of the symbols @ # $ % ^ & + = * ! ( ) _ /")
  String newPassword;
}
