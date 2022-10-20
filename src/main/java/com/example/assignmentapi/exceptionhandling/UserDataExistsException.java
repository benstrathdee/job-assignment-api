package com.example.assignmentapi.exceptionhandling;

import java.io.Serial;

public class UserDataExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserDataExistsException() {
        super("The username or email provided already exists");
    }
}
