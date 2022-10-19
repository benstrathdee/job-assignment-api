package com.example.assignmentapi.service;

import com.example.assignmentapi.entity.User;
import com.example.assignmentapi.repository.UserRepository;
import com.example.assignmentapi.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> fetchedUser = userRepository.findByUsername(username);
        if (fetchedUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        User user = fetchedUser.get();

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));

        UserPrincipal.UserPrincipalBuilder builder = UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .authorities(authorities)
                .password(user.getPassword());

        return builder.build();
    }
}
