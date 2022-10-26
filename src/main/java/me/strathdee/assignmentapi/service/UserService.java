package com.example.assignmentapi.service;

import com.example.assignmentapi.entity.User;
import com.example.assignmentapi.repository.UserRepository;
import com.example.assignmentapi.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    // Implements UserDetailsService's method which is used by the authentication service
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Get the user from the database
        Optional<User> fetchedUser = userRepository.findByUsername(username);
        if (fetchedUser.isEmpty()) {
            // User doesn't exist
            throw new UsernameNotFoundException(username);
        }
        User user = fetchedUser.get();

        // Build the representation of the user that gets passed to other methods
        // UserPrincipal implements the UserDetails class, which is what Spring's
        // authentication uses
        UserPrincipal.UserPrincipalBuilder builder = UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fingerprint(user.getFingerprint())
                .password(user.getPassword());

        builder.authorities(List.of(new SimpleGrantedAuthority(user.getRole())));

        return builder.build();
    }

    // Checks whether the provided fingerprint is the same as that of the user specified
    public Boolean isUserRefreshFingerprintCorrect(String username, String fingerprint) {
        UserPrincipal user = (UserPrincipal) loadUserByUsername(username);
        if (!user.getFingerprint().equals(fingerprint)) {
            throw new JwtException("The refresh fingerprint was invalid.");
        }
        return true;
    }

    // Updates the refreshFingerprint field on a user - used any time a new token is issued
    // Prevents the re-use of any old tokens
    public UserPrincipal updateUserFingerprint(String username) {
        Optional<User> fetchedUser = userRepository.findByUsername(username);
        if (fetchedUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        User user = fetchedUser.get();

        // New random fingerprint
        user.setFingerprint(UUID.randomUUID().toString());

        // Save the user to the repository,
        userRepository.save(user);
        return (UserPrincipal) loadUserByUsername(user.getUsername());
    }
}
