package com.example.authservice.configs.swagger;

import com.example.authservice.exceptions.CommonSwaggerApiErrorResponses.AuthorizationResponses;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthorizationResponseOperationCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        // Retrieve the annotation from the handler method
        AuthorizationResponses annotation =
                handlerMethod.getMethodAnnotation(AuthorizationResponses.class);
        if (annotation != null) {
            // Define the shared schema reference for the response
            Schema<?> schema = new Schema<>().$ref("#/components/schemas/GlobalErrorResponse");

            // Define all possible examples
            EnumMap<AuthorizationResponses.ExampleType, Example> exampleMap = new EnumMap<>(AuthorizationResponses.ExampleType.class);
            exampleMap.put(AuthorizationResponses.ExampleType.AUTHORIZATION_DENIED, createExample(
                    "AuthorizationDeniedException",
                    "This is the error returned when the user is authenticated but doesn't have the required permissions or roles to access the resources",
                    """
                    {
                      "type": "AuthorizationDeniedException",
                      "message": "Access Denied",
                      "status": "UNAUTHORIZED"
                    }
                    """
            ));
            exampleMap.put(AuthorizationResponses.ExampleType.INSUFFICIENT_AUTHENTICATION, createExample(
                    "InsufficientAuthenticationException",
                    "This is the error returned when trying to access a protected resource while being un-authenticated",
                    """
                    {
                      "type": "InsufficientAuthenticationException",
                      "message": "Full authentication is required to access this resource",
                      "status": "UNAUTHORIZED"
                    }
                    """
            ));
            exampleMap.put(AuthorizationResponses.ExampleType.EXPIRED_JWT, createExample(
                    "ExpiredJwtException",
                    "This is the error returned when a JWT token has expired",
                    """
                    {
                      "type": "ExpiredJwtException",
                      "message": "JWT token is expired",
                      "status": "UNAUTHORIZED"
                    }
                    """
            ));

            // Determine which examples to include
            Map<String, Example> selectedExamples = new HashMap<>();
            if (annotation.includeAll()) {
                // Include all examples
                exampleMap.forEach((key, value) -> selectedExamples.put(value.getSummary(), value));
            } else {
                // Include only the specified examples
                for (AuthorizationResponses.ExampleType exampleType : annotation.example()) {
                    Example example = exampleMap.get(exampleType);
                    if (example != null) {
                        selectedExamples.put(example.getSummary(), example);
                    }
                }
            }

            // Create content with the selected examples
            MediaType mediaType = new MediaType();
            mediaType.schema(schema);
            selectedExamples.forEach(mediaType::addExamples);

            Content content = new Content();
            content.addMediaType("application/json", mediaType);

            // Define the 401 API response
            ApiResponse apiResponse = new ApiResponse()
                    .description("Unauthorized or access denied")
                    .content(content);

            // Add the response to the operation
            operation.getResponses().addApiResponse("401", apiResponse);
        }

        return operation;
    }


    private Example createExample(String summary, String description, String value) {
        Example example = new Example();
        example.setSummary(summary);
        example.setDescription(description);
        example.setValue(value);
        return example;
    }
}
