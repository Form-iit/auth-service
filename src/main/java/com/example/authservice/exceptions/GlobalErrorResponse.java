package com.example.authservice.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@Schema(description = "The global error schema")
public class GlobalErrorResponse {
  @Schema(description = "The type of the exception")
  private String type;

  @Schema(description = "A detailed error message")
  private String message;

  @Schema(description = "HTTP status code")
  private HttpStatus status;
}
