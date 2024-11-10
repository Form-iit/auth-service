package com.example.authservice.repositories;

import com.example.authservice.models.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepo extends JpaRepository<User, String> {
  Optional<User> findByEmail(String email);

  Boolean existsByEmail(String email);
}
