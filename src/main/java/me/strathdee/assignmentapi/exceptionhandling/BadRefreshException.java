package me.strathdee.assignmentapi.exceptionhandling;

import org.springframework.security.oauth2.jwt.JwtException;

public class BadRefreshException extends JwtException {
    public BadRefreshException() {
        super("There was an issue refreshing the tokens.");
    }
}
