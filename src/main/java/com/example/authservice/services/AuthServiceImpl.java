package com.example.authservice.services;

import com.example.authservice.configs.security.services.JwtService;
import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.enums.Role;
import com.example.authservice.exceptions.FailedEmailVerification;
import com.example.authservice.exceptions.UserAlreadyExistsException;
import com.example.authservice.mailTemplates.templates.mailTemplates;
import com.example.authservice.models.User;
import com.example.authservice.repositories.AuthRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder passEncoder;
    private final AuthRepo authRepo;
    private final AuthenticationManager authManager;
    private final MailsProducer mailer;

    //TODO: Change into domain name config (after you make the api gateway)
    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.protocol}")
    private String serverProtocol;

    @Autowired
    public AuthServiceImpl(AuthRepo authRepo, JwtService jwtService, AuthenticationManager authManager, PasswordEncoder passEncoder, MailsProducer mailer) {
        this.authRepo = authRepo;
        this.jwtService = jwtService;
        this.passEncoder = passEncoder;
        this.authManager = authManager;
        this.mailer = mailer;
    }

    @Override
    public void register(RegisterRequest request) {
        //? check for user presence
        if (authRepo.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("There's already a user with the given email address");
        }
        User user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        try {
            User savedUser = authRepo.save(user);
            //? Create email verification token
            String token = jwtService.generateToken(savedUser);
            //? Create base public URL
            String publicBaseUrl = (serverProtocol + "://" + serviceName + ":" + serverPort).toLowerCase();
            //? Creating email Html body
            String htmlTemplate = mailTemplates.getRegistrationEmailTemplate()
                    .replace("{{userFirstName}}", request.getFirstname())
                    .replace("{{verifyEmailAPI}}", publicBaseUrl + "/api/v1/auth/verify?token=" + token)
                    .replace("{{base_url}}", publicBaseUrl);
            //? Sending email verification token
            mailer.sendEmail(request.getEmail(), "Verify your email address", htmlTemplate);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException("There's already a user with the given email address");
        }
    }

    @Override
    public void verify() {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Extract the user ID
        String userId = userDetails.getUsername();

        // Find the user
        User user = authRepo.findById(userId).orElseThrow(() -> new FailedEmailVerification("Failed to verify user"));

        // Enable user's account
        user.setEnabled(true);
        authRepo.save(user);
    }

    @Override
    public String login(AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        ); //* if this doesn't throw an error, the user is authenticated
        User user = authRepo.findByEmail(request.getEmail()).orElseThrow();
        return jwtService.generateToken(user);
    }
}