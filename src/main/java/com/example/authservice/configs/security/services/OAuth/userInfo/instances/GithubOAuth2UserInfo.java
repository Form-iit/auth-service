package com.example.authservice.configs.security.services.OAuth.userInfo.instances;

import com.example.authservice.configs.security.services.OAuth.userInfo.OAuth2UserInfo;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@SuperBuilder
public class GithubOAuth2UserInfo extends OAuth2UserInfo {

    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getFirstName() {
        String name =  (String) attributes.get("name");
        return name != null ? name.split(" ")[0] : null;
    }

    @Override
    public String getLastName() {
        String name =  (String) attributes.get("name");
        if (name != null) {
            String[] parts = name.split(" ");
            return parts.length > 1 ? parts[parts.length - 1] : null;
        }
        return null;
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}
