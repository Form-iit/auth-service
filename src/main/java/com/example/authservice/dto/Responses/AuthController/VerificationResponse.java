package com.example.authservice.dto.Responses.AuthController;

import com.example.authservice.dto.Responses.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class VerificationResponse implements BaseResponse {
  @Builder.Default private String status = "OK";
  @Builder.Default private String message = "Account enabled successfully!";
  @Builder.Default private String type = "VerificationResponse";
}
