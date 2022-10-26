package com.example.assignmentapi.dto.user;


import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UserLoginData {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
