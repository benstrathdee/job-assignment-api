package com.example.assignmentapi.dto.temp;

import com.example.assignmentapi.dto.job.JobAsChild;
import lombok.Getter;

import java.util.List;

@Getter
public class TempWithJobs extends TempAsChild {
    private final List<JobAsChild> jobs;

    public TempWithJobs(Integer id, String firstName, String lastName, List<JobAsChild> jobs) {
        super(id, firstName, lastName);
        this.jobs = jobs;
    }
}
