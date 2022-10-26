package me.strathdee.assignmentapi.exceptionhandling;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("The resource you were trying to access was not available");
    }
}
