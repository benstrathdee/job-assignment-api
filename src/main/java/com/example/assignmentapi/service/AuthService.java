package com.example.assignmentapi.service;

import com.example.assignmentapi.dto.user.UserLoginData;
import com.example.assignmentapi.dto.user.UserRegistrationData;
import com.example.assignmentapi.entity.User;
import com.example.assignmentapi.repository.TempRepository;
import com.example.assignmentapi.repository.UserRepository;
import com.example.assignmentapi.security.jwt.JwtUtils;
import com.example.assignmentapi.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public ResponseCookie getAuthCookie (UserLoginData data) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(authentication);

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return jwtCookie;
    }

    public User registerUser (UserRegistrationData data) {
        if (userRepository.existsByUsername(data.getUsername()) ||
                userRepository.existsByEmail(data.getEmail())) {
            return null;
        }
        User.UserBuilder builder = User.builder()
                .username(data.getUsername())
                .email(data.getEmail())
                .password(encoder.encode(data.getPassword()))
                .role("user") // TODO - handle this better
                .temp(null /*TODO - make a way to link temp to user*/);

        User user = builder.build();

        userRepository.save(user);

        return user;
    }
}
