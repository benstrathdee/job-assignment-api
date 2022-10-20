package com.example.assignmentapi.controllers;

import com.example.assignmentapi.dto.user.UserLoginData;
import com.example.assignmentapi.dto.user.UserRegistrationData;
import com.example.assignmentapi.dto.user.UserReturnDTO;
import com.example.assignmentapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    // POST /auth/login
        // Log in user and return JWT in cookie
    @PostMapping(path = "/login")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody UserLoginData data) {
        Authentication authentication = authService.authenticateUser(data);
        ResponseCookie jwtCookie = authService.getAuthCookie(authentication);
        UserReturnDTO user = authService.getUser(authentication);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(user);
    }

    // POST /auth/logout
        // Sign out the currently logged-in user by wiping their JWT
    @PostMapping(path = "/logout")
    public ResponseEntity<Object> logoutUser() {
        ResponseCookie cookie = authService.logoutUser();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Successfully signed out!");
    }

    // POST /auth/register
        // Register new user
    @PostMapping(path = "/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserRegistrationData data) {
        UserReturnDTO user = authService.registerUser(data);
        return ResponseEntity.ok(user);
    }
}
