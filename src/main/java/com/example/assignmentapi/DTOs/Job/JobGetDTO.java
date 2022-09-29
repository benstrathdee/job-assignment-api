package com.example.assignmentapi.DTOs.Job;

import com.example.assignmentapi.DTOs.Temp.TempAsChildDTO;
import com.example.assignmentapi.Entities.Job;

public class JobGetDTO {
    private final Integer id;
    private final String name;
    private final Long startDate;
    private final Long endDate;
    private final TempAsChildDTO temp;

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

    public TempAsChildDTO getTemp() {
        return temp;
    }

    public JobGetDTO(Job job) {
        this.id = job.getId();
        this.name = job.getName();
        this.startDate = job.getStartDate();
        this.endDate = job.getEndDate();
        this.temp = job.getTemp() != null ? new TempAsChildDTO(job.getTemp()) : null;
    }
}
