package com.example.assignmentapi.security.jwt;

import com.example.assignmentapi.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    @Autowired
    JwtEncoder jwtEncoder;
    @Autowired
    JwtDecoder jwtDecoder;

    // How long the auth token/cookie will be valid for
    @Value("${auth.token.authExpiry}")
    private Long authExpirySeconds;
    @Value("${auth.token.refreshExpiry}")
    private Long refreshExpirySeconds;

    // The name of the refresh claim
    @Value("${auth.token.fingerprint}")
    private String fingerprintName;

    // The value of the issuer claim
    @Value("${auth.token.issuer}")
    private String issuer;

    // Generate a new auth token using the user information
    public String generateAuthTokenFromUser(UserPrincipal user) {
        Instant now = Instant.now();

        // Matches the authentication credentials to a user and gets their authorities (in this case, role "user")
        String scope = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        // The claims form the center part of the JWT, this builder uses standard Registered ones
        // https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-token-claims
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(authExpirySeconds))
                .subject(user.getUsername())
                .claim("scope", scope) // the user's authorities, from above
                .claim(fingerprintName, user.getFingerprint())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    // Generate a new refresh token using the user information
    public String generateRefreshTokenFromUser(UserPrincipal user) {
        Instant now = Instant.now();

        // This cookie has no authorization details, but the
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshExpirySeconds))
                .subject(user.getUsername())
                .claim(fingerprintName, user.getFingerprint())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    // Gets the username from the token claims
    public String getUserNameFromToken(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    // Gets the value of a specific claim from a token
    public <T> T getClaimFromToken(String claimName, String token) {
        T claim = jwtDecoder.decode(token).getClaim(claimName);
        if (claim != null) {
            return claim;
        } else {
            throw new JwtException(String.format("Claim %s did not exist on the token or was invalid", claimName));
        }
    }

    // Ensure the token is a valid token that can be read
    public boolean isJwtValid(String token) {
        try {
            Instant expiry = jwtDecoder.decode(token).getExpiresAt();
            Instant now = Instant.now();
            if (expiry == null) {
                throw new JwtException("Invalid expiry claim on JWT");
            }

            return now.isBefore(expiry);
        } catch (JwtException e) {
            System.err.println("JWT Token error: " + e.getMessage());
        }
        return false;
    }
}
