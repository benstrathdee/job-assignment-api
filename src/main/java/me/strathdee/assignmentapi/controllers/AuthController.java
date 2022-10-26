package com.example.assignmentapi.controllers;

import com.example.assignmentapi.dto.user.UserLoginData;
import com.example.assignmentapi.dto.user.UserRegistrationData;
import com.example.assignmentapi.dto.user.UserReturnDTO;
import com.example.assignmentapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    // POST /auth/login
        // Log in user and return JWT in cookie
    @PostMapping(path = "/login")
    public ResponseEntity<Object> loginUser(@Valid @RequestBody UserLoginData data) {
        HttpHeaders headers = authService.loginUser(data);

        UserReturnDTO user = authService.getUserFromContext();

        return ResponseEntity.ok().headers(headers).body(user);
    }

    // POST /auth/logout
        // Sign out the currently logged-in user by wiping their JWT
    @PostMapping(path = "/logout")
    public ResponseEntity<Object> logoutUser(@Valid @CookieValue(name = "${auth.token.refresh}") String token) {
        HttpHeaders headers =  authService.logoutUser(token);

        return ResponseEntity.ok().headers(headers)
                .body("Successfully signed out!");
    }

    // POST /auth/refresh
        // Attempt to refresh the user's JWTs
    @PostMapping(path = "/refresh")
    public ResponseEntity<Object> refreshTokens(@Valid @CookieValue(name = "${auth.token.refresh}") String token) {
        HttpHeaders headers = authService.refreshTokens(token);

        return ResponseEntity.ok().headers(headers)
                .body("Successfully refreshed tokens!");
    }

    // POST /auth/register
        // Register new user
    @PostMapping(path = "/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserRegistrationData data) {
        UserReturnDTO user = authService.registerUser(data);
        return ResponseEntity.ok(user);
    }
}
