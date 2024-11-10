package com.example.authservice.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.authservice.configs.ModelMapperConfig;
import com.example.authservice.dto.UserDto;
import com.example.authservice.exceptions.UserNotFoundException;
import com.example.authservice.models.User;
import com.example.authservice.repositories.AuthRepo;
import com.example.authservice.utils.user.TestDataUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock private AuthRepo authRepo;

  @InjectMocks private UserServiceImpl userService;

  @Spy private ModelMapper mapper = new ModelMapperConfig().modelMapper();

  @Mock private PasswordEncoder passwordEncoder;

  private User currentUser;

  @BeforeEach
  void init() {
    // !Arrange
    currentUser = TestDataUtil.currentUser();
    // !Mock
    Authentication authentication = mock(Authentication.class);
    when(authentication.getName()).thenReturn(currentUser.getId());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Nested
  @DisplayName("User data retrieval")
  class UserData {

    @Test
    @DisplayName("Get user's data")
    public void getUserData() {
      // !Mock
      when(authRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));

      // !Act
      UserDto userData = userService.getUserData();

      // !Assert
      assertEquals(userData, mapper.map(currentUser, UserDto.class));

      // !Verify
      verify(authRepo, times(1)).findById(currentUser.getId());
    }

    @Test
    @DisplayName("User was not found")
    public void UserDataNotFound() {
      // !Mock
      when(authRepo.findById(currentUser.getId()))
          .thenThrow(new UserNotFoundException("User not found"));

      // !Act & Assert
      UserNotFoundException ex =
          assertThrows(UserNotFoundException.class, () -> userService.getUserData());
      String exceptionMessage = ex.getMessage();
      String expectedMessage = "User not found";
      assertEquals(expectedMessage, exceptionMessage);

      // !Verify
      verify(authRepo, times(1)).findById(currentUser.getId());
    }
  }

  @Nested
  @DisplayName("Update user's data")
  class UpdateUserData {
    private UserDto inputUserDto;

    @BeforeEach
    void init() {
      // !Arrange
      inputUserDto = mapper.map(currentUser, UserDto.class);
      inputUserDto.setFirstName("modifiedFirstName");
    }

    @Test
    @DisplayName("Successful modification of user's data")
    public void SuccessfulEditUserDataTest() {
      // !Arrange (continuation)
      User updatedUser = mapper.map(inputUserDto, User.class);
      updatedUser.setPassword(currentUser.getPassword()); // * Ensure password is preserved
      updatedUser.setRole(currentUser.getRole()); // * Ensure role is preserved

      // ! Mock
      when(authRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
      when(authRepo.save(any(User.class))).thenReturn(updatedUser);

      // ! Act
      UserDto resultUserDto = userService.EditUserData(inputUserDto);

      // ! Assert
      assertEquals("modifiedFirstName", resultUserDto.getFirstName());
      assertEquals(currentUser.getLastName(), resultUserDto.getLastName());
      assertEquals(currentUser.getEmail(), resultUserDto.getEmail());

      // ! Verify
      verify(authRepo, times(1)).findById(currentUser.getId());
      verify(authRepo, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("User to edit was not found")
    public void FailedEditUserDataTest() {
      // !mock
      when(authRepo.findById(currentUser.getId()))
          .thenThrow(new UserNotFoundException("User not found"));

      // !Act & Assert
      UserNotFoundException ex =
          assertThrows(UserNotFoundException.class, () -> userService.EditUserData(inputUserDto));
      assertEquals("User not found", ex.getMessage());
      verify(authRepo, times(1)).findById(currentUser.getId());
    }
  }

  @Nested
  @DisplayName("Change user's password")
  class ChangeUserPassword {
    private String oldPassword;
    private String newPassword;

    @BeforeEach
    void init() {
      // !Arrange
      oldPassword = "oldPassword";
      newPassword = "newPassword";
    }

    @Test
    @DisplayName("Successful change of user's password")
    public void SuccessfulChangePasswordTest() {
      // !Arrange (continuation)
      String encodedOldPassword =
          currentUser.getPassword(); // * This represents the current password in the DB (encoded)
      String encodedNewPassword = "EncodedNewPass";

      // !Mock
      when(authRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
      when(passwordEncoder.encode(oldPassword)).thenReturn(encodedOldPassword);
      when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
      when(authRepo.save(any(User.class))).thenReturn(TestDataUtil.currentUser());

      // !Act
      String userId = userService.ChangeCurrentUserPassword(oldPassword, newPassword);

      // !Assert
      verify(authRepo, times(1)).findById(currentUser.getId());
      verify(authRepo, times(1)).save(argThat(arg -> arg.getPassword().equals(encodedNewPassword)));
      assert (userId != null);
      assert (currentUser.getUsername().equals(userId));
    }

    @Test
    @DisplayName("User was not found")
    public void UserNotFoundTest() {
      // !Mock
      when(authRepo.findById(currentUser.getId()))
          .thenThrow(new UserNotFoundException("User not found"));

      // !Act
      UserNotFoundException ex =
          assertThrows(
              UserNotFoundException.class,
              () -> userService.ChangeCurrentUserPassword(oldPassword, newPassword));

      // !Assert
      assertEquals("User not found", ex.getMessage());
      verify(authRepo, times(1)).findById(currentUser.getId());
      verify(authRepo, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("Inputted old password don't match with current password")
    public void OldPasswordNotMatching() {
      // !Mock
      when(passwordEncoder.encode(oldPassword)).thenReturn("UnmatchedPassword@1234");
      when(authRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));

      // !Act & Assert
      BadCredentialsException ex =
          assertThrows(
              BadCredentialsException.class,
              () -> userService.ChangeCurrentUserPassword(oldPassword, newPassword));

      assertEquals(ex.getMessage(), "Given old password does not match");

      // !Verify
      verify(authRepo, times(1)).findById(currentUser.getId());
      verify(authRepo, times(0)).save(any(User.class));
    }
  }

  @Nested
  @DisplayName("Delete current user")
  class DeleteUser {
    @Test
    @DisplayName("Successful deletion")
    public void DeleteCurrentUser() {
      // !Mock
      when(authRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
      doNothing().when(authRepo).delete(any(User.class));

      // !Act
      String userId = userService.DeleteCurrentUser();

      // !Assert
      assertEquals(currentUser.getUsername(), userId);

      // !Verify
      verify(authRepo, times(1)).findById(currentUser.getId());
      verify(authRepo, times(1)).delete(argThat(arg -> arg.getId().equals(currentUser.getId())));
    }

    @Test
    @DisplayName("Failed Deletion (user not found)")
    public void UserNotFound() {
      // !Mock
      when(authRepo.findById(currentUser.getId()))
          .thenThrow(new UserNotFoundException("User not found"));

      // !Act & Assert
      UserNotFoundException ex =
          assertThrows(UserNotFoundException.class, () -> userService.DeleteCurrentUser());
      assertEquals("User not found", ex.getMessage());

      // !Verify
      verify(authRepo, times(1)).findById(currentUser.getId());
      verify(authRepo, times(0)).save(any(User.class));
    }
  }
}
