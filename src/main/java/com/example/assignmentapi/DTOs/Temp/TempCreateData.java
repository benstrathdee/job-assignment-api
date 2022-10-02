package com.example.assignmentapi.DTOs.Temp;

import javax.validation.constraints.NotBlank;

public class TempCreateData {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private Integer managerId;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getManagerId() {
        return managerId;
    }
}
