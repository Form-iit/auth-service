package com.example.authservice.configs.security.services.OAuth;

import com.example.authservice.configs.security.services.OAuth.userInfo.OAuth2UserInfo;
import com.example.authservice.configs.security.services.OAuth.userInfo.OAuth2UserInfoFactory;
import com.example.authservice.enums.AuthProvider;
import com.example.authservice.enums.Role;
import com.example.authservice.models.User;
import com.example.authservice.repositories.AuthRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomOidcUserService extends OidcUserService {
    private final AuthRepo authRepo;

    public CustomOidcUserService(AuthRepo authRepo) {
        this.authRepo = authRepo;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        log.error("this is the type of the user: {}", (super.loadUser(userRequest)).getClass().getName());
        OidcUser oidcUser = super.loadUser(userRequest);
        log.trace("OIDC User '{}' has been loaded", oidcUser);

        try {
            log.trace("processing OIDC user...");
            return processOidcUser(userRequest, oidcUser);
        } catch (Exception ex) {
            log.error("Error processing OIDC user: {}", ex.getMessage(), ex);
            throw new OAuth2AuthenticationException("oidc_processing_error");
        }
    }

    private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.trace("OIDC registrationId: {}", registrationId);

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oidcUser.getAttributes());
        log.trace("OIDC UserInfo: {}", oAuth2UserInfo);

        if (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("email_not_found_from_oidc_provider");
        }

        Optional<User> userOptional = authRepo.findByEmail(oAuth2UserInfo.getEmail());
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            user = updateExistingUser(user, oAuth2UserInfo, registrationId);
        } else {
            user = createNewUser(oAuth2UserInfo, registrationId);
        }

        log.trace("Returning CustomOAuth2User for OIDC user");
        return new CustomOAuth2User(user, oidcUser.getAttributes(), oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    private User createNewUser(OAuth2UserInfo oAuth2UserInfo, String registrationId) {
        User user = User.builder()
                .firstName(oAuth2UserInfo.getFirstName())
                .lastName(oAuth2UserInfo.getLastName())
                .email(oAuth2UserInfo.getEmail())
                .providerId(oAuth2UserInfo.getId())
                .authProvider(AuthProvider.valueOf(registrationId.toUpperCase()))
                .profileImageUrl(oAuth2UserInfo.getImageUrl())
                .role(Role.USER)
                .enabled(true)
                .emailVerified(true)
                .build();

        return authRepo.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo, String registrationId) {
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setProfileImageUrl(oAuth2UserInfo.getImageUrl());

        if (existingUser.getAuthProvider() == AuthProvider.LOCAL || existingUser.getAuthProvider() == null) {
            existingUser.setProviderId(oAuth2UserInfo.getId());
            existingUser.setAuthProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
            existingUser.setEmailVerified(true);
        }

        return authRepo.save(existingUser);
    }
}
