package com.example.assignmentapi.Services;

import com.example.assignmentapi.DTOs.TempCreateDTO;
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

    public Temp create(TempCreateDTO data) {
        Temp temp = new Temp(data.getFirstName(), data.getLastName());
        Temp returnedTemp = this.repository.save(temp);
        return returnedTemp;
//        return this.repository.save(temp);
    }

    public ArrayList<Temp> all() {
        return new ArrayList<>(this.repository.findAll());
    }

    public Temp getById(Integer id) {
        Optional<Temp> fetchedTemp = this.repository.findById(id);
        return fetchedTemp.orElse(null);
    }

    public ArrayList<Temp> getByDates(Long startDate, Long endDate) {
        ArrayList<Temp> availableTemps = new ArrayList<>();
        List<Integer> availableIds = this.repository.findByDates(startDate, endDate);
        for (Integer id : availableIds) {
            Optional<Temp> fetchedTemp = this.repository.findById(id);
            if (fetchedTemp.isPresent()) {
                Temp temp = fetchedTemp.get();
                availableTemps.add(temp);
            }
        }
        return availableTemps;
    }

    public ArrayList<Temp> getAvailable(Integer jobId) {
        ArrayList<Temp> availableTemps = new ArrayList<>();
        Optional<Job> fetchedJob = this.jobRepository.findById(jobId);
        if (fetchedJob.isPresent()) {
            Job job = fetchedJob.get();
            availableTemps.addAll(getByDates(job.getStartDate(), job.getEndDate()));
        }
        return availableTemps;
    }
}
