package com.example.assignmentapi.utilities;

import com.example.assignmentapi.dto.job.JobReturnDTO;
import com.example.assignmentapi.dto.temp.*;
import com.example.assignmentapi.entity.Job;
import com.example.assignmentapi.entity.Temp;

import java.util.*;

public final class DTODirector {
    // Creates a DTO of a Temp with no attached job
    public static TempReturnDTO buildTemp(Temp temp) {
        if (temp != null) {
            return TempReturnDTO.builder()
                    .id(temp.getId())
                    .firstName(temp.getFirstName())
                    .lastName(temp.getLastName())
                    .build();
        }
        return null;
    }

    // Create a DTO of a job with no attached temp
    public static JobReturnDTO buildJob(Job job) {
        if (job != null) {
            return JobReturnDTO.builder()
                    .id(job.getId())
                    .name(job.getName())
                    .startDate(job.getStartDate())
                    .endDate(job.getEndDate())
                    .build();
        }
        return null;
    }

    // Creates a DTO of temp including any assigned jobs
    public static TempReturnDTO buildTempWithJobs(Temp temp, Set<Job> jobs) {
        TempReturnDTO.Builder builder = TempReturnDTO.builder()
                .id(temp.getId())
                .firstName(temp.getFirstName())
                .lastName(temp.getLastName());

        if (jobs != null) jobs.forEach(job -> builder.job(DTODirector.buildJob(job)));

        return builder.build();
    }

    // Create a DTO of job with assigned temp
    public static JobReturnDTO buildJobWithTemp(Job job, Temp temp) {
        JobReturnDTO.Builder builder = JobReturnDTO.builder()
                .id(job.getId())
                .name(job.getName())
                .startDate(job.getStartDate())
                .endDate(job.getEndDate())
                .temp(DTODirector.buildTemp(temp));

        return builder.build();
    }

    // Creates a DTO of temp including any subordinates and assigned jobs
    public static TempReturnDTO buildTempWithSubsAndJobs(Temp temp, List<Temp> subordinates, Set<Job> jobs) {
        TempReturnDTO.Builder builder = TempReturnDTO.builder()
                .id(temp.getId())
                .firstName(temp.getFirstName())
                .lastName(temp.getLastName());

        subordinates.forEach(sub -> builder.subordinate(DTODirector.buildTemp(sub)));
        jobs.forEach(job -> builder.job(DTODirector.buildJob(job)));

        return builder.build();
    }

    public static TempReturnDTO buildTempWithNestedSubs(Temp temp, List<TempReturnDTO> directSubordinates) {
        TempReturnDTO.Builder builder = TempReturnDTO.builder()
                .id(temp.getId())
                .firstName(temp.getFirstName())
                .lastName(temp.getLastName())
                .directSubordinates(directSubordinates);

        return builder.build();
    }
}
