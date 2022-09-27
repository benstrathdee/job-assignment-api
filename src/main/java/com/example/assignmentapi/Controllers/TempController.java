package com.example.assignmentapi.Controllers;

import com.example.assignmentapi.DTOs.TempCreateDTO;
import com.example.assignmentapi.Entities.Job;
import com.example.assignmentapi.Entities.Temp;
import com.example.assignmentapi.Services.JobService;
import com.example.assignmentapi.Services.TempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping(path="/temps")
public class TempController {
    @Autowired
    TempService service;
    @Autowired
    JobService jobService;


    // POST /temps
    @PostMapping
    public ResponseEntity<Temp> createTemp (@Valid @RequestBody TempCreateDTO data) {
        // Create a temp from data in request body
        Temp temp = this.service.create(data);
        return new ResponseEntity<>(temp, HttpStatus.CREATED);
    }

    // GET /temps
    // GET /temps?jobId={jobId}
    @GetMapping
    public ResponseEntity<ArrayList<Temp>> getTemps (@Valid @RequestParam(required = false) Integer jobId) {
        if (jobId != null) {
            // List temps that are available for the job with jobId based on the jobs date range
            Job job = this.jobService.getById(jobId);
            if (job != null) {
                ArrayList<Temp> availableTemps = this.service.getByDates(job.getStartDate(), job.getEndDate());
                return new ResponseEntity<>(availableTemps, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            // List all temps
            ArrayList<Temp> temps = this.service.all();
            return new ResponseEntity<>(temps, HttpStatus.OK);
        }
    }

    // GET /temps/{id}
    @GetMapping(path = "/{id}")
    public ResponseEntity<Temp> getTemp (@PathVariable Integer id) {
        // get temp by id
        Optional<Temp> fetchedTemp = this.service.getById(id);
        return new ResponseEntity<>(
                fetchedTemp.orElse(null),
                fetchedTemp.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }
}
