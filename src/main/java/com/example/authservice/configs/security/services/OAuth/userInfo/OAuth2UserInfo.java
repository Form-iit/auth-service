package com.example.authservice.configs.security.services.OAuth.userInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@AllArgsConstructor
@SuperBuilder
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public abstract String getId();
    public abstract String getFirstName();
    public abstract String getLastName();
    public abstract String getEmail();
    public abstract String getImageUrl();
}
