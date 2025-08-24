package com.example.authservice.configs.security.services.OAuth;
import com.example.authservice.configs.security.services.OAuth.userInfo.OAuth2UserInfo;
import com.example.authservice.configs.security.services.OAuth.userInfo.OAuth2UserInfoFactory;
import com.example.authservice.enums.AuthProvider;
import com.example.authservice.enums.Role;
import com.example.authservice.models.User;
import com.example.authservice.repositories.AuthRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final AuthRepo authRepo;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(AuthRepo authRepo, PasswordEncoder passwordEncoder) {
        this.authRepo = authRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.trace("User '{}' has been loaded", oAuth2User);
        try {
            log.trace("processing user...");
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user: {}", ex.getMessage(), ex);
            throw new OAuth2AuthenticationException("oauth2_processing_error");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.trace("registrationId: {}", registrationId);
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        log.trace("UserInfo: {}", oAuth2UserInfo);
        log.trace("user email: {}", oAuth2UserInfo.getEmail());
        if (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isEmpty()) {
                throw new OAuth2AuthenticationException("email_not_found_from_oauth2_provider");
        }

        Optional<User> userOptional = authRepo.findByEmail(oAuth2UserInfo.getEmail());
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            user = updateExistingUser(user, oAuth2UserInfo, registrationId);
        } else {
            user = createNewUser(oAuth2UserInfo, registrationId);
        }
        log.trace("Defining the user's type...");
        if(oAuth2User instanceof OidcUser oidcUser){
            log.trace("User is of type OidcUser!!");
            log.trace("Processing OIDC user of provider {}", registrationId);
            return new CustomOAuth2User(user, oidcUser.getAttributes(), oidcUser.getIdToken(), oidcUser.getUserInfo());
        }else{
            log.trace("User is of type OAuth2User!!");
            log.trace("Processing OAuth2 user of provider {}", registrationId);
            return new CustomOAuth2User(user, oAuth2User.getAttributes());
        }
    }

    private User createNewUser(OAuth2UserInfo oAuth2UserInfo, String registrationId) {
        User user = User.builder()
                .firstName(oAuth2UserInfo.getFirstName())
                .lastName(oAuth2UserInfo.getLastName())
                .email(oAuth2UserInfo.getEmail())
                .providerId(oAuth2UserInfo.getId())
                .authProvider(AuthProvider.valueOf(registrationId.toUpperCase()))
                .profileImageUrl(oAuth2UserInfo.getImageUrl())
                .role(Role.USER) // Default role - adjust to match your Role enum
                .enabled(true) // OAuth2 users are typically enabled by default
                .emailVerified(true) // OAuth2 providers typically verify emails
                .build();

        return authRepo.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo, String registrationId) {
        // Update user info from OAuth2 provider
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setProfileImageUrl(oAuth2UserInfo.getImageUrl());

        // If this is the first OAuth2 login for a local user, update provider info
        if (existingUser.getAuthProvider() == AuthProvider.LOCAL || existingUser.getAuthProvider() == null) {
            existingUser.setProviderId(oAuth2UserInfo.getId());
            existingUser.setAuthProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
            existingUser.setEmailVerified(true);
        }

        return authRepo.save(existingUser);
    }
}