package com.example.assignmentapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {
    @Autowired
    JwtEncoder encoder;

    // Attempts to authentication a user and return a JWT
    public String getToken (Authentication authentication) {
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

            return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        }
        return null;
    }
}
