package com.example.authservice.services;


import com.example.authservice.dto.UserDto;
import com.example.authservice.exceptions.UserNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;

public interface UserService {
    UserDto getUserData() throws UserNotFoundException;
    UserDto EditUserData(UserDto userDto) throws UserNotFoundException;
    String ChangeCurrentUserPassword(String oldPassword, String newPassword) throws UserNotFoundException, BadCredentialsException;
    String DeleteCurrentUser() throws UserNotFoundException;
}
