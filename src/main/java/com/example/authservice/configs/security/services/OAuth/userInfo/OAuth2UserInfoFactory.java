package com.example.authservice.configs.security.services.OAuth.userInfo;

import com.example.authservice.configs.security.services.OAuth.userInfo.instances.GithubOAuth2UserInfo;
import com.example.authservice.configs.security.services.OAuth.userInfo.instances.GoogleOAuth2UserInfo;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "github" -> new GithubOAuth2UserInfo(attributes);
            default -> throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        };
    }
}

