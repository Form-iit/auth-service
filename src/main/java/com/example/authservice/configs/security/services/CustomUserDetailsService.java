package com.example.authservice.configs.security.services;

import com.example.authservice.exceptions.UserNotFoundException;
import com.example.authservice.repositories.AuthRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final AuthRepo repo;

  public CustomUserDetailsService(AuthRepo repo) {
    this.repo = repo;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
    System.out.println("Attempting to load user with email: " + email);
    return repo.findByEmail(email)
        .orElseThrow(
            () -> new UserNotFoundException("No user with the email " + email + " was found"));
  }

  public UserDetails loadUserById(String id) throws UserNotFoundException {
    return repo.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
  }
}
