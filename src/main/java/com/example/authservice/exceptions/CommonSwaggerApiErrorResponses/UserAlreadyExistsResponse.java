package com.example.authservice.exceptions.CommonSwaggerApiErrorResponses;

import com.example.authservice.exceptions.GlobalErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(
    value = {
      @ApiResponse(
          responseCode = "409",
          content =
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = GlobalErrorResponse.class),
                  examples =
                      @ExampleObject(
                          name = "UserAlreadyExistsException",
                          description =
                              "This error response is returned when the user-to-create already"
                                  + " exists",
                          value =
                              """
    {
             "type": "UserAlreadyExistsException",
             "message": "There's already a user with the given email address",
             "status": "CONFLICT"
             }
""")))
    })
public @interface UserAlreadyExistsResponse {}
