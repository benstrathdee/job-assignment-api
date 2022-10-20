package com.example.assignmentapi.exceptionhandling;

import java.io.Serial;

public class ResourceNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException() {
        super("The resource you were trying to access was not available");
    }
}
