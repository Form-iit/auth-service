package com.example.authservice.dto.Responses.UserController;


import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class UserController_UserID extends  UserControllerBaseResponse{
    private String userId;
}
