package com.example.assignmentapi.DTOs.Temp;

import com.example.assignmentapi.DTOs.Job.JobAsChild;
import com.example.assignmentapi.Entities.Temp;

import java.util.List;

public class TempWithJobs extends TempAsChild {
    private final List<JobAsChild> jobs;

    public List<JobAsChild> getJobs() {
        return jobs;
    }

    public TempWithJobs(Temp temp, List<JobAsChild> jobs) {
        super(temp);
        this.jobs = jobs;
    }
}
