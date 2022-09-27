package com.example.assignmentapi.Services;

import com.example.assignmentapi.DTOs.TempCreateDTO;
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
        return this.repository.save(temp);
    }

    public ArrayList<Temp> all() {
        return new ArrayList<>(this.repository.findAll());
    }

    public Optional<Temp> getById(Integer id) {
        return this.repository.findById(id);
    }

    public ArrayList<Temp> getByDates(Integer startDate, Integer endDate) {
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
}
