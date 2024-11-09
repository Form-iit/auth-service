package com.example.authservice.mailTemplates.dto;

import lombok.*;

import java.io.Serializable;

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
