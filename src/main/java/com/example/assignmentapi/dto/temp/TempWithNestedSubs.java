package com.example.assignmentapi.DTOs.Temp;

import com.example.assignmentapi.Entities.Temp;

import java.util.List;

public class TempWithNestedSubs extends TempAsChild {
    private final List<TempWithNestedSubs> subordinates;

    public List<TempWithNestedSubs> getSubordinates() {
        return subordinates;
    }

    public TempWithNestedSubs(Temp temp, List<TempWithNestedSubs> subordinates) {
        super(temp);
        this.subordinates = subordinates;
    }
}
