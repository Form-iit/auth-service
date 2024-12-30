package com.example.authservice.dto.Responses.AuthController;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
  @Schema(description = "Json Web Token")
  private String token;
}
