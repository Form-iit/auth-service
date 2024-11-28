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
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "400",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalErrorResponse.class),
                        examples = @ExampleObject(
                                name = "MethodArgumentNotValidException",
                                description = "This error response is returned whenever one or more arguments passed to a controller don't respect the imposed validation rules",
                                value = "{\n" +
                                        "  \"type\": \"MethodArgumentNotValidException\",\n" +
                                        "  \"message\": \"Validation failed for one or more fields.\",\n" +
                                        "  \"errors\": {\n" +
                                        "    \"field 1\": [\n" +
                                        "      \"Valid field 1 criteria 1 description\",\n" +
                                        "      \"Valid field 1 criteria 2 description\"\n" +
                                        "    ],\n" +
                                        "    \"field 2\": [\n" +
                                        "      \"Valid field 2 criteria 1 description\"\n" +
                                        "    ]\n" +
                                        "  },\n" +
                                        "  \"status\": \"BAD_REQUEST\"\n" +
                                        "}"
                        )
                )
        )
})
public @interface InvalidMethodArgumentResponse {
}
