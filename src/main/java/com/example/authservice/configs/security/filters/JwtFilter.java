package com.example.authservice.configs.security.filters;

import com.example.authservice.configs.security.services.CustomUserDetailsService;
import com.example.authservice.configs.security.services.JwtService;
import com.example.authservice.exceptions.FailedEmailVerification;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private final JwtService jwtService;
  private final CustomUserDetailsService
      userDetailsService; // * already got imps of it, but we need our own imp cuz we need to fetch

  // users from our DB

  @Qualifier("handlerExceptionResolver")
  private final HandlerExceptionResolver resolver;

  private final List<String> openApiEndpoints =
      Arrays.asList("/api/v1/auth/login", "/api/v1/auth/register", "/error", "/images/.*");

  public JwtFilter(
      JwtService jwtService,
      CustomUserDetailsService userDetailsService,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    super();
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
    this.resolver = resolver;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    logger.info("Request reached the JWT filter");
    try {
      // Allow requests to log in and register without a token
      logger.trace("Checking whether the request doesn't require auth");
      if (isOpenEndpoint(request)) {
        logger.trace(
            "Status confirmed: request doesn't require auth... Continuing without token"
                + " verification");
        filterChain.doFilter(request, response); // Continue the filter chain without token validation
      }
      logger.trace("Request requires auth... ");
      logger.trace("extracting jwt from request");
      Optional<String> jwt = extractJwtFromRequest(request);
      if (jwt.isPresent()) {
        logger.trace("Checking token...");
        authenticateUser(jwt.get(), request);
      } else {
        handleFailedVerification(request, response);
        return; // Early exit after handling
      }
      filterChain.doFilter(request, response);
    } catch (Exception ex) {
      resolver.resolveException(request, response, null, ex);
    }
  }

  private Boolean isOpenEndpoint(HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    for (String endpoint : openApiEndpoints) {
      if (Pattern.compile(endpoint).matcher(requestURI).find()) {
        return true;
      }
    }
    return false;
  }

  private Optional<String> extractJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getParameter("token");
    logger.debug(bearerToken);
    if (bearerToken == null || bearerToken.isEmpty()) {
      String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
      if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
        bearerToken = authorizationHeader.substring(BEARER_PREFIX.length());
        logger.info("JWT found in Authorization header");
      } else {
        logger.error("Jwt not found!");
        return Optional.empty();
      }
    } else {
      logger.info("JWT found in request parameter");
    }
    return Optional.of(bearerToken);
  }

  private void authenticateUser(String token, HttpServletRequest request) {
    String userId = jwtService.extractId(token);
    logger.trace("user ID: {}", userId);
    if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      logger.trace("User non authenticated!");
      UserDetails userDetails = userDetailsService.loadUserById(userId);
      logger.trace("user details loaded by ID");
      logger.trace("Checking token's validity...");
      if (jwtService.tokenIsValid(token, userDetails)) {
        logger.trace("token is valid");
        logger.trace("Making auth token for user...");
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        logger.trace("Done");
        logger.trace("Updating security context...");
        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.trace("Done");
      } else logger.error("Invalid token");
    }
  }

  private void handleFailedVerification(HttpServletRequest request, HttpServletResponse response) {
    if (request.getRequestURI().contains("verify")) {
      // Handle the failed verification case
      FailedEmailVerification emailVerificationException =
          new FailedEmailVerification("Failed to verify email");
      logger.error("Email verification failed: {}", emailVerificationException.getMessage());
      resolver.resolveException(request, response, null, emailVerificationException);
    }
  }
}
