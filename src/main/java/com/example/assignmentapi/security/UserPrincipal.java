package com.example.assignmentapi.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.*;

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

    @Builder
    public UserPrincipal(Integer id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Integer id() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserPrincipal user = (UserPrincipal) o;
        return Objects.equals(id, user.id);
    }
}
