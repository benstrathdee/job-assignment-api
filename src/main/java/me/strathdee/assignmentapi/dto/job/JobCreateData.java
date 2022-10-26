package me.strathdee.assignmentapi.dto.job;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class JobCreateData {
    @NotBlank
    private String name;
    @NotNull
    private Long startDate;
    @NotNull
    private Long endDate;
    private Integer tempId;
}
