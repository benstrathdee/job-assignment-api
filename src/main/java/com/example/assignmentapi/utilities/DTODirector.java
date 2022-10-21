package com.example.assignmentapi.utilities;

import com.example.assignmentapi.dto.job.*;
import com.example.assignmentapi.dto.temp.*;
import com.example.assignmentapi.dto.user.*;
import com.example.assignmentapi.entity.*;

import java.util.*;

public final class DTODirector {
    // Create a DTO of a Temp with no attached job
    public static TempReturnDTO build(Temp temp) {
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
    public static JobReturnDTO build(Job job) {
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

    // Create a DTO of a user with no password field
    public static UserReturnDTO build(User user) {
        UserReturnDTO.Builder builder = UserReturnDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole());

        return builder.build();
    }

    // Create a DTO of a temp including any assigned jobs
    public static TempReturnDTO build(Temp temp, Set<Job> jobs) {
        TempReturnDTO.Builder builder = TempReturnDTO.builder()
                .id(temp.getId())
                .firstName(temp.getFirstName())
                .lastName(temp.getLastName());

        if (jobs != null) jobs.forEach(job -> builder.job(DTODirector.build(job)));

        return builder.build();
    }

    // Create a DTO of a job with assigned temp
    public static JobReturnDTO build(Job job, Temp temp) {
        JobReturnDTO.Builder builder = JobReturnDTO.builder()
                .id(job.getId())
                .name(job.getName())
                .startDate(job.getStartDate())
                .endDate(job.getEndDate())
                .temp(DTODirector.build(temp));

        return builder.build();
    }

    // Create a DTO of a temp including all subordinates and assigned jobs
    public static TempReturnDTO build(Temp temp, List<Temp> subordinates, Set<Job> jobs) {
        TempReturnDTO.Builder builder = TempReturnDTO.builder()
                .id(temp.getId())
                .firstName(temp.getFirstName())
                .lastName(temp.getLastName());

        subordinates.forEach(sub -> builder.subordinate(DTODirector.build(sub)));
        jobs.forEach(job -> builder.job(DTODirector.build(job)));

        return builder.build();
    }

    // Create a DTO of a temp with their direct subordinates
    public static TempReturnDTO build(Temp temp, List<TempReturnDTO> directSubordinates) {
        TempReturnDTO.Builder builder = TempReturnDTO.builder()
                .id(temp.getId())
                .firstName(temp.getFirstName())
                .lastName(temp.getLastName())
                .directSubordinates(directSubordinates);

        return builder.build();
    }
}
