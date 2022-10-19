package com.example.assignmentapi.controllers;

import com.example.assignmentapi.dto.user.UserLoginData;
import com.example.assignmentapi.dto.user.UserRegistrationData;
import com.example.assignmentapi.entity.User;
import com.example.assignmentapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
        ResponseCookie jwtCookie = authService.getAuthCookie(data);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body("Yay!"); // TODO
    }

    // POST /auth/register
        // Register new user
    @PostMapping(path = "/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserRegistrationData data) {
        User user = authService.registerUser(data);
        if (user == null) {
            return ResponseEntity.badRequest().body("A user already exists with that username/email");
        }
        return ResponseEntity.ok("registered");
    }
}
