package com.example.authservice.services;

import com.example.authservice.dto.UserDto;
import com.example.authservice.exceptions.UserNotFoundException;
import com.example.authservice.models.User;
import com.example.authservice.repositories.AuthRepo;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
  private final AuthRepo authRepo;
  private final ModelMapper mapper;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(AuthRepo authRepo, ModelMapper mapper, PasswordEncoder passwordEncoder) {
    this.authRepo = authRepo;
    this.mapper = mapper;
    this.passwordEncoder = passwordEncoder;
  }

  public String getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getName();
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getUserData() throws UserNotFoundException {
    String userId = getCurrentUserId();
    User user =
        authRepo
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User was not found"));
    return mapper.map(user, UserDto.class);
  }

  @Override
  @Transactional
  public UserDto EditUserData(UserDto userDto) throws UserNotFoundException {
    String userId = getCurrentUserId();

    User user =
        authRepo
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User was not found"));

    // * Update only the fields that were modified in the userDto
    user.setFirstName(
        userDto.getFirstName() != null ? userDto.getFirstName() : user.getFirstName());
    user.setLastName(userDto.getLastName() != null ? userDto.getLastName() : user.getLastName());
    user.setEmail(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail());

    User updatedUser = authRepo.save(user);
    return mapper.map(updatedUser, UserDto.class);
  }

  @Override
  @Transactional
  public String ChangeCurrentUserPassword(String oldPassword, String newPassword)
      throws UserNotFoundException, BadCredentialsException {
    String encodedOldPassword = passwordEncoder.encode(oldPassword);
    String userId = getCurrentUserId();
    User user =
        authRepo
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User was not found"));
    if (!encodedOldPassword.equals(user.getPassword())) {
      throw new BadCredentialsException("Given old password does not match");
    }
    String encodedNewPassword = passwordEncoder.encode(newPassword);
    user.setPassword(encodedNewPassword);
    authRepo.save(user);
    return user.getUsername();
  }

  @Override
  @Transactional
  public String DeleteCurrentUser() throws UserNotFoundException {
    String userId = getCurrentUserId();

    User userToDelete =
        authRepo
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User was not found"));
    authRepo.delete(userToDelete);
    return userToDelete.getUsername();
  }
}
