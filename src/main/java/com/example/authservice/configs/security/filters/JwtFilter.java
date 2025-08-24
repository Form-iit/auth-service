package com.example.authservice.configs.security.filters;

import com.example.authservice.configs.security.services.CustomUserDetailsService;
import com.example.authservice.configs.security.services.JwtService;
import com.example.authservice.exceptions.FailedEmailVerification;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

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

  private final List<RequestMatcher> permittedMatchers;

  public JwtFilter(
          JwtService jwtService,
          CustomUserDetailsService userDetailsService,
          @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
          @Qualifier("permittedEndpoints") List<String> permittedEndpoints) {
    super();
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
    this.resolver = resolver;
    this.permittedMatchers =
            permittedEndpoints.stream()
                    .map(endpoint -> new AntPathRequestMatcher(endpoint, null)) // since it works only with get reqs, null makes it support all
                    .collect(Collectors.toList());
    log.info("===================================================================================================================>");
    log.info("permittedMatchers List: {}", this.permittedMatchers);
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    String requestPath = request.getRequestURI();
    String method = request.getMethod();

    log.trace("Checking whether the request doesn't require auth");
    log.trace("Request details - Method: {}, URI: {}", method, requestPath);

    // Debug: Print all permitted endpoints
    log.trace("Permitted endpoints: {}",
            permittedMatchers.stream()
                    .map(matcher -> ((AntPathRequestMatcher) matcher).getPattern())
                    .collect(Collectors.toList()));

    boolean isPermitted = permittedMatchers.stream().anyMatch(matcher -> {
      boolean matches = matcher.matches(request);
      log.trace("Checking pattern '{}' against '{}' {}: {}",
              ((AntPathRequestMatcher) matcher).getPattern(),
              requestPath,
              method,
              matches);
      return matches;
    });

    log.trace("Final decision - isPermitted: {}", isPermitted);

    if (isPermitted) {
      log.trace("Status confirmed: request doesn't require auth... Continuing without token verification");
    } else {
      log.trace("Request requires auth for: {} {}", method, requestPath);
    }

    return isPermitted;
  }

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain) { // Added missing throws clause

    String requestURL = request.getRequestURL().toString();
    String requestURI = request.getRequestURI();
    String method = request.getMethod();

    log.trace("Processing request - Method: {}, URI: {}, URL: {}", method, requestURI, requestURL);
    log.info("Request reached the JWT filter for: {} {}", method, requestURI);

    try {
      log.trace("Request requires auth... ");
      log.trace("Extracting jwt from request");

      Optional<String> jwt = extractJwtFromRequest(request);
      if (jwt.isPresent()) {
        log.trace("JWT found, checking token...");
        authenticateUser(jwt.get(), request);
      } else {
        log.warn("JWT not found for protected endpoint: {} {}", method, requestURI);
        handleFailedVerification(request, response, filterChain);
        return; // Early exit after handling
      }

      log.trace("Proceeding with filter chain");
      filterChain.doFilter(request, response);

    } catch (Exception ex) {
      log.error("Exception in JWT filter for {} {}: {}", method, requestURI, ex.getMessage(), ex);
      resolver.resolveException(request, response, null, ex);
    }
  }

  private Optional<String> extractJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getParameter("token");
    log.debug(bearerToken);
    if (bearerToken == null || bearerToken.isEmpty()) {
      String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
      if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
        bearerToken = authorizationHeader.substring(BEARER_PREFIX.length());
        log.info("JWT found in Authorization header");
      } else {
        log.error("Jwt not found!");
        return Optional.empty();
      }
    } else {
      log.info("JWT found in request parameter");
    }
    return Optional.of(bearerToken);
  }

  private void authenticateUser(String token, HttpServletRequest request) {
    try {
      String userId = jwtService.extractId(token);
      log.trace("Extracted user ID from token: {}", userId);

      if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        log.trace("User not authenticated, loading user details");

        UserDetails userDetails = userDetailsService.loadUserById(userId);
        log.trace("User details loaded by ID: {}", userDetails.getUsername());

        log.trace("Validating token...");
        if (jwtService.tokenIsValid(token, userDetails)) {
          log.trace("Token is valid, creating authentication token");

          UsernamePasswordAuthenticationToken authToken =
                  new UsernamePasswordAuthenticationToken(
                          userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          log.trace("Updating security context");
          SecurityContextHolder.getContext().setAuthentication(authToken);
          log.trace("Authentication successful for user: {}", userDetails.getUsername());
        } else {
          log.error("Invalid token for user: {}", userId);
        }
      } else {
        log.trace("User already authenticated or userId is null");
      }
    } catch (Exception ex) {
      log.error("Error during user authentication: {}", ex.getMessage(), ex);
      throw ex; // Re-throw to be handled by the outer try-catch
    }
  }

  private void handleFailedVerification(
          HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
    if (request.getRequestURI().contains("verify")) {
      // Handle the failed verification case
      FailedEmailVerification emailVerificationException =
              new FailedEmailVerification("Failed to verify email. Token could not be verified.");
      log.error("Email verification failed: " + emailVerificationException.getMessage());
    }
    filterChain.doFilter(request, response);
  }
}
