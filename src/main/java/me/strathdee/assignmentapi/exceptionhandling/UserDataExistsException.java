package com.example.assignmentapi.exceptionhandling;

import java.io.Serial;

public class UserDataExistsException extends RuntimeException {
    public UserDataExistsException() {
        super("The username or email provided already exists");
    }
}
