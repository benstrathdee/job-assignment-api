package com.example.assignmentapi.service;

import  com.example.assignmentapi.dto.temp.*;
import com.example.assignmentapi.entity.Job;
import com.example.assignmentapi.entity.Temp;
import com.example.assignmentapi.repository.JobRepository;
import com.example.assignmentapi.repository.TempRepository;
import com.example.assignmentapi.utilities.DTODirector;
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
    public TempReturnDTO createTemp(TempCreateData data) {
        Temp.TempBuilder builder = Temp.builder()
                .firstName(data.getFirstName())
                .lastName(data.getLastName());

        // Default nesting values, in case entry is first in DB
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
        // Finish building the temp
        Temp temp = builder.leftVal(leftVal).rightVal(rightVal).build();
        tempRepository.save(temp);
        return DTODirector.buildTempWithJobs(temp, temp.getJobs());
    }

    // Get all temps in DB, return as DTOs including assigned jobs
    public List<TempReturnDTO> getAllTemps() {
        List<Temp> fetchedTemps = tempRepository.findAll();
        return fetchedTemps
                .stream()
                .map(temp -> DTODirector.buildTempWithJobs(temp, temp.getJobs()))
                .toList();
    }

    // Returns a DTO of an individual temp including assigned jobs and subordinates, found by ID
    public TempReturnDTO getTempById(Integer id) {
        Optional<Temp> fetchedTemp = tempRepository.findById(id);
        if (fetchedTemp.isPresent()) {
            Temp temp = fetchedTemp.get();
            List<Temp> subordinates = tempRepository.findNested(temp.getLeftVal(), temp.getRightVal());

            return DTODirector.buildTempWithSubsAndJobs(temp, subordinates, temp.getJobs());
        }
        return null;
    }

    public List<TempReturnDTO> getNestedBetween (Integer leftVal, Integer rightVal) {
        List<TempReturnDTO> directSubordinates = new ArrayList<>();
        while (leftVal < rightVal) {
            Optional<Temp> fetchedTemp = tempRepository.findByLeftVal(leftVal);
            if (fetchedTemp.isPresent()) {
                // Found a direct child
                Temp child = fetchedTemp.get();
                directSubordinates.add(DTODirector.buildTempWithNestedSubs(child, getNestedBetween(child.getLeftVal() + 1, child.getRightVal())));

                // Skip over this direct child's own children so that they're not added to the parent
                leftVal = child.getRightVal() + 1;
            } else {
                // Keep going until a direct child is found
                leftVal++;
            }
        }
        return directSubordinates;
    }

    // Get the full nested set hierarchy
    public List<TempReturnDTO> getTempHierarchy() {
        List<TempReturnDTO> hierarchy = new ArrayList<>();
        Optional<Integer> fetchedHighestRightVal = tempRepository.findMaxRightVal();
        if (fetchedHighestRightVal.isPresent()) {
            // These values encapsulate the whole nested set
            Integer leftVal = 1;
            Integer highestRightVal = fetchedHighestRightVal.get();
            hierarchy.addAll(getNestedBetween(leftVal, highestRightVal));
        }
        return hierarchy;
    }

    // Gets a list of the available temps for the time frame of the given job
    public List<TempReturnDTO> getAvailable(Integer jobId) {
        // Get temps that are available for a job in specific time range
        List<TempReturnDTO> availableTempDTOs = new ArrayList<>();
        Optional<Job> fetchedJob = jobRepository.findById(jobId);
        if (fetchedJob.isPresent()) {
            Job job = fetchedJob.get();
            List<Integer> availableIds = tempRepository.findByDates(job.getStartDate(), job.getEndDate());
            for (Integer id : availableIds) {
                Optional<Temp> fetchedTemp = tempRepository.findById(id);
                if (fetchedTemp.isPresent()) {
                    Temp temp = fetchedTemp.get();
                    availableTempDTOs.add(DTODirector.buildTempWithJobs(temp, temp.getJobs()));
                }
            }
        }
        return availableTempDTOs;
    }
}
