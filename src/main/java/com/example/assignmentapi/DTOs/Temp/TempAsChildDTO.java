package com.example.assignmentapi.DTOs.Temp;

import com.example.assignmentapi.Entities.Temp;

public class TempAsChildDTO {
    private final Integer id;
    private final String firstName;
    private final String lastName;

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public TempAsChildDTO(Temp temp) {
        this.id = temp.getId();
        this.firstName = temp.getFirstName();
        this.lastName = temp.getLastName();
    }
}
