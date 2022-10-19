package com.example.assignmentapi.security;

public enum Authorities {
    TEMP("TEMP"),
    ADMIN("ADMIN");

    private final String scope;

    Authorities(String scope) {
        this.scope = scope;
    }

    public String getTitle() {
        return scope;
    }
}
