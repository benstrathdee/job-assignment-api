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
    @Serial
    private static final long serialVersionUID = 1L;
    // UserDetails is a Serializable, which uses this ID during de-serialization. If not supplied,
    // compiler will generate one, but defining one is generally the better idea
    // https://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it

    private final Integer id;

    private final String username;

    private final String email;

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
