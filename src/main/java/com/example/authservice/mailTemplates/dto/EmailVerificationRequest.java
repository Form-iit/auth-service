package com.example.authservice.mailTemplates.dto;

import java.io.Serializable;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EmailVerificationRequest implements Serializable {
  private String to;
  private String subject;
  private String content;
}
