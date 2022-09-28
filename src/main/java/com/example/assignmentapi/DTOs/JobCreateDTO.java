package com.example.assignmentapi.DTOs;

import javax.validation.constraints.NotBlank;

public class JobCreateDTO {
    @NotBlank
    private String name;
    @NotBlank
    private Long startDate;
    @NotBlank
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
