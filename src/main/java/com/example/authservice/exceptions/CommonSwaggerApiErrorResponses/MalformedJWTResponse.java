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
          responseCode = "500",
          content =
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = GlobalErrorResponse.class),
                  examples =
                      @ExampleObject(
                          name = "MalformedJwtException",
                          description =
                              "This error response is returned when the provided Jwt isn't valid"
                                  + " (Format-wise)",
                          value =
                              """
{
  "type": "MalformedJwtException",
  "message": "Invalid compact JWT string: Compact JWSs must contain exactly 2 period characters, and compact JWEs must contain exactly 4.  Found: 1",
  "status": "INTERNAL_SERVER_ERROR"
}
""")))
    })
public @interface MalformedJWTResponse {}
