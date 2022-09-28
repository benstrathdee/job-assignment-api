package com.example.assignmentapi.DTOs;

import com.example.assignmentapi.Entities.Job;

public class JobReadDTO {
    private final Integer id;
    private final String name;
    private final Long startDate;
    private final Long endDate;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getStartDate() {
        return startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public JobReadDTO (Job job) {
        this.id = job.getId();
        this.name = job.getName();
        this.startDate = job.getStartDate();
        this.endDate = job.getEndDate();
    }
}
