package com.example.authservice.utils.user;

import com.example.authservice.enums.Role;
import com.example.authservice.models.User;

import java.util.UUID;

public class TestDataUtil {
    public static User currentUser() {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .password("Test@123")
                .role(Role.USER)
                .build();
    }

}
