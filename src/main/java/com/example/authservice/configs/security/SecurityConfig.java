package com.example.authservice.configs.security;

import static com.example.authservice.enums.Role.ADMIN;

import com.example.authservice.configs.security.filters.JwtFilter;
import com.example.authservice.configs.security.services.CustomUserDetailsService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
  private final CustomUserDetailsService customUserDetailsService;
  private final CustomEntryPoint authEntryPoint;
  private final JwtFilter jwtFilter;
  private final RoleAccessDeniedHandler roleAccessDeniedHandler;
  private final List<String> openEndpoints;

  public SecurityConfig(
      CustomUserDetailsService customUserDetailsService,
      CustomEntryPoint authEntryPoint,
      JwtFilter jwtFilter,
      RoleAccessDeniedHandler roleAccessDeniedHandler,
      List<String> permittedEndpoints) {
    this.customUserDetailsService = customUserDetailsService;
    this.authEntryPoint = authEntryPoint;
    this.jwtFilter = jwtFilter;
    this.roleAccessDeniedHandler = roleAccessDeniedHandler;
    this.openEndpoints = permittedEndpoints;
  }

  @Bean
  public SecurityFilterChain filterChainConfiguration(HttpSecurity http) throws Exception {
    return http.securityMatcher("/api/**") // Apply to API endpoints only
        .httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .formLogin(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            req ->
                req.requestMatchers(openEndpoints.toArray(new String[0]))
                    .permitAll() // Allow access to log in and register
                    .requestMatchers(new RegexRequestMatcher(".*/actuator/.*", null))
                    .hasRole(ADMIN.name())
                    .anyRequest()
                    .authenticated() // Authenticate any other request
            )
        .authenticationProvider(customAuthProvider())
        .addFilterBefore(
            jwtFilter,
            UsernamePasswordAuthenticationFilter
                .class) // * This method places the JwtFilter before the
        // UsernamePasswordAuthenticationFilter in the filter chain. Normally,
        // authentication relies on the UsernamePasswordAuthenticationFilter, which
        // verifies a user's credentials (username and password) before allowing the
        // request to proceed further. However, since we're using JWT-based
        // authentication, we want to validate the JWT token instead of using a
        // username/password. By placing the JwtFilter before the
        // UsernamePasswordAuthenticationFilter, we ensure that the JWT token is
        // checked first, and, if valid, it will set the authentication context.
        // This allows the request to bypass the
        // UsernamePasswordAuthenticationFilter for further validation, as the
        // JwtFilter will have already authenticated the user.
        .exceptionHandling(
            customizer -> {
              customizer.authenticationEntryPoint(authEntryPoint);
              customizer.accessDeniedHandler(roleAccessDeniedHandler);
            })
        .build();
  }

  @Bean
  public AuthenticationManager customAuthManager(AuthenticationConfiguration authConfig)
      throws Exception {
    logger.debug("Request reached the Custom Auth Manager");
    return authConfig
        .getAuthenticationManager(); // * returns an AuthenticationManager that is configured with
    // all available AuthenticationProvider beans which includes
    // our customAuthProvider.
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  public AuthenticationProvider customAuthProvider() {
    logger.debug("Request reached the Custom Auth Provider");
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    logger.trace("initiated the DaoAuthenticationProvider");
    provider.setPasswordEncoder(passwordEncoder());
    logger.trace("Password encoder linked to the DaoAuthenticationProvider");
    provider.setUserDetailsService(customUserDetailsService);
    logger.trace("Custom UserDetailsService linked to the DaoAuthenticationProvider");
    return provider;
  }
}
