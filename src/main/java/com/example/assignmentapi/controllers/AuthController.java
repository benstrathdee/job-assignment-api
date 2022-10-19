package com.example.assignmentapi.controllers;

import com.example.assignmentapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class AuthController {
    @Autowired
    AuthService authService;

    // End point for authenticating a user and returning a JWT
    // User is just the details set as an in-memory user in the WebSecurityConfig
    // This end point is configured to use HTTP Basic Auth,
    // which is an Authorization header with value "Basic {base64 encoded user:password}
    @CrossOrigin(origins = "http://127.0.0.1:5173") // allow cross-origin calls from this address (default Vite react app)
    @PostMapping(path = "/login")
    public ResponseEntity<Object> getToken(Authentication authentication) {
        // Return a response with the JWT as a string, the client then needs to implement this in
        // an Authorization header with value "Bearer {JWT}"
        String token = authService.getToken(authentication);
        if (token != null) {
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        }
        return ResponseEntity.badRequest().build();
    }
}
