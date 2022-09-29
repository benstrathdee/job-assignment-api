package com.example.assignmentapi.Services;

import com.example.assignmentapi.DTOs.Job.JobCreateDTO;
import com.example.assignmentapi.DTOs.Job.JobGetDTO;
import com.example.assignmentapi.DTOs.Job.JobUpdateDTO;
import com.example.assignmentapi.Entities.Job;
import com.example.assignmentapi.Entities.Temp;
import com.example.assignmentapi.Repositories.JobRepository;
import com.example.assignmentapi.Repositories.TempRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JobService {
    @Autowired
    JobRepository repository;
    @Autowired
    TempRepository tempRepository;

    public Boolean checkAvailability (Integer tempId, Long startDate, Long endDate) {
        // Check if a specified temp is available for a job in the date range provided
        List<Integer> availableTemps = this.tempRepository.findByDates(startDate, endDate);
        return availableTemps.contains(tempId);
    }

    public JobGetDTO create(JobCreateDTO data) {
        // Create a new job in the database
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
        return new JobGetDTO(job);
    }

    public ArrayList<JobGetDTO> all() {
        // Get all jobs from DB
        ArrayList<Job> jobEntities = new ArrayList<>(this.repository.findAll());
        ArrayList<JobGetDTO> jobGetDTOs = new ArrayList<>();
        // Create representations and send to client
        for (Job job : jobEntities) {
            jobGetDTOs.add(new JobGetDTO(job));
        }
        return jobGetDTOs;
    }

    public ArrayList<JobGetDTO> getByAssigned(Boolean assigned) {
        // List Jobs based on whether they're assigned to a temp
        ArrayList<Job> jobEntities = new ArrayList<>();
        ArrayList<JobGetDTO> jobGetDTOs = new ArrayList<>();
        if (assigned) {
            jobEntities.addAll(this.repository.findByIsAssigned());
        } else {
            jobEntities.addAll(this.repository.findByIsNotAssigned());
        }
        // Create representations and send to client
        for (Job job : jobEntities) {
            jobGetDTOs.add(new JobGetDTO(job));
        }
        return jobGetDTOs;
    }

    public JobGetDTO getById(Integer id) {
        // Get a single job from DB
        Optional<Job> fetchedJob = this.repository.findById(id);
        // If the job exists, send representation to client, otherwise 404
        return fetchedJob.map(JobGetDTO::new).orElse(null);
    }

    public JobGetDTO update(Integer id, JobUpdateDTO data) {
        // Update job in DB
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
            return new JobGetDTO(this.repository.save(job));
        }
        // No job exists, send bad request
        return null;
    }
}
