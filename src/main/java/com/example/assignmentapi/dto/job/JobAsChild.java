package com.example.assignmentapi.dto.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JobAsChild {
    private final Integer id;
    private final String name;
    private final Long startDate;
    private final Long endDate;
}
