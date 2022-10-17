package com.example.assignmentapi.dto.job;

import com.example.assignmentapi.dto.temp.TempAsChild;
import lombok.Builder;
import lombok.Getter;

@Getter
public class JobWithTemp extends JobAsChild {
    private final TempAsChild temp;

    @Builder(builderMethodName = "jobWithTempBuilder")
    public JobWithTemp(Integer id, String name, Long startDate, Long endDate, TempAsChild temp) {
        super(id, name, startDate, endDate);
        this.temp = temp;
    }
}
