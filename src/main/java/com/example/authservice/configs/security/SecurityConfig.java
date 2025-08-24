package com.example.authservice.configs.security;

import static com.example.authservice.enums.Role.ADMIN;

import com.example.authservice.configs.security.filters.JwtFilter;
import com.example.authservice.configs.security.services.JwtService;
import com.example.authservice.configs.security.services.OAuth.CustomOAuth2UserService;
import com.example.authservice.configs.security.services.CustomUserDetailsService;

import java.util.List;

import com.example.authservice.configs.security.services.OAuth.CustomOidcUserService;
import com.example.authservice.configs.security.services.OAuth.OAuth2AuthenticationFailureHandler;
import com.example.authservice.configs.security.services.OAuth.OAuth2AuthenticationSuccessHandler;
import com.example.authservice.repositories.AuthRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomEntryPoint authEntryPoint;
    private final AuthRepo authRepo;
    private final JwtFilter jwtFilter;
    private final JwtService jwtService;
    private final RoleAccessDeniedHandler roleAccessDeniedHandler;
    private final List<String> openEndpoints;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            CustomEntryPoint authEntryPoint,
            JwtFilter jwtFilter,
            JwtService jwtService,
            AuthRepo authRepo,
            RoleAccessDeniedHandler roleAccessDeniedHandler,
            @Qualifier("permittedEndpoints") List<String> permittedEndpoints) {
        this.customUserDetailsService = customUserDetailsService;
        this.authEntryPoint = authEntryPoint;
        this.authRepo = authRepo;
        this.jwtFilter = jwtFilter;
        this.jwtService = jwtService;
        this.roleAccessDeniedHandler = roleAccessDeniedHandler;
        this.openEndpoints = permittedEndpoints;
    }

    @Bean
    @Order(2) // Higher priority
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/actuator/**")
                .httpBasic(Customizer.withDefaults()) // Use HTTP Basic for protected actuator endpoints
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/actuator/health", "/actuator/info")
                                .permitAll() // Allow health checks without auth
                                .requestMatchers("/actuator/**")
                                .hasRole(ADMIN.name()) // Require ADMIN role for other actuator endpoints
                )
                .authenticationProvider(customAuthProvider())
                // NO JWT filter here - use HTTP Basic for protected endpoints
                .build();
    }

    @Bean
    @Order(3) // Higher priority - Swagger endpoints
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/v1/swagger-ui/**", "/api/v1/api-docs/**")
                .httpBasic(Customizer.withDefaults()) // Browser popup
                .authenticationProvider(customAuthProvider()) // Uses your DB via CustomUserDetailsService
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req ->
                        req.anyRequest().hasRole(ADMIN.name())
                )
                .build();
    }

    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService(authRepo, passwordEncoder());
    }


    @Bean
    public CustomOidcUserService customOidcUserService() {
        return new CustomOidcUserService(authRepo);
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(jwtService, authRepo);
    }

    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain OAuth2SecurityFilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher("/oauth2/**", "/login/oauth2/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(req -> req.anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService())     // For OAuth2 providers
                                .oidcUserService(customOidcUserService())   // For OIDC providers like Google
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler())
                        .failureHandler(oAuth2AuthenticationFailureHandler())
                )
                .build();
    }

    @Bean
    @Order(4) // Lower priority - API endpoints
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/**") // Apply to remaining API endpoints
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(openEndpoints.toArray(new String[0]))
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .authenticationProvider(customAuthProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(customizer -> {
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

    @Bean
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
