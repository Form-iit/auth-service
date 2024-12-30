package com.example.authservice.dto.Responses.UserController;

import com.example.authservice.dto.UserDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class UserController_UserDto extends UserControllerBaseResponse {
  private UserDto user;
}
