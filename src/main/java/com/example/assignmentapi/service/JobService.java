package com.example.assignmentapi.service;

import com.example.assignmentapi.dto.job.JobCreateData;
import com.example.assignmentapi.dto.job.JobWithTemp;
import com.example.assignmentapi.dto.job.JobUpdateData;
import com.example.assignmentapi.entity.Job;
import com.example.assignmentapi.entity.Temp;
import com.example.assignmentapi.repository.JobRepository;
import com.example.assignmentapi.repository.TempRepository;
import com.example.assignmentapi.utilities.DTOBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JobService {
    @Autowired
    JobRepository jobRepository;
    @Autowired
    TempRepository tempRepository;

    // Returns true or false for if a given temp is available for a specific time frame
    public Boolean checkAvailability (Integer tempId, Long startDate, Long endDate) {
        List<Integer> availableTemps = tempRepository.findByDates(startDate, endDate);
        return availableTemps.contains(tempId);
    }

    // Create a new job in the DB
    public JobWithTemp createJob(JobCreateData data) {
        Job job;
        if (data.getTempId() != null) {
            // If a tempId is provided, check if temp is available for this job, if not return bad request
            if (!checkAvailability(data.getTempId(), data.getStartDate(), data.getEndDate())) {
                return null;
            }

            // If available, create new job with temp assigned
            Optional<Temp> fetchedTemp = tempRepository.findById(data.getTempId());
            job = new Job(data.getName(), data.getStartDate(), data.getEndDate(), fetchedTemp.orElse(null));

        } else {
            // No tempId provided, temp is null
            job = new Job(data.getName(), data.getStartDate(), data.getEndDate(), null);
        }
        // Save job to DB and send representation to client
        jobRepository.save(job);
        return DTOBuilder.buildJobWithTemp(job);
    }

    // Get a list of all jobs as DTOs including their assigned temps
    public List<JobWithTemp> getAllJobs() {
        List<Job> jobEntities = jobRepository.findAll();

        // Create representations and send to client
        return jobEntities
                .stream()
                .map(DTOBuilder::buildJobWithTemp)
                .toList();
    }

    // Get a list of either all assigned or unassigned jobs
    public List<JobWithTemp> getJobsByAssigned(Boolean assigned) {
        List<Job> jobEntities = assigned
                ? jobRepository.findByIsAssigned()
                : jobRepository.findByIsNotAssigned();

        // Create representations and send to client
        return jobEntities
                .stream()
                .map(DTOBuilder::buildJobWithTemp)
                .toList();
    }

    // Returns a DTO of a specific job with assigned temp
    public JobWithTemp getJobById(Integer id) {
        Optional<Job> fetchedJob = jobRepository.findById(id);

        // If the job exists, send representation to client, otherwise send null
        return fetchedJob.map(DTOBuilder::buildJobWithTemp).orElse(null);
    }

    // Updates the job in the DB - used to assign a temp/change any details
    public JobWithTemp updateJob(Integer id, JobUpdateData data) {
        // Check if job specified exists
        Optional<Job> fetchedJob = jobRepository.findById(id);
        if (fetchedJob.isPresent()) {
            // Update job for each field included in request body
            Job job = fetchedJob.get();
            if (data.getName() != null) {
                job.setName(data.getName());
            }
            if (data.getStartDate() != null) {
                job.setStartDate(data.getStartDate());
            }
            if (data.getEndDate() != null) {
                job.setEndDate(data.getEndDate());
            }
            // The more complicated one because it needs to check availability for the job etc.
            if (data.getTempId() != null) {
                if (!checkAvailability(data.getTempId(), job.getStartDate(), job.getEndDate())) {
                    // Not available, send bad request
                    return null;
                }

                Optional<Temp> fetchedTemp = tempRepository.findById(data.getTempId());
                if (fetchedTemp.isPresent()) {
                    // Temp is present and available, assign to job
                    Temp temp = fetchedTemp.get();
                    job.setTemp(temp);
                }
            }
            // Save job to DB and send representation to client
            jobRepository.save(job);
            return DTOBuilder.buildJobWithTemp(job);
        }
        // No job exists, send bad request
        return null;
    }
}
