package me.strathdee.assignmentapi.security.jwt;

import me.strathdee.assignmentapi.service.AuthService;
import me.strathdee.assignmentapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    // The name of the authToken cookie
    @Value("${auth.token.auth}")
    private String authCookieName;

    // The name of the refresh claim
    @Value("${auth.token.fingerprint}")
    private String fingerprintName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // This just checks if the URL contains "auth", because ideally this filter shouldn't
        // be running for any /auth/** paths, but it still seems to anyway which makes unnecessary
        // DB queries
        boolean isAuth = request.getServletPath().contains("auth");
        if (!isAuth) {
            try {
                String authToken = authService.getTokenFromCookies(request, authCookieName);
                if (authToken != null && !authToken.equals("")) {
                    String username = jwtUtils.getUserNameFromToken(authToken);

                    boolean jwtIsValid = jwtUtils.isJwtValid(authToken);

                    String refreshClaim = jwtUtils.getClaimFromToken(fingerprintName, authToken);
                    boolean jwtRefreshIsValid = userService.isUserRefreshFingerprintCorrect(username, refreshClaim);

                    if (jwtIsValid && jwtRefreshIsValid) {
                        // JWT is completely valid - load user from data in token
                        UserDetails userDetails = userService.loadUserByUsername(username);

                        // Authenticate user
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                System.err.println("Cannot set user authentication: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
