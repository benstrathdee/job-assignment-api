package com.example.assignmentapi.dto.user;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder")
public class UserReturnDTO {
    @NonNull
    String username;
    @NonNull
    String email;
    @NonNull
    String role;
}
