package com.example.assignmentapi.DTOs;

import javax.validation.constraints.NotBlank;

public class TempCreateDTO {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
