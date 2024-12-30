package com.example.authservice.mailTemplates.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EmailVerificationRequest implements Serializable {
  @Builder.Default()
  private final String correlationId = java.util.UUID.randomUUID().toString();
  private String to;
  private String subject;
  private String content;
}
