package com.example.authservice.services;

import com.example.authservice.dto.Requests.AuthRequest;
import com.example.authservice.dto.Requests.RegisterRequest;

public interface AuthService {
  void register(RegisterRequest request);

  String login(AuthRequest request);

  void verify();
}
