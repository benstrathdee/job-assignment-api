package com.example.assignmentapi.service;

import com.example.assignmentapi.dto.temp.*;
import com.example.assignmentapi.entity.Job;
import com.example.assignmentapi.entity.Temp;
import com.example.assignmentapi.repository.JobRepository;
import com.example.assignmentapi.repository.TempRepository;
import com.example.assignmentapi.utilities.DTOBuilder;
import com.example.assignmentapi.utilities.TreeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class TempService {
    @Autowired
    TempRepository tempRepository;
    @Autowired
    JobRepository jobRepository;

    // Creates a new temp in the DB and returns a representative DTO including any assigned jobs
    public TempWithJobs createTemp(TempCreateData data) {
        // Default nesting values
        int leftVal = 1;
        int rightVal = 2;

        // Attempt to find any manager that might've been specified in the data
        Optional<Temp> fetchedManager = tempRepository.findById(data.getManagerId());
        if (fetchedManager.isPresent()) {
            // Get position in nested table
            Temp manager = fetchedManager.get();
            leftVal = manager.getRightVal();
            rightVal = manager.getRightVal() + 1;

            // Update all temps in the table to create space for new temp in nested hierarchy
            tempRepository.updateNestValues(manager.getRightVal());
        } else {
            // No manager for temp exists, will be inserted into table un-nested
             Optional<Integer> fetchedHighestRight = tempRepository.findMaxRightVal();
             if (fetchedHighestRight.isPresent()) {
                 Integer highestRight = fetchedHighestRight.get();
                 leftVal = highestRight + 1;
                 rightVal = leftVal + 1;
             }
        }
        // Save the new temp to the db
        Temp temp = new Temp(data.getFirstName(), data.getLastName(), leftVal, rightVal);
        tempRepository.save(temp);
        return DTOBuilder.buildTempWithJobs(temp);
    }

    // Get all temps in DB, return as DTOs including assigned jobs
    public List<TempWithJobs> getAllTemps() {
        List<Temp> fetchedTemps = tempRepository.findAll();
        return fetchedTemps
                .stream()
                .map(DTOBuilder::buildTempWithJobs)
                .toList();
    }

    // Returns a DTO of an individual temp including assigned jobs and subordinates, found by ID
    public TempWithSubsAndJobs getTempById(Integer id) {
        Optional<Temp> fetchedTemp = tempRepository.findById(id);
        if (fetchedTemp.isPresent()) {
            Temp temp = fetchedTemp.get();
            List<Temp> subordinates = tempRepository.findNested(temp.getLeftVal(), temp.getRightVal());

            return DTOBuilder.buildTempWithSubsAndJobs(temp, subordinates);
        }
        return null;
    }



    // Get the full nested set hierarchy
    public List<TempWithNestedSubs> getTempHierarchy() {
        List<TempWithNestedSubs> hierarchy = Collections.emptyList();
        Optional<Integer> fetchedHighestRightVal = tempRepository.findMaxRightVal();
        if (fetchedHighestRightVal.isPresent()) {
            Integer highestRightVal = fetchedHighestRightVal.get();
            hierarchy = TreeBuilder.buildTree(tempRepository, 1, highestRightVal);
        }
        return hierarchy;
    }

    // Gets a list of the available temps for the time frame of the given job
    public ArrayList<TempWithJobs> getAvailable(Integer jobId) {
        // Get temps that are available for a job in specific time range
        ArrayList<TempWithJobs> availableTempDTOs = new ArrayList<>();
        Optional<Job> fetchedJob = jobRepository.findById(jobId);
        if (fetchedJob.isPresent()) {
            Job job = fetchedJob.get();
            List<Integer> availableIds = tempRepository.findByDates(job.getStartDate(), job.getEndDate());
            for (Integer id : availableIds) {
                Optional<Temp> fetchedTemp = tempRepository.findById(id);
                if (fetchedTemp.isPresent()) {
                    Temp temp = fetchedTemp.get();
                    availableTempDTOs.add(DTOBuilder.buildTempWithJobs(temp));
                }
            }
        }
        return availableTempDTOs;
    }
}
