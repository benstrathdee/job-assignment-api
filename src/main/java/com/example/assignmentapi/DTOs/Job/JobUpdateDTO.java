package com.example.assignmentapi.DTOs.Job;

public class JobUpdateDTO {
    private String name;
    private Long startDate;
    private Long endDate;
    private Integer tempId;

    public String getName() {
        return name;
    }

    public Long getStartDate() {
        return startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public Integer getTempId() {
        return tempId;
    }
}
