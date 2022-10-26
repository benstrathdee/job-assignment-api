package me.strathdee.assignmentapi.exceptionhandling;

public class UserDataExistsException extends RuntimeException {
    public UserDataExistsException() {
        super("The username or email provided already exists");
    }
}
