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
    responseCode = "404",
    content =
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GlobalErrorResponse.class),
            examples = {
              @ExampleObject(
                  name = "UserNotFoundException",
                  description =
                      "This error response is returned when the concerned user account doesn't"
                          + " exist",
                  value =
                      """
                      {
                                          "type": "UserNotFoundException",
                                  "message": "No user with the email test@example.com was found",
                                  "status": "NOT_FOUND"
                                  }
                      """)
            }))
public @interface UserNotFoundResponse {}
