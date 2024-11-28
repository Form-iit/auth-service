package com.example.authservice.exceptions.CommonSwaggerApiErrorResponses;

import com.example.authservice.exceptions.GlobalErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
    responseCode = "401",
    content =
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GlobalErrorResponse.class),
            examples = {
              @ExampleObject(
                  name = "FailedEmailVerification",
                  description = "This is the error returned when the email verification fails",
                  value =
                      """
                      {
                        "type": "FailedEmailVerificationException",
                        "message": "Failed to verify email. Token could not be verified.",
                        "status": "UNAUTHORIZED"
                      }
                      """)
            }))
public @interface FailedEmailVerificationResponse {}
