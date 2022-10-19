package com.example.assignmentapi.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    @Autowired
    JwtEncoder jwtEncoder;
    @Autowired
    JwtDecoder jwtDecoder;

    // The name of the cookie that will be sent to and read from the client
    private final String jwtCookie = "Token";

    // How long the token/cookie will be valid for
    private final Long expirySeconds = 600L;

    // Get the JWT cookie from the client request
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    // Generate a new JWT cookie using authentication details
    public ResponseCookie generateJwtCookie(Authentication authentication) {
        String jwt = generateTokenFromAuth(authentication);
        return ResponseCookie.from(jwtCookie, jwt).path("/").maxAge(expirySeconds).httpOnly(true).build();
    }

    // Generates a blank JWT cookie - for clearing on log out
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, "").path("/").build();
    }

    // Gets the username from the JWT claims
    public String getUserNameFromJwtToken(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    // Ensure the token is a valid token that can be read
    public boolean validateJwtToken(String token) {
        try {
            jwtDecoder.decode(token).getClaims();
            return true;
        } catch (JwtException e) {
            System.err.println("JWT Token error: " + e.getMessage());
        }
        return false;
    }

    // Generate a new JWT using the user information
    public String generateTokenFromAuth(Authentication authentication) {
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
                .expiresAt(now.plusSeconds(expirySeconds))
                .subject(authentication.getName())
                .claim("scope", scope) // the user's authorities, from above
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
