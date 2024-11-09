package com.example.authservice.services;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    String login(AuthRequest request);
    void verify();
}
