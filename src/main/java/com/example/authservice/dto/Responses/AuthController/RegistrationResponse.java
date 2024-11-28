package com.example.authservice.dto.Responses.AuthController;

import com.example.authservice.dto.Responses.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RegistrationResponse implements BaseResponse {
  @Builder.Default private String status = "CREATED";

  @Builder.Default
  private String message = "Please verify your email inbox to complete the registration!";

  @Builder.Default private String type = "RegistrationResponse";
}
