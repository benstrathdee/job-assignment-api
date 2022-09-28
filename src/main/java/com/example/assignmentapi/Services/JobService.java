package com.example.assignmentapi.Services;

import com.example.assignmentapi.DTOs.JobCreateDTO;
import com.example.assignmentapi.DTOs.JobUpdateDTO;
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
        // Check if specified temp is available for a job in the date range provided
        List<Integer> availableTemps = this.tempRepository.findByDates(startDate, endDate);
        return availableTemps.contains(tempId);
    }

    public Job create(JobCreateDTO data) {
        if (data.getTempId() != null) {
            if (!checkAvailability(data.getTempId(), data.getStartDate(), data.getEndDate())) {
                return null;
            }
            Optional<Temp> fetchedTemp = this.tempRepository.findById(data.getTempId());
            Job job = new Job(data.getName(), data.getStartDate(), data.getEndDate(), fetchedTemp.orElse(null));
            return this.repository.save(job);
        } else {
            Job job = new Job(data.getName(), data.getStartDate(), data.getEndDate(), null);
            return this.repository.save(job);
        }
    }

    public ArrayList<Job> all() {
        return new ArrayList<>(this.repository.findAll());
    }

    public ArrayList<Job> getByAssigned(Boolean assigned) {
        if (assigned) {
            return new ArrayList<>(this.repository.findByIsAssigned());
        } else {
            return new ArrayList<>(this.repository.findByIsNotAssigned());
        }

    }

    public Job getById(Integer id) {
        Optional<Job> fetchedJob = this.repository.findById(id);
        return fetchedJob.orElse(null);
    }

    public Job update(Integer id, JobUpdateDTO data) {
        Job job = getById(id);
        if (job != null) {
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
                if (!checkAvailability(data.getTempId(), job.getStartDate(), job.getEndDate())) {
                    return null;
                }
                Optional<Temp> fetchedTemp = tempRepository.findById(data.getTempId());
                if (fetchedTemp.isPresent()) {
                    Temp temp = fetchedTemp.get();
                    job.setTemp(temp);
                }
            }
            return this.repository.save(job);
        }
        return null;
    }
}
