package me.strathdee.assignmentapi.dto.user;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UserRegistrationData {
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
