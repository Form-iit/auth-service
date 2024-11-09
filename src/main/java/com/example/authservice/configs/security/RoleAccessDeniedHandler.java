package com.example.authservice.configs.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class RoleAccessDeniedHandler implements AccessDeniedHandler {
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver resolver;
    private static final Logger logger = LoggerFactory.getLogger(RoleAccessDeniedHandler.class);


    public RoleAccessDeniedHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        super();
        this.resolver = resolver;
    }
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        logger.info("request in the Custom Access Denied Handler");
        resolver.resolveException(request, response, null, accessDeniedException);
    }
}