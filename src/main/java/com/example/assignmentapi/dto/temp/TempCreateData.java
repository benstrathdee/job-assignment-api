package com.example.assignmentapi.dto.temp;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class TempCreateData {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private Integer managerId;
}
