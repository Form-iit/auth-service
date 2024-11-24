package com.example.authservice.models;

import com.example.authservice.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@SoftDelete(strategy = SoftDeleteType.DELETED, columnName = "deleted")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String firstName;

  private String lastName;

  @Email
  @Column(unique = true)
  private String email;

  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;

  @CreatedDate private LocalDateTime createdAt;

  @LastModifiedDate private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  @Builder.Default
  private LocalDateTime deletedAt = null;

  @Builder.Default private boolean enabled = false;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getUsername() {
    return this.id;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @PreRemove
  private void preRemove() {
    this.deletedAt = LocalDateTime.now();
    this.enabled = false;
  }
}
