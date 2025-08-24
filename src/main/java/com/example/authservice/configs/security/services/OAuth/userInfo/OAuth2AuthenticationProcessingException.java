package com.example.authservice.configs.security.services.OAuth.userInfo;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class OAuth2AuthenticationProcessingException extends OAuth2AuthenticationException {
    public OAuth2AuthenticationProcessingException(String msg) {
        super(msg);
    }
}
