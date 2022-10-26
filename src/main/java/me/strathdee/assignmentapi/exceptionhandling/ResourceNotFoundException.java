package com.example.assignmentapi.exceptionhandling;

import java.io.Serial;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("The resource you were trying to access was not available");
    }
}
