package com.example.assignmentapi.DTOs.Temp;

import com.example.assignmentapi.DTOs.Job.JobAsChild;
import com.example.assignmentapi.Entities.Temp;

import java.util.List;

public class TempWithSubsAndJobs extends TempWithJobs {
    private final List<TempAsChild> subordinates;

    public List<TempAsChild> getSubordinates() {
        return subordinates;
    }

    public TempWithSubsAndJobs(Temp temp, List<TempAsChild> subordinates, List<JobAsChild> jobs) {
        super(temp, jobs);
        this.subordinates = subordinates;
    }
}
