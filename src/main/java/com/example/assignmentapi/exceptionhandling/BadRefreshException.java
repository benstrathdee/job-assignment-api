package com.example.assignmentapi.exceptionhandling;

import org.springframework.security.oauth2.jwt.JwtException;

import java.io.Serial;

public class BadRefreshException extends JwtException {
    @Serial
    private static final long serialVersionUID = 1L;

    public BadRefreshException() {
        super("There was an issue refreshing the tokens.");
    }
}
