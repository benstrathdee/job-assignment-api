package com.example.assignmentapi.dto.temp;

import com.example.assignmentapi.dto.job.JobAsChild;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
public class TempWithSubsAndJobs extends TempWithJobs {
    private final List<TempAsChild> subordinates;

    @Builder(builderMethodName = "tempWithSubsAndJobsBuilder")
    public TempWithSubsAndJobs(Integer id, String firstName, String lastName, @Singular List<TempAsChild> subordinates, @Singular List<JobAsChild> jobs) {
        super(id, firstName, lastName, jobs);
        this.subordinates = subordinates;
    }
}
