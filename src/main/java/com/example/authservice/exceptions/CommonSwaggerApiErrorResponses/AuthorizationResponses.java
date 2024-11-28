package com.example.authservice.exceptions.CommonSwaggerApiErrorResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizationResponses {
    ExampleType[] example() default ExampleType.AUTHORIZATION_DENIED;
    boolean includeAll() default false;

    enum ExampleType {
        AUTHORIZATION_DENIED,
        INSUFFICIENT_AUTHENTICATION,
        EXPIRED_JWT // Extend as needed
    }
}