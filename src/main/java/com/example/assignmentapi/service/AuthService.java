package com.example.assignmentapi.service;

import com.example.assignmentapi.dto.user.UserLoginData;
import com.example.assignmentapi.dto.user.UserRegistrationData;
import com.example.assignmentapi.dto.user.UserReturnDTO;
import com.example.assignmentapi.entity.User;
import com.example.assignmentapi.exceptionhandling.ResourceNotFoundException;
import com.example.assignmentapi.exceptionhandling.UserDataExistsException;
import com.example.assignmentapi.repository.TempRepository;
import com.example.assignmentapi.repository.UserRepository;
import com.example.assignmentapi.security.jwt.JwtUtils;
import com.example.assignmentapi.security.UserPrincipal;
import com.example.assignmentapi.utilities.DTODirector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TempRepository tempRepository;
    @Autowired
    JwtUtils jwtUtils;

    public Authentication authenticateUser (UserLoginData data) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    // Return a JWT in a cookie for an authenticated user
    public ResponseCookie getAuthCookie (Authentication authentication) {
        return jwtUtils.generateJwtCookie(authentication);
    }

    // Get a representation of the user from authentication data
    public UserReturnDTO getUser (Authentication authentication) {
        // Get user details from authentication
        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

        // Get user from DB
        Optional<User> fetchedUser = userRepository.findByUsername(userDetails.getUsername());
        if (fetchedUser.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        // Return representation of user to client
        User user = fetchedUser.get();
        return DTODirector.buildUser(user);
    }

    // Log out a user by clearing their JWT cookie
    public ResponseCookie logoutUser () {
        return jwtUtils.getCleanJwtCookie();
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
        return DTODirector.buildUser(user);
    }
}
