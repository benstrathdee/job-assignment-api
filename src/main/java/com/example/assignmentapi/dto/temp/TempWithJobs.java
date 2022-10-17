package com.example.assignmentapi.dto.temp;

import com.example.assignmentapi.dto.job.JobAsChild;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
public class TempWithJobs extends TempAsChild {
    private final List<JobAsChild> jobs;

    @Builder(builderMethodName = "tempWithJobsBuilder")
    public TempWithJobs(Integer id, String firstName, String lastName, @Singular List<JobAsChild> jobs) {
        super(id, firstName, lastName);
        this.jobs = jobs;
    }
}
