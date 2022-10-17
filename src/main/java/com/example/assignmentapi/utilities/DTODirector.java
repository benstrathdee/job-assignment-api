package com.example.assignmentapi.utilities;

import com.example.assignmentapi.dto.job.JobAsChild;
import com.example.assignmentapi.dto.job.JobWithTemp;
import com.example.assignmentapi.dto.temp.TempAsChild;
import com.example.assignmentapi.dto.temp.TempWithJobs;
import com.example.assignmentapi.dto.temp.TempWithNestedSubs;
import com.example.assignmentapi.dto.temp.TempWithSubsAndJobs;
import com.example.assignmentapi.entity.Job;
import com.example.assignmentapi.entity.Temp;
import com.example.assignmentapi.repository.TempRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class DTODirector {
    // Creates a DTO of a Temp with no attached job
    public static TempAsChild buildTemp(Temp temp) {
        if (temp != null) {
            return TempAsChild.builder()
                    .id(temp.getId())
                    .firstName(temp.getFirstName())
                    .lastName(temp.getLastName())
                    .build();
        }
        return null;
    }

    // Create a DTO of a job with no attached temp
    public static JobAsChild buildJob(Job job) {
        if (job != null) {
            return JobAsChild.builder()
                    .id(job.getId())
                    .name(job.getName())
                    .startDate(job.getStartDate())
                    .endDate(job.getEndDate())
                    .build();
        }
        return null;
    }

    // Creates a DTO of temp including any assigned jobs
    public static TempWithJobs buildTempWithJobs(Temp temp, Set<Job> jobs) {
        if (temp != null) {
            TempWithJobs.TempWithJobsBuilder builder = TempWithJobs.tempWithJobsBuilder()
                    .id(temp.getId())
                    .firstName(temp.getFirstName())
                    .lastName(temp.getLastName());

            if (jobs != null) {
                jobs.forEach(job -> builder.job(DTODirector.buildJob(job)));
            }

            return builder.build();
        }
        return null;
    }

    // Create a DTO of job with assigned temp
    public static JobWithTemp buildJobWithTemp(Job job, Temp temp) {
        if (job != null) {
            JobWithTemp.JobWithTempBuilder builder = JobWithTemp.jobWithTempBuilder()
                    .id(job.getId())
                    .name(job.getName())
                    .startDate(job.getStartDate())
                    .endDate(job.getEndDate());

            builder.temp(DTODirector.buildTemp(temp));

            return builder.build();
        }
        return null;
    }

    // Creates a DTO of temp including any subordinates and assigned jobs
    public static TempWithSubsAndJobs buildTempWithSubsAndJobs(Temp temp, List<Temp> subordinates, Set<Job> jobs) {
        if (temp != null) {
            TempWithSubsAndJobs.TempWithSubsAndJobsBuilder builder = TempWithSubsAndJobs.tempWithSubsAndJobsBuilder()
                    .id(temp.getId())
                    .firstName(temp.getFirstName())
                    .lastName(temp.getLastName());

            if (subordinates != null) {
                subordinates
                        .forEach(sub -> builder.subordinate(DTODirector.buildTemp(sub)));
            }

            if (jobs != null) {
                jobs.forEach(job -> builder.job(DTODirector.buildJob(job)));
            }

            return builder.build();
        }
        return null;
    }

    // Recursively get direct children of a temp, building representation of a nested set
    public static ArrayList<TempWithNestedSubs> buildTree(TempRepository repository, Integer leftVal, Integer rightVal) {
        ArrayList<TempWithNestedSubs> children = new ArrayList<>();
        while (leftVal < rightVal) {
            Optional<Temp> fetchedTemp = repository.findByLeftVal(leftVal);
            if (fetchedTemp.isPresent()) {
                // Found a direct child
                Temp temp = fetchedTemp.get();
                TempWithNestedSubs.TempWithNestedSubsBuilder builder = TempWithNestedSubs.tempWithNestedSubsBuilder()
                        .id(temp.getId())
                        .firstName(temp.getFirstName())
                        .lastName(temp.getLastName());

                // Get this child's children (recursion!!!)
                builder.subordinates(
                    buildTree(
                        repository,
                        temp.getLeftVal() + 1,
                        temp.getRightVal() - 1
                    )
                );

                children.add(builder.build());

                // Skip over this direct child's own children so that they're not added to the parent
                leftVal = temp.getRightVal() + 1;
            } else {
                // Keep going until a direct child is found
                leftVal ++;
            }
        }
        return children;
    }
}
