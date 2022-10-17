package com.example.assignmentapi.dto.temp;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class TempWithNestedSubs extends TempAsChild {
    private final List<TempWithNestedSubs> subordinates;

    @Builder(builderMethodName = "tempWithNestedSubsBuilder")
    public TempWithNestedSubs(Integer id, String firstName, String lastName, List<TempWithNestedSubs> subordinates) {
        super(id, firstName, lastName);
        this.subordinates = subordinates;
    }
}
