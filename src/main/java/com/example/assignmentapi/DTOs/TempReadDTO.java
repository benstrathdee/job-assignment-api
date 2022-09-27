package com.example.assignmentapi.DTOs;

import com.example.assignmentapi.Entities.Temp;

public class TempReadDTO {
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

    public TempReadDTO (Temp temp) {
        this.id = temp.getId();
        this.firstName = temp.getFirstName();
        this.lastName = temp.getLastName();
    }
}
