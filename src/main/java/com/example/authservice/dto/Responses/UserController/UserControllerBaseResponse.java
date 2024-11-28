package com.example.authservice.dto.Responses.UserController;

import com.example.authservice.dto.Responses.BaseResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class UserControllerBaseResponse implements BaseResponse {
  private String status;
  private String message;
  private String type;
}
