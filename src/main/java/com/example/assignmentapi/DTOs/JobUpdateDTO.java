package com.example.assignmentapi.DTOs;

public class JobUpdateDTO {
    private String name;
    private Integer startDate;
    private Integer endDate;
    private Integer tempId;

    public String getName() {
        return name;
    }

    public Integer getStartDate() {
        return startDate;
    }

    public Integer getEndDate() {
        return endDate;
    }

    public Integer getTempId() {
        return tempId;
    }
}
