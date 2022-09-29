package com.example.assignmentapi.Services;

import com.example.assignmentapi.DTOs.Temp.TempCreateDTO;
import com.example.assignmentapi.DTOs.Temp.TempGetDTO;
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
public class TempService {
    @Autowired
    TempRepository repository;
    @Autowired
    JobRepository jobRepository;

    public TempGetDTO create(TempCreateDTO data) {
        // TODO
        // Integer managerId = data.getManagerId()
        // if manager. == null, assume temp is a manager (root), insert at latest point
        // Integer leftVal = ...
        // Integer rightVal = leftVal + 1
        // increase furthest right value by 2
        // else
        // Optional<Temp> fetchedManager = this.repository.findManager(managerID)
        // Temp manager = fetchedManager.get()
        // Integer managerLeftVal = manager.getRightVal()
        // Integer leftVal = managerRightVal - 2
        // Integer rightVal = managerRightVal - 1
        // get all with values to the right, increment all values by 2
        Temp tempEntity = new Temp(data.getFirstName(), data.getLastName() /*, leftVal, rightVal */);
        this.repository.save(tempEntity);
        return new TempGetDTO(tempEntity);
    }

    public ArrayList<TempGetDTO> all() {
        // Get all temps in DB
        ArrayList<Temp> allTempEntities = new ArrayList<>(this.repository.findAll());
        ArrayList<TempGetDTO> allTempDTOs = new ArrayList<>();
        // Create representations and send to client
        for (Temp temp : allTempEntities) {
            allTempDTOs.add(new TempGetDTO(temp));
        }
        return allTempDTOs;
    }

    public TempGetDTO getById(Integer id) {
        // Get specific temp by id
        Optional<Temp> fetchedTemp = this.repository.findById(id);
        // Send representation to client
        return fetchedTemp.map(TempGetDTO::new).orElse(null);
        // Doesn't exist, send 404
    }

    public ArrayList<TempGetDTO> getAvailable(Integer jobId) {
        // Get temps that are available for a job in specific time range
        ArrayList<TempGetDTO> availableTempDTOs = new ArrayList<>();
        // Check if specified job exists
        Optional<Job> fetchedJob = this.jobRepository.findById(jobId);
        if (fetchedJob.isPresent()) {
            Job job = fetchedJob.get();
            List<Integer> availableIds = this.repository.findByDates(job.getStartDate(), job.getEndDate());
            for (Integer id : availableIds) {
                // Get available temps by ID
                Optional<Temp> fetchedTemp = this.repository.findById(id);
                // Create representations to send to client
                fetchedTemp.ifPresent(temp -> availableTempDTOs.add(new TempGetDTO(temp)));
            }
        }
        return availableTempDTOs;
    }
}
