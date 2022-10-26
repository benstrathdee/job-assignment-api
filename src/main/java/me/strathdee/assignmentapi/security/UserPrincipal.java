package com.example.assignmentapi.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.*;

@Builder
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private final Integer id;

    private final String username;

    private final String email;

    private final String fingerprint;

    @JsonIgnore // don't risk ever returning this field
    private final String password;

    // Authorities are essentially roles that can be checked against (e.g. user/admin permissions)
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public boolean isAccountNonExpired() {
        return true; // TODO
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // TODO
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // TODO
    }

    @Override
    public boolean isEnabled() {
        return true; // TODO
    }
}
