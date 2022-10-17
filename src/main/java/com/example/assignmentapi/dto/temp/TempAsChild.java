package com.example.assignmentapi.dto.temp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "TempAsChildBuilder")
@AllArgsConstructor
public class TempAsChild {
    private final Integer id;
    private final String firstName;
    private final String lastName;
}
