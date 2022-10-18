package com.example.assignmentapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Collections;
import java.util.stream.Collectors;

@RestController
public class AuthController {
    @Autowired
    JwtEncoder encoder;

    // End point for authenticating a user and returning a JWT
    // User is just the details set as an in-memory user in the WebSecurityConfig
    // This end point is configured to use HTTP Basic Auth,
    // which is an Authorization header with value "Basic {base64 encoded user:password}
    @CrossOrigin(origins = "http://127.0.0.1:5173") // allow cross-origin calls from this address (default Vite react app)
    @PostMapping(path = "/login")
    public ResponseEntity<Object> getToken(Authentication authentication) {
        if (authentication != null) {
            Instant now = Instant.now();
            long expiry = 600L; // How long until the JWT will expire
                                // Shorter is better, as JWTs can not be revoked without putting them on a blacklist

            // Matches the authentication credentials to a user and gets their authorities (in this case, role "user")
            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            // The claims form the center part of the JWT, this builder uses standard Registered ones
            // https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-token-claims
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expiry))
                    .subject(authentication.getName())
                    .claim("scope", scope) // the user's authorities, from above
                    .build();

            // Return a response with the JWT as a string, the client then needs to implement this in
            // an Authorization header with value "Bearer {JWT}"
            String token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        }
        return ResponseEntity.badRequest().body("User Credentials not received");
    }
}
