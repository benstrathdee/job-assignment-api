package com.example.assignmentapi.Services;

import com.example.assignmentapi.DTOs.Job.JobCreateData;
import com.example.assignmentapi.DTOs.Job.JobWithTemp;
import com.example.assignmentapi.DTOs.Job.JobUpdateData;
import com.example.assignmentapi.DTOs.Temp.TempAsChild;
import com.example.assignmentapi.Entities.Job;
import com.example.assignmentapi.Entities.Temp;
import com.example.assignmentapi.Repositories.JobRepository;
import com.example.assignmentapi.Repositories.TempRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JobService {
    @Autowired
    JobRepository repository;
    @Autowired
    TempRepository tempRepository;

    // Create a DTO of job with assigned temp
    public JobWithTemp createJobWithTemp (Job job) {
        if (job != null) {
            Temp fetchedTemp = job.getTemp();
            return new JobWithTemp(job, fetchedTemp != null ? new TempAsChild(fetchedTemp) : null);
        }
        return null;
    }

    // Returns true or false for if a given temp is available for a specific time frame
    public Boolean checkAvailability (Integer tempId, Long startDate, Long endDate) {
        List<Integer> availableTemps = this.tempRepository.findByDates(startDate, endDate);
        return availableTemps.contains(tempId);
    }

    // Create a new job in the DB
    public JobWithTemp create(JobCreateData data) {
        Job job;
        if (data.getTempId() != null) {
            // If a tempId is provided, check if temp is available for this job, if not return bad request
            if (!checkAvailability(data.getTempId(), data.getStartDate(), data.getEndDate())) {
                return null;
            }
            // If available, create new job with temp assigned
            Optional<Temp> fetchedTemp = this.tempRepository.findById(data.getTempId());
            job = new Job(data.getName(), data.getStartDate(), data.getEndDate(), fetchedTemp.orElse(null));
        } else {
            // No tempId provided, temp is null
            job = new Job(data.getName(), data.getStartDate(), data.getEndDate(), null);
        }
        // Save job to DB and send representation to client
        this.repository.save(job);
        return createJobWithTemp(job);
    }

    // Get a list of all jobs as DTOs including their assigned temps
    public List<JobWithTemp> getAll() {
        List<Job> jobEntities = this.repository.findAll();
        // Create representations and send to client
        List<JobWithTemp> jobs = jobEntities
                .stream()
                .map(this::createJobWithTemp)
                .toList();
        return jobs.isEmpty() ? Collections.emptyList() : jobs;
    }

    // Get a list of either all assigned or unassigned jobs
    public List<JobWithTemp> getByAssigned(Boolean assigned) {
        List<Job> jobEntities = assigned ? this.repository.findByIsAssigned() : this.repository.findByIsNotAssigned();
        // Create representations and send to client
        return jobEntities
                .stream()
                .map(this::createJobWithTemp)
                .toList();
    }

    // Returns a DTO of a specific job with assigned temp
    public JobWithTemp getById(Integer id) {
        Optional<Job> fetchedJob = this.repository.findById(id);
        // If the job exists, send representation to client, otherwise send null
        return fetchedJob.map(this::createJobWithTemp).orElse(null);
    }

    // Updates the job in the DB - used to assign a temp/change any details
    public JobWithTemp update(Integer id, JobUpdateData data) {
        // Check if job specified exists
        Optional<Job> fetchedJob = this.repository.findById(id);
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
            if (data.getTempId() != null) {
                // if a tempId provided, check if this temp is available for this job's date range
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
            this.repository.save(job);
            return createJobWithTemp(job);
        }
        // No job exists, send bad request
        return null;
    }
}
