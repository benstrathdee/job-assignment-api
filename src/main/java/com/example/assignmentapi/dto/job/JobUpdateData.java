package com.example.assignmentapi.dto.job;

import lombok.Getter;

@Getter
public class JobUpdateData {
    private String name;
    private Long startDate;
    private Long endDate;
    private Integer tempId;
}
