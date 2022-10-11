package com.example.assignmentapi.dto.temp;

import lombok.Getter;

import java.util.List;

@Getter
public class TempWithNestedSubs extends TempAsChild {
    private final List<TempWithNestedSubs> subordinates;

    public TempWithNestedSubs(Integer id, String firstName, String lastName, List<TempWithNestedSubs> subordinates) {
        super(id, firstName, lastName);
        this.subordinates = subordinates;
    }
}
