package com.example.authservice.repositories;

import com.example.authservice.enums.AuthProvider;
import com.example.authservice.models.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepo extends JpaRepository<User, String> {
  Optional<User> findByEmail(String email);

  Boolean existsByEmail(String email);

  // OAuth2 related ----------------------------

  Optional<User> findByProviderIdAndAuthProvider(String providerId, AuthProvider authProvider);
  Optional<User> findByEmailAndAuthProvider(String email, AuthProvider authProvider);
  Boolean existsByEmailAndAuthProvider(String email, AuthProvider authProvider);
  // find users by auth provider
  List<User> findByAuthProvider(AuthProvider authProvider);

  // find local users only (for migration)
  List<User> findByAuthProviderIsNull();
}
