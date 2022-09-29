package com.example.assignmentapi.DTOs.Temp;

import javax.validation.constraints.NotBlank;

public class TempCreateDTO {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String managerId;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getManagerId() {
        return managerId;
    }
}
