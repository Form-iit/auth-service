package com.example.authservice.configs.security.filters;

import com.example.authservice.configs.security.services.CustomUserDetailsService;
import com.example.authservice.configs.security.services.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

@Configuration
public class JwtFilterConfig {
    @Bean
    public JwtFilter jwtFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
            @Qualifier("permittedEndpoints") List<String> permittedEndpoints) {
        return new JwtFilter(jwtService, userDetailsService, resolver, permittedEndpoints);
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter jwtFilter) {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>(jwtFilter);
        registration.setEnabled(false); // Prevent automatic servlet registration
        return registration;
    }
}