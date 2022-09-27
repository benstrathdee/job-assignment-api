package com.example.assignmentapi.Controllers;

import com.example.assignmentapi.DTOs.JobCreateDTO;
import com.example.assignmentapi.DTOs.JobUpdateDTO;
import com.example.assignmentapi.Entities.Job;
import com.example.assignmentapi.Services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping(path="/jobs")
public class JobController {
    @Autowired
    JobService service;

    // POST /jobs
    @PostMapping
    public ResponseEntity<Job> createJob ( @Valid @RequestBody JobCreateDTO data ) {
        // Creates a job
        Job job = this.service.create(data);
        return new ResponseEntity<>(job, job != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    // PATCH /jobs/{id}
    @PatchMapping(path = "/{id}")
    public ResponseEntity<Job> updateJob ( @PathVariable Integer id, @Valid @RequestBody JobUpdateDTO data ) {
        // Updates job with id using data from request body
        Job job = this.service.update(id, data);
        return new ResponseEntity<>(job, job != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    // GET /jobs
    // GET /jobs?assigned={true|false}
    @GetMapping
    public ResponseEntity<ArrayList<Job>> getJobs ( @RequestParam(required = false) Boolean assigned ) {
        ArrayList<Job> jobs;
        if (assigned != null) {
            // Filter by whether a job is assigned to a temp or not
            jobs = this.service.getByAssigned(assigned);
        } else {
            // Fetch all jobs
            jobs = this.service.all();
        }
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }

    // GET /jobs/{id}
    @GetMapping(path = "/{id}")
    public ResponseEntity<Job> getJob ( @PathVariable Integer id ) {
        // get the job with id
        Job job = this.service.getById(id);
        return new ResponseEntity<>(job, job != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}