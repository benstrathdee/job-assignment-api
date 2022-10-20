package com.example.assignmentapi.service;

import  com.example.assignmentapi.dto.temp.*;
import com.example.assignmentapi.entity.Job;
import com.example.assignmentapi.entity.Temp;
import com.example.assignmentapi.exceptionhandling.ResourceNotFoundException;
import com.example.assignmentapi.exceptionhandling.TempNotAvailableException;
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
        // Start building a temp from provided data
        Temp.TempBuilder builder = Temp.builder()
                .firstName(data.getFirstName())
                .lastName(data.getLastName());

        // Default nesting values, in case entry is first in DB
        int leftVal = 1;
        int rightVal = 2;

        // Attempt to find any manager that might've been specified in the data
        if (data.getManagerId() == null) {
            // No manager for temp was provided, will be inserted into table un-nested
            // Check if there are other temps in the DB already, if so new temp will be inserted after
            Optional<Integer> fetchedHighestRight = tempRepository.findMaxRightVal();
            if (fetchedHighestRight.isPresent()) {
                Integer highestRight = fetchedHighestRight.get();
                leftVal = highestRight + 1;
                rightVal = leftVal + 1;
            }
        } else {
            // A managerId was provided
            Optional<Temp> fetchedManager = tempRepository.findById(data.getManagerId());
            if (fetchedManager.isPresent()) {
                // Get position in nested table
                Temp manager = fetchedManager.get();
                leftVal = manager.getRightVal();
                rightVal = manager.getRightVal() + 1;

                // Update all temps in the table to create space for new temp in nested hierarchy
                tempRepository.updateNestValues(manager.getRightVal());
            } else {
                // Specified manager does not exist, send bad request
                throw new TempNotAvailableException("The manager specified could not be found");
            }
        }

        // Finish building the temp and return a representation to client
        Temp temp = builder
                .leftVal(leftVal)
                .rightVal(rightVal)
                .build();
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

        if (fetchedTemp.isEmpty()) {
            // Temp with this id does not exist, return 404
            throw new ResourceNotFoundException();
        }

        // Get temp and their subordinates
        Temp temp = fetchedTemp.get();
        List<Temp> subordinates = tempRepository.findNested(temp.getLeftVal(), temp.getRightVal());

        // Create representation of temp and send to client
        return DTODirector.buildTempWithSubsAndJobs(temp, subordinates, temp.getJobs());
    }

    public List<TempReturnDTO> getNestedBetween (Integer leftVal, Integer rightVal) {
        // Initialise an empty list in case there's no entries within these values
        List<TempReturnDTO> directSubordinates = new ArrayList<>();
        while (leftVal < rightVal) {
            Optional<Temp> fetchedTemp = tempRepository.findByLeftVal(leftVal);

            if (fetchedTemp.isPresent()) {
                // Found a direct child
                Temp temp = fetchedTemp.get();
                // Find the direct child's direct children (recursion!!)
                List<TempReturnDTO> childSubs = getNestedBetween(temp.getLeftVal() + 1, temp.getRightVal());

                // Create a representation of the child and add to the list
                TempReturnDTO child = DTODirector.buildTempWithNestedSubs(temp, childSubs);
                directSubordinates.add(child);

                // Skip over this direct child's own children so that they're not added incorrectly to the parent
                leftVal = temp.getRightVal() + 1;
            } else {
                // Keep going until a direct child is found
                leftVal++;
            }
        }
        return directSubordinates;
    }

    // Get the full nested set hierarchy
    public List<TempReturnDTO> getTempHierarchy() {
        // Initialise an empty list in case there's no entries in DB
        List<TempReturnDTO> hierarchy = new ArrayList<>();

        // Get the highest rightVal that exists in the db
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
        // Initialise an empty list in case there's no temps available
        List<TempReturnDTO> availableTempDTOs = new ArrayList<>();

        Optional<Job> fetchedJob = jobRepository.findById(jobId);

        if (fetchedJob.isEmpty()) {
            // Job with specified id doesn't exist, return 404
            throw new ResourceNotFoundException();
        }

        Job job = fetchedJob.get();
        List<Integer> availableIds = tempRepository.findByDates(job.getStartDate(), job.getEndDate());
        for (Integer id : availableIds) {
            Optional<Temp> fetchedTemp = tempRepository.findById(id);

            if (fetchedTemp.isPresent()) { // temp will always exist if it's in the loop
                // Create a representation of the temp and add to list
                Temp temp = fetchedTemp.get();
                availableTempDTOs.add(DTODirector.buildTempWithJobs(temp, temp.getJobs()));
            }
        }
        return availableTempDTOs;
    }
}
