package me.strathdee.assignmentapi.utilities;

import me.strathdee.assignmentapi.entity.Job;
import me.strathdee.assignmentapi.entity.Temp;
import me.strathdee.assignmentapi.security.UserPrincipal;
import me.strathdee.assignmentapi.dto.job.JobReturnDTO;
import me.strathdee.assignmentapi.dto.temp.TempReturnDTO;
import me.strathdee.assignmentapi.dto.user.UserReturnDTO;
import me.strathdee.assignmentapi.entity.User;

import java.util.*;
import java.util.stream.Collectors;

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

    // Create a DTO of a user with no password field from a User entity
    public static UserReturnDTO build(User user) {
        UserReturnDTO.Builder builder = UserReturnDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole());

        return builder.build();
    }

    // Create a DTO of a user with no password field from a UserPrincipal
    public static UserReturnDTO build(UserPrincipal user) {
        UserReturnDTO.Builder builder = UserReturnDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail());

        String role = user.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));

        builder.role(role);

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
