package com.example.assignmentapi.service;

import com.example.assignmentapi.dto.user.UserLoginData;
import com.example.assignmentapi.dto.user.UserRegistrationData;
import com.example.assignmentapi.dto.user.UserReturnDTO;
import com.example.assignmentapi.entity.User;
import com.example.assignmentapi.exceptionhandling.BadRefreshException;
import com.example.assignmentapi.exceptionhandling.UserDataExistsException;
import com.example.assignmentapi.repository.TempRepository;
import com.example.assignmentapi.repository.UserRepository;
import com.example.assignmentapi.security.jwt.JwtUtils;
import com.example.assignmentapi.security.UserPrincipal;
import com.example.assignmentapi.utilities.DTODirector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserService userService;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TempRepository tempRepository;

    // The names of the cookies that will be sent to and read from the client
    @Value("${auth.token.auth}")
    private String authCookieName;
    @Value("${auth.token.refresh}")
    private String refreshCookieName;

    // How long the auth token/cookie will be valid for
    @Value("${auth.token.authExpiry}")
    private Long authExpirySeconds;
    @Value("${auth.token.refreshExpiry}")
    private Long refreshExpirySeconds;

    // The name of the refresh claim
    @Value("${auth.token.fingerprint}")
    private String fingerprintName;

    // Authenticate the user
    public UserPrincipal authenticateUser (UserLoginData data) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return (UserPrincipal) authentication.getPrincipal();
    }

    // Get a representation of the user from authentication data
    public UserReturnDTO getUserFromContext() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return DTODirector.build(user);
    }

    // Authenticate the user and return JWT cookies
    public HttpHeaders loginUser (UserLoginData data) {
        UserPrincipal user = authenticateUser(data);

        ResponseCookie authCookie = generateAuthCookie(user);
        ResponseCookie refreshCookie = generateRefreshCookie(user);

        // JWTs are returned as SET_COOKIEs
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, authCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return headers;
    }

    // Using the refresh token from client, generate new JWTs
    public HttpHeaders refreshTokens (String token) {
        // Get the user information from the refresh token
        String username;
        UserPrincipal user;
        try {
            username = jwtUtils.getUserNameFromToken(token);
            System.out.println(username);
            user = (UserPrincipal) userService.loadUserByUsername(username);
        } catch (BadJwtException e) {
            throw new BadRefreshException();
        }

        try {
            // Make sure the fingerprint on the refresh token matches the current one on
            // the user entity
            String fingerprint = jwtUtils.getClaimFromToken(fingerprintName, token);
            if (!user.getFingerprint().equals(fingerprint)) {
                // The fingerprint doesn't match - don't refresh
                throw new BadRefreshException();
            }
            user = userService.updateUserFingerprint(username);

            // Generate new JWT cookies, send to client
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, generateAuthCookie(user).toString());
            headers.add(HttpHeaders.SET_COOKIE, generateRefreshCookie(user).toString());

            return headers;
        } catch (JwtException e) {
            System.err.printf("An invalid refresh attempt was made for %s - %s %n", user.getUsername(), e.getMessage());
            userService.updateUserFingerprint(username);

            throw new BadRefreshException();
        }
    }

    // Log out a user by clearing their JWT cookies
    public HttpHeaders logoutUser (String token) {
        // Get the user from token, update the user fingerprint to invalidate all old JWTs
        String username = jwtUtils.getUserNameFromToken(token);
        userService.updateUserFingerprint(username);

        // Get some empty versions of the Auth/Refresh cookies and return them to client
        // to overwrite existing ones
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, getCleanCookie(authCookieName).toString());
        headers.add(HttpHeaders.SET_COOKIE, getCleanCookie(refreshCookieName).toString());

        return headers;
    }

    // Register a new user in the DB
    public UserReturnDTO registerUser (UserRegistrationData data) {
        // Check if username/email already exists in DB
        Boolean usernameTaken = userRepository.existsByUsername(data.getUsername());
        Boolean emailTaken = userRepository.existsByEmail(data.getEmail());
        if (usernameTaken || emailTaken) {
            throw new UserDataExistsException();
        }

        // Create a new user from provided data
        User.UserBuilder builder = User.builder()
                .username(data.getUsername())
                .email(data.getEmail())
                .password(encoder.encode(data.getPassword()))
                .role("user") // TODO - handle this better
                .temp(null /*TODO - make a way to link temp to user*/);

        // Save user to DB and return representation to client (without password)
        User user = builder.build();
        userRepository.save(user);
        return DTODirector.build(user);
    }

    // Generate a new JWT Auth cookie using authentication details
    public ResponseCookie generateAuthCookie(UserPrincipal user) {
        String jwt = jwtUtils.generateAuthTokenFromUser(user);
        return ResponseCookie.from(authCookieName, jwt)
                .path("/")
                .maxAge(authExpirySeconds)
                .httpOnly(true)
                .build();
    }

    // Generate a new JWT Refresh cookie using authentication details
    public ResponseCookie generateRefreshCookie(UserPrincipal user) {
        String jwt = jwtUtils.generateRefreshTokenFromUser(user);
        return ResponseCookie.from(refreshCookieName, jwt)
                .path("/")
                .maxAge(refreshExpirySeconds)
                .httpOnly(true)
                .build();
    }

    // Get the JWT token from the client request
    public String getTokenFromCookies(HttpServletRequest request, String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null) {
            return null;
        }
        return cookie.getValue();
    }

    // Generates a blank JWT cookie - for clearing on log out
    public ResponseCookie getCleanCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "").path("/").build();
    }
}
