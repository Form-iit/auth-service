package com.example.authservice.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.authservice.configs.ModelMapperConfig;
import com.example.authservice.dto.ChangePasswordRequest;
import com.example.authservice.dto.UserDto;
import com.example.authservice.exceptions.UserNotFoundException;
import com.example.authservice.models.User;
import com.example.authservice.services.UserService;
import com.example.authservice.utils.user.TestDataUtil;
import java.util.Map;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

  @Mock private UserService userService;
  @Spy private ModelMapper mapper = new ModelMapperConfig().modelMapper();
  @InjectMocks private UserController userController;

  @Nested
  @DisplayName("Get user")
  class GetUserTests {
    private User currentUser;

    @BeforeEach
    void init() {
      // !Arrange
      currentUser = TestDataUtil.currentUser();
    }

    @Test
    @DisplayName("Get connected user data")
    public void getUserData() {
      // !Arrange (continuation)
      UserDto userDto = mapper.map(currentUser, UserDto.class);
      // !Mock
      when(userService.getUserData()).thenReturn(userDto);
      // !Act
      ResponseEntity<Map<String, Object>> response = userController.GetUser();
      // !Assert
      assertEquals(response.getStatusCode(), HttpStatus.OK);
      assertNotNull(response.getBody());
      assertEquals(mapper.map(response.getBody().get("user"), UserDto.class), userDto);
      // !verify
      verify(userService, times(1)).getUserData();
    }

    @Test
    @DisplayName("Error getting connected user data")
    public void GetUserDataError() {
      // !Mock
      when(userService.getUserData()).thenThrow(UserNotFoundException.class);
      // !Act & Assert
      assertThrows(UserNotFoundException.class, () -> userController.GetUser());
      // !verify
      verify(userService, times(1)).getUserData();
    }
  }

  @Nested
  @DisplayName("Edit user")
  class EditUserTests {
    private UserDto userDto;

    @BeforeEach
    void init() {
      // !Arrange
      User currentUser = TestDataUtil.currentUser();
      currentUser.setFirstName("ModifiedFirstName");
      userDto = mapper.map(currentUser, UserDto.class);
    }

    @Test
    @DisplayName("Edit user data")
    public void editUserData() {
      // !Mock
      when(userService.EditUserData(userDto)).thenReturn(userDto);
      // !Act
      ResponseEntity<Map<String, Object>> response = userController.EditUserData(userDto);

      // !Assert
      assertEquals(response.getStatusCode(), HttpStatus.OK);
      assertNotNull(response.getBody());
      assertEquals(((UserDto) response.getBody().get("user")).getFirstName(), "ModifiedFirstName");
      // !verify
      verify(userService, times(1)).EditUserData(userDto);
    }

    @Test
    @DisplayName("UserNotFoundException while updating user data")
    public void UnsuccessfulEditUserData() {
      // !Mock
      when(userService.EditUserData(userDto)).thenThrow(UserNotFoundException.class);
      // !Act && Assert
      assertThrows(UserNotFoundException.class, () -> userController.EditUserData(userDto));
      // !verify
      verify(userService, times(1)).EditUserData(userDto);
    }
  }

  @Nested
  @DisplayName("Change password")
  class ChangeUserPasswordTest {
    private User currentUser;
    private ChangePasswordRequest req;

    @BeforeEach
    void init() {
      // !Arrange
      currentUser = TestDataUtil.currentUser();
      req = new ChangePasswordRequest("NonHashedPass", "newPassword");
    }

    @Test
    @DisplayName("Change user's password")
    public void editUserPassword() {
      // !Mock
      when(userService.ChangeCurrentUserPassword(req.getOldPassword(), req.getNewPassword()))
          .thenReturn(currentUser.getUsername()); // ? the getUsername returns the ID
      // !Act
      ResponseEntity<Map<String, Object>> response = userController.EditUserPassword(req);
      // !Assert
      assertEquals(response.getStatusCode(), HttpStatus.OK);
      assertNotNull(response.getBody());
      assertEquals(response.getBody().get("Id"), currentUser.getUsername());
    }

    @Test
    @DisplayName("Error changing user's password")
    public void PasswordChangeError() {
      // !Mock
      when(userService.ChangeCurrentUserPassword(req.getOldPassword(), req.getNewPassword()))
          .thenThrow(BadCredentialsException.class);
      // !Act & Assert
      assertThrows(BadCredentialsException.class, () -> userController.EditUserPassword(req));
      // !Verify
      verify(userService, times(1))
          .ChangeCurrentUserPassword(req.getOldPassword(), req.getNewPassword());
    }
  }

  @Nested
  @DisplayName("Delete user")
  class DeleteUserTest {
    @Test
    @DisplayName("Delete current user's account")
    public void deleteUser() {
      // !Arrange
      User currentUser = TestDataUtil.currentUser();

      // !Mock
      when(userService.DeleteCurrentUser()).thenReturn(currentUser.getUsername());
      // !Act
      ResponseEntity<Map<String, Object>> response = userController.DeleteUserAccount();
      // !Assert
      assertEquals(response.getStatusCode(), HttpStatus.OK);
      assertNotNull(response.getBody());
      assertEquals(response.getBody().get("Id"), currentUser.getUsername());
      // !Verify
      verify(userService, times(1)).DeleteCurrentUser();
    }

    @Test
    @DisplayName("Unsuccessful user account deletion")
    public void UnsuccessfulDeleteUserAccount() {
      // !Mock
      when(userService.DeleteCurrentUser()).thenThrow(UserNotFoundException.class);
      // !Act & Assert
      assertThrows(UserNotFoundException.class, () -> userController.DeleteUserAccount());
      // !verify
      verify(userService, times(1)).DeleteCurrentUser();
    }
  }
}
