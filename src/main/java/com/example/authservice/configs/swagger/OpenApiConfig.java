package com.example.authservice.configs.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;


@OpenAPIDefinition (
        info = @Info(
                title = "Authentication Service - Open Api Specification",
                version = "1.0",
                contact = @Contact(
                        name = "Bachar ELKARNI",
                        email = "elkarni.bachar@gmail.com"
                )
        ),
        security = {
                @SecurityRequirement(name="Bearer Auth")
        }
)
@SecurityScheme(
        name = "Bearer Auth",
        scheme= "bearer",
        type= SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
