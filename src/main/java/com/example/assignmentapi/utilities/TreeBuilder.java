package com.example.assignmentapi.utilities;

import com.example.assignmentapi.dto.temp.TempWithNestedSubs;
import com.example.assignmentapi.entity.Temp;
import com.example.assignmentapi.repository.TempRepository;

import java.util.ArrayList;
import java.util.Optional;

public final class TreeBuilder {
    // Recursively get direct children of a temp, building representation of a nested set
    public static ArrayList<TempWithNestedSubs> buildTree(TempRepository repository, Integer leftVal, Integer rightVal) {
        ArrayList<TempWithNestedSubs> children = new ArrayList<>();
        while (leftVal < rightVal) {
            Optional<Temp> fetchedTemp = repository.findByLeftVal(leftVal);
            if (fetchedTemp.isPresent()) {
                // Found a direct child
                Temp child = fetchedTemp.get();

                // Get this child's children (recursion!!!)
                ArrayList<TempWithNestedSubs> childChildren = buildTree(
                        repository,
                        child.getLeftVal() + 1,
                        child.getRightVal() - 1
                );

                children.add(
                        new TempWithNestedSubs(
                                child.getId(),
                                child.getFirstName(),
                                child.getLastName(),
                                childChildren
                        )
                );

                // Skip over this direct child's own children so that they're not added to the parent
                leftVal = child.getRightVal() + 1;
            } else {
                // Keep going until a direct child is found
                leftVal ++;
            }
        }
        return children;
    }
}
