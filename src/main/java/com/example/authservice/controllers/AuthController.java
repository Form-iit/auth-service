package com.example.authservice.controllers;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.services.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService service;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    public AuthController(AuthService authService) {
        this.service = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request){
        logger.info("Register controller reached");
        service.register(request);
        return new ResponseEntity<>("Please verify your email address :D", HttpStatus.CREATED);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token){
        logger.info("Verifying email address ...");
        service.verify();
        return new ResponseEntity<>("Account enabled successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request){
        logger.info("Login controller reached");
        final String token = service.login(request);
        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK);
    }
}
