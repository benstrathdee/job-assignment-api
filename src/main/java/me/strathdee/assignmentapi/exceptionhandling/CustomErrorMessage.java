package com.example.assignmentapi.exceptionhandling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
public class CustomErrorMessage {
    private int statusCode;
    private Date timestamp;
    private String message;
    private String description;
}
