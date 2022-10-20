package com.example.assignmentapi.exceptionhandling;

import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class ControllerExceptionHandler {
    // This class essentially wraps around all RestControllers and handles the specified exceptions
    // Allows for these custom messages to be sent for things like 404 errors

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            TempNotAvailableException.class,
            UserDataExistsException.class,
            HttpMessageNotReadableException.class,
            PropertyValueException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public CustomErrorMessage badData(RuntimeException e, WebRequest request) {
        return new CustomErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                e.getMessage(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public CustomErrorMessage resourceNotFound(ResourceNotFoundException e, WebRequest request) {
        return new CustomErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                e.getMessage(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public CustomErrorMessage badCredentials(BadCredentialsException e, WebRequest request) {
        return new CustomErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                new Date(),
                e.getMessage(),
                request.getDescription(false)
        );
    }
}
