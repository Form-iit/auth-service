package com.example.authservice.configs.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class CustomEntryPoint implements AuthenticationEntryPoint {

  @Qualifier("handlerExceptionResolver")
  private final HandlerExceptionResolver resolver;

  private static final Logger logger = LoggerFactory.getLogger(CustomEntryPoint.class);

  public CustomEntryPoint(
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    super();
    this.resolver = resolver;
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    logger.info("request in the Custom Entry Point");
    resolver.resolveException(request, response, null, authException);
  }
}
