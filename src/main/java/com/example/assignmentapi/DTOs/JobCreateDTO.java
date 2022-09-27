package com.example.assignmentapi.DTOs;

import javax.validation.constraints.NotBlank;

public class JobCreateDTO {
    @NotBlank
    private String name;
    @NotBlank
    private Integer startDate;
    @NotBlank
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
