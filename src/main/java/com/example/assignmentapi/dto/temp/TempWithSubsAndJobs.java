package com.example.assignmentapi.dto.temp;

import com.example.assignmentapi.dto.job.JobAsChild;
import com.example.assignmentapi.entity.Temp;
import lombok.Getter;

import java.util.List;

@Getter
public class TempWithSubsAndJobs extends TempWithJobs {
    private final List<TempAsChild> subordinates;

    public TempWithSubsAndJobs(Integer id, String firstName, String lastName, List<TempAsChild> subordinates, List<JobAsChild> jobs) {
        super(id, firstName, lastName, jobs);
        this.subordinates = subordinates;
    }
}
