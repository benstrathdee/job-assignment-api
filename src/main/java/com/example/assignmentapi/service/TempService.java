package com.example.assignmentapi.Services;

import com.example.assignmentapi.dto.Job.JobAsChild;
import com.example.assignmentapi.dto.Temp.*;
import com.example.assignmentapi.Entities.Job;
import com.example.assignmentapi.Entities.Temp;
import com.example.assignmentapi.Repositories.JobRepository;
import com.example.assignmentapi.Repositories.TempRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class TempService {
    @Autowired
    TempRepository repository;
    @Autowired
    JobRepository jobRepository;

    // Creates a DTO of temp including any assigned jobs
    public TempWithJobs createTempWithJobs (Temp temp) {
        List<JobAsChild> jobs = Collections.emptyList();
        if (temp != null) {
            Set<Job> fetchedJobs = temp.getJobs();
            if (fetchedJobs != null) {
                jobs = fetchedJobs
                        .stream()
                        .map(JobAsChild::new)
                        .toList();
            }
        }
        return new TempWithJobs(temp, jobs);
    }

    // Creates a DTO of temp including any subordinates and assigned jobs
    public TempWithSubsAndJobs createTempWithSubsAndJobs (Temp temp) {
        if (temp != null) {
            List<Temp> fetchedSubordinates = this.repository.findNested(temp.getLeftVal(), temp.getRightVal());
            List<TempAsChild> subordinates = fetchedSubordinates
                    .stream()
                    .map(TempAsChild::new)
                    .toList();
            List<JobAsChild> jobs = temp.getJobs()
                    .stream()
                    .map(JobAsChild::new)
                    .toList();
            return new TempWithSubsAndJobs(temp, subordinates, jobs);
        }
        return null;
    }


    // Creates a new temp in the DB and returns a representative DTO including any assigned jobs
    public TempWithJobs create(TempCreateData data) {
        int leftVal = 1;
        int rightVal = 2;
        Optional<Temp> fetchedManager = this.repository.findById(data.getManagerId());
        if (fetchedManager.isPresent()) {
            // Get position in nested table
            Temp manager = fetchedManager.get();
            leftVal = manager.getRightVal();
            rightVal = manager.getRightVal() + 1;
            // Update all temps in the table to create space for new temp in nested hierarchy
            this.repository.updateNestValues(manager.getRightVal());
        } else {
            // No manager for temp exists, will be inserted into table un-nested
             Optional<Integer> fetchedHighestRight = this.repository.findHighestRightVal();
             if (fetchedHighestRight.isPresent()) {
                 Integer highestRight = fetchedHighestRight.get();
                 leftVal = highestRight + 1;
                 rightVal = leftVal + 1;
             }
        }
        // Save the new temp to the db
        Temp temp = new Temp(data.getFirstName(), data.getLastName(), leftVal, rightVal);
        this.repository.save(temp);
        return createTempWithJobs(temp);
    }

    // Get all temps in DB, return as DTOs including assigned jobs
    public List<TempWithJobs> getAll() {
        List<Temp> fetchedTemps = this.repository.findAll();
        return fetchedTemps
                .stream()
                .map(this::createTempWithJobs)
                .toList();
    }

    // Returns a DTO of an individual temp including assigned jobs and subordinates, found by ID
    public TempWithSubsAndJobs getIndividual (Integer id) {
        Optional<Temp> fetchedTemp = this.repository.findById(id);
        return fetchedTemp.map(this::createTempWithSubsAndJobs).orElse(null);
    }

    // Recursively get direct children of a temp, building representation of a nested set
    public ArrayList<TempWithNestedSubs> getNestedChildren (Integer leftVal, Integer rightVal) {
        ArrayList<TempWithNestedSubs> children = new ArrayList<>();
        while (leftVal < rightVal) {
            Optional<Temp> fetchedTemp = this.repository.findByLeftVal(leftVal);
            if (fetchedTemp.isPresent()) {
                // Found a direct child
                Temp child = fetchedTemp.get();
                children.add(new TempWithNestedSubs(child, getNestedChildren(child.getLeftVal() + 1, child.getRightVal() - 1)));
                // Skip over this direct child's own children
                leftVal = child.getRightVal() + 1;
            } else {
                // Keep going until a direct child is found
                leftVal ++;
            }
        }
        return children;
    }

    // Get the full nested set hierarchy
    public List<TempWithNestedSubs> getHierarchy () {
        Optional<Integer> fetchedHighestRightVal = this.repository.findHighestRightVal();
        return fetchedHighestRightVal.map(i -> getNestedChildren(1, i)).orElse(null);
    }

    // Gets a list of the available temps for the time frame of the given job
    public ArrayList<TempWithJobs> getAvailable(Integer jobId) {
        // Get temps that are available for a job in specific time range
        ArrayList<TempWithJobs> availableTempDTOs = new ArrayList<>();
        Optional<Job> fetchedJob = this.jobRepository.findById(jobId);
        if (fetchedJob.isPresent()) {
            Job job = fetchedJob.get();
            List<Integer> availableIds = this.repository.findByDates(job.getStartDate(), job.getEndDate());
            for (Integer id : availableIds) {
                Optional<Temp> fetchedTemp = this.repository.findById(id);
                fetchedTemp.ifPresent(temp -> availableTempDTOs.add(createTempWithJobs(temp)));
            }
        }
        return availableTempDTOs;
    }
}
