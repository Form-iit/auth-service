package com.example.authservice.configs.security.services.OAuth;

import com.example.authservice.configs.security.services.JwtService;
import com.example.authservice.models.User;
import com.example.authservice.repositories.AuthRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final AuthRepo authRepo;

    public OAuth2AuthenticationSuccessHandler(JwtService jwtService, AuthRepo authRepo) {
        this.jwtService = jwtService;
        this.authRepo = authRepo;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.trace("reached the onAuthenticationSuccess");
        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect.");
            return;
        }

        User user = extractUser(authentication);
        log.trace("gotten the user {}", user);

        // Generate jwt
        String jwt = jwtService.generateToken(user);
        log.trace("generated token");

        // we can either redirect to the frontend with token as a url param
        String targetUrl = determineTargetUrl(jwt);
        log.trace("determined target url {}", targetUrl);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect.");
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(String jwt) {
        // Redirect to frontend with JWT as query parameter
        // Configure this as needed
        String frontendUrl = "http://localhost:3000";
        return frontendUrl + "/auth/oauth2/redirect?token=" + jwt;
    }

    private User extractUser(Authentication authentication) {
        log.trace("trying to get the principal ...");
        Object principal = authentication.getPrincipal();
        log.trace("gotten the principal {}", principal);

        // Since CustomOAuth2UserService processes both OAuth2User and OidcUser
        // and returns CustomOAuth2User, we should always get CustomOAuth2User here
        if (principal instanceof CustomOAuth2User customOAuth2User) {
            log.trace("is instance of customOAuth2User");
            return customOAuth2User.getUser();
        } else {
            // This shouldn't happen if CustomOAuth2UserService is working properly
            log.error("Unexpected principal type: {}", principal.getClass());

            // Fallback: try to extract email and find user
            String email = extractEmailFromPrincipal(principal);
            if (email != null) {
                return authRepo.findByEmail(email)
                        .orElseThrow(() -> new IllegalStateException("User not found: " + email));
            }

            throw new IllegalArgumentException("Unknown principal type: " + principal.getClass());
        }
    }

    private String extractEmailFromPrincipal(Object principal) {
        if (principal instanceof DefaultOidcUser oidcUser) {
            return oidcUser.getEmail();
        } else if (principal instanceof OAuth2User oAuth2User) {
            return (String) oAuth2User.getAttributes().get("email");
        }
        return null;
    }
}