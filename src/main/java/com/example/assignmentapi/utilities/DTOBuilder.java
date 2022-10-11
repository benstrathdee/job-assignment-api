package com.example.assignmentapi.utilities;

import com.example.assignmentapi.dto.job.JobAsChild;
import com.example.assignmentapi.dto.job.JobWithTemp;
import com.example.assignmentapi.dto.temp.TempAsChild;
import com.example.assignmentapi.dto.temp.TempWithJobs;
import com.example.assignmentapi.dto.temp.TempWithSubsAndJobs;
import com.example.assignmentapi.entity.Job;
import com.example.assignmentapi.entity.Temp;

import java.util.*;

public final class DTOBuilder {
    // Creates a DTO of a Temp with no attached job
    public static TempAsChild buildTempAsChild(Temp temp) {
        return new TempAsChild(
                temp.getId(),
                temp.getFirstName(),
                temp.getLastName()
        );
    }

    // Create a DTO of a job with no attached temp
    public static JobAsChild buildJobAsChild(Job job) {
        return new JobAsChild(
                job.getId(),
                job.getName(),
                job.getStartDate(),
                job.getEndDate()
        );
    }

    // Creates a DTO of temp including any assigned jobs
    public static TempWithJobs buildTempWithJobs(Temp temp) {
        List<JobAsChild> jobs = Collections.emptyList();
        if (temp != null) {
            Set<Job> fetchedJobs = temp.getJobs();
            if (fetchedJobs != null) {
                jobs = fetchedJobs
                        .stream()
                        .map(DTOBuilder::buildJobAsChild)
                        .toList();
            }
        }
        assert temp != null;
        return new TempWithJobs(
                temp.getId(),
                temp.getFirstName(),
                temp.getLastName(),
                jobs
        );
    }

    // Create a DTO of job with assigned temp
    public static JobWithTemp buildJobWithTemp(Job job) {
        if (job != null) {
            Temp fetchedTemp = job.getTemp();
            if (fetchedTemp != null) {
                return new JobWithTemp(
                        job.getId(),
                        job.getName(),
                        job.getStartDate(),
                        job.getEndDate(),
                        DTOBuilder.buildTempAsChild(fetchedTemp)
                );
            }
            return new JobWithTemp(
                    job.getId(),
                    job.getName(),
                    job.getStartDate(),
                    job.getEndDate(),
                    null
            );
        }
        return null;
    }

    // Creates a DTO of temp including any subordinates and assigned jobs
    public static TempWithSubsAndJobs buildTempWithSubsAndJobs(Temp temp, List<Temp> subordinates) {
        if (temp != null) {
            List<TempAsChild> subordinatesAsChildren = subordinates
                    .stream()
                    .map(DTOBuilder::buildTempAsChild)
                    .toList();

            List<JobAsChild> jobs = temp.getJobs()
                    .stream()
                    .map(DTOBuilder::buildJobAsChild)
                    .toList();

            return new TempWithSubsAndJobs(
                    temp.getId(),
                    temp.getFirstName(),
                    temp.getLastName(),
                    subordinatesAsChildren,
                    jobs
            );
        }
        return null;
    }
}
